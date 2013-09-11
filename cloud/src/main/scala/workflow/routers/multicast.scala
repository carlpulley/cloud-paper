package cloud

package workflow

package routers

import akka.actor.ActorRef
import akka.camel.CamelMessage
import cloud.lib.RouterWorkflow
import cloud.lib.Workflow
import cloud.workflow.actors.AddHandlers
import cloud.workflow.actors.ControlEvent
import scala.concurrent.duration._
import org.apache.camel.CamelContext
import org.apache.camel.Exchange
import org.apache.camel.processor.aggregate.AbstractListAggregationStrategy
import org.apache.camel.model.RouteDefinition
import org.apache.camel.scala.dsl.builder.RouteBuilder
import scala.xml.XML
import scalikejdbc.DB
import scalikejdbc.SQLInterpolation._

case class AddWorkflow(workflow: Workflow) extends ControlEvent
case class RemoveWorkflow(routeId: String) extends ControlEvent

class ListAggregationStrategy extends AbstractListAggregationStrategy[String] {
  def getValue(exchange: Exchange): String = {
    exchange.getIn.getHeader("clientId").asInstanceOf[String]
  }
}

class Multicast(name: String, workflows: RouterWorkflow*)(implicit val timeout: Duration, implicit val controller: ActorRef, implicit val camel_context: CamelContext) extends RouterWorkflow {
  def endpointUri = "direct:%s".format(name)

  controller ! AddHandlers {
    case AddWorkflow(child) => {
      camel_context.addRouteDefinition(new RouteDefinition {
        from(s"jms:topic:$name").to(child.endpointUri)
      })
    }

    case RemoveWorkflow(routeId) => {
      camel_context.removeRoute(routeId)
    }
  }

  workflows.foreach(w => controller ! AddWorkflow(w))

  def enrichSubmission = { (exchange: Exchange) =>
    val student = exchange.getIn.getHeader("replyTo", classOf[String])
    val last5 = DB autoCommit { implicit session =>
      sql"""SELECT f.message
          FROM ${SubmissionTable.name} AS s 
          INNER JOIN ${FeedbackTable.name} AS f ON s.client_id = f.client_id
        WHERE s.student=$student LIMIT 5 ORDER BY DATETIME(s.created_at) DESC
      """.map(_.string("message")).list.apply()
    }

    (exchange.getIn.getBody, last5)
  }

  def mergeAggregatedFeedback = { (exchange: Exchange) =>
    val feedback_items = exchange.getIn.getBody(classOf[List[String]]).map(XML.loadString)
    val feedback = <feedback></feedback>.copy(child = feedback_items.flatMap(_.child))

    feedback.toString
  }

  val routes = Seq(new RouteBuilder {
    endpointUri ==> {
      transform(enrichSubmission)
      to("jms:queue:$name")
      errorHandler(deadLetterChannel("jms:queue:error"))
      aggregate(header("clientId"), new ListAggregationStrategy()).completionTimeout(timeout.toMillis)
      transform(mergeAggregatedFeedback)
    }
  })
}

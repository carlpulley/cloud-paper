package cloud

package workflow

package routers

import cloud.workflow.actors.ControlEvent
import cloud.lib.RouterWorkflow
import cloud.lib.Workflow
import scala.concurrent.duration._
import org.apache.camel.Exchange
import org.apache.camel.processor.aggregate.AbstractListAggregationStrategy
import org.apache.camel.scala.dsl.builder.RouteBuilder
import scalikejdbc.DB
import scalikejdbc.SQLInterpolation._

case class AddWorkflow(workflow: Workflow) extends ControlEvent
case class RemoveWorkflow(workflow: Workflow) extends ControlEvent

class ListAggregationStrategy extends AbstractListAggregationStrategy[String] {
  def getValue(exchange: Exchange): String = {
    exchange.getIn.getHeader("clientId").asInstanceOf[String]
  }
}

// TODO: want to extend control bus to allow dynamically adding/removing child workflows

class Multicast(name: String, children: Seq[RouterWorkflow], timeout: Duration = 3 seconds) extends RouterWorkflow {
  def endpointUri = "direct:%s".format(name)

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

  def routes = Seq(new RouteBuilder {
    endpointUri ==> {
      transform(enrichSubmission)
      to(children.map(_.endpointUri): _*)
      errorHandler(deadLetterChannel("jms:queue:error"))
      aggregate(header("clientId"), new ListAggregationStrategy()).completionTimeout(timeout.toMillis)
    }
  
    for(child <- children) {
      s"jms:topic:$name" ==> {
        to(child.endpointUri)
      }
    }
  }) ++ children.flatMap(_.routes)
}

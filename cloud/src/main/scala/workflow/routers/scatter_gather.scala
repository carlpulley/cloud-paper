// Copyright (C) 2013  Carl Pulley
// 
// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU General Public License
// as published by the Free Software Foundation; either version 2
// of the License, or (at your option) any later version.
// 
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.

package cloud

package workflow

package routers

import akka.actor.ActorRef
import akka.camel.CamelMessage
import cloud.lib.EndpointWorkflow
import cloud.lib.RouterWorkflow
import cloud.lib.Workflow
import cloud.workflow.actors.AddHandlers
import cloud.workflow.actors.ControlEvent
import cloud.workflow.endpoints.FeedbackTable
import cloud.workflow.endpoints.SubmissionTable
import scala.concurrent.duration.Duration
import org.apache.camel.CamelContext
import org.apache.camel.model.ModelCamelContext
import org.apache.camel.Exchange
import org.apache.camel.processor.aggregate.AggregationStrategy
import org.apache.camel.model.RouteDefinition
import org.apache.camel.scala.dsl.builder.RouteBuilder
import scala.collection.JavaConversions._
import scala.xml.XML
import scalikejdbc.DB
import scalikejdbc.SQLInterpolation._

case class AddWorkflow(workflow: Workflow) extends ControlEvent
case class RemoveWorkflow(uri: String) extends ControlEvent

class FeedbackAggregationStrategy extends AggregationStrategy {
  def aggregate(merged_exchange: Exchange, new_exchange: Exchange): Exchange = {
    if (merged_exchange == null) {
      new_exchange
    } else {
      val old_feedback = XML.loadString(merged_exchange.getIn.getBody(classOf[String]))
      val new_feedback = XML.loadString(new_exchange.getIn.getBody(classOf[String]))
      val merged_feedback = old_feedback.copy(child = old_feedback.child ++ new_feedback.child)
      merged_exchange.getIn.setBody(merged_feedback.toString)

      merged_exchange
    }
  }
}

class ScatterGather(name: String, workflows: RouterWorkflow*)(implicit val timeout: Duration, implicit val controller: ActorRef, implicit val camel_context: CamelContext) extends RouterWorkflow {
  def entryUri = s"direct:${name}-entry"

  def exitUri = s"direct:${name}-exit"

  controller ! AddHandlers {
    // TODO: need to ensure child.routes exit/join to aggregator route (i.e. jms:queue:aggregate.$name)
    case AddWorkflow(child: RouterWorkflow) => {
      camel_context.addRoutes(new RouteBuilder {
        from(s"jms:topic:$name").to(child.entryUri)
      })

      for(route <- child.routes) {
        camel_context.addRoutes(route)
        //camel_context.addRoutes(new RouteBuilder {
        //  ? ==> {
        //    to(s"jms:queue:aggregate.$name")
        //  }
        //})
      }
    }
    case AddWorkflow(child: EndpointWorkflow) => {
      camel_context.addRoutes(new RouteBuilder {
        from(s"jms:topic:$name").to(child.entryUri)
      })
    }

    // FIXME: also need to remove any of the child.routes workflows that may have been added!
    case RemoveWorkflow(uri) => {
      val matches = camel_context.asInstanceOf[ModelCamelContext].getRouteDefinitions().filter(rd => rd.toString.contains(s"From[jms:topic:$name] -> To[$uri]"))
      for(route <- matches) {
        camel_context.asInstanceOf[ModelCamelContext].removeRouteDefinition(route)
      }
    }
  }

  workflows.foreach(w => controller ! AddWorkflow(w))

  def enrichSubmission = { (exchange: Exchange) =>
    val student = exchange.getIn.getHeader("replyTo", classOf[String])
    val last5 = DB autoCommit { implicit session =>
      sql"""
        SELECT f.message
          FROM ${SubmissionTable.name} AS s 
          INNER JOIN ${FeedbackTable.name} AS f ON s.id = f.submission_id
        WHERE s.student=$student ORDER BY DATETIME(s.created_at) DESC LIMIT 5
      """.map(_.string("message")).list.apply()
    }

    (exchange.getIn.getBody, last5)
  }

  val routes = Seq(new RouteBuilder {
    entryUri ==> {
      transform(enrichSubmission)
      to(s"jms:topic:$name")
    }

    s"jms:queue:aggregate.$name" ==> {
      errorHandler(deadLetterChannel("jms:queue:error"))
      aggregate(header("breadcrumbId"), new FeedbackAggregationStrategy()).completionTimeout(timeout.toMillis).to(exitUri)
    }
  })
}

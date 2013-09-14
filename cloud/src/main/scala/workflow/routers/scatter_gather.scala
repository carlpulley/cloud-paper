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
import cloud.lib.Helpers
import cloud.lib.RouterWorkflow
import cloud.workflow.controller.AddHandlers
import cloud.workflow.controller.ControlEvent
import cloud.workflow.controller.FeedbackTable
import cloud.workflow.controller.SubmissionTable
import java.util.Date
import org.apache.camel.CamelContext
import org.apache.camel.model.ModelCamelContext
import org.apache.camel.Exchange
import org.apache.camel.processor.aggregate.AggregationStrategy
import org.apache.camel.model.RouteDefinition
import org.apache.camel.scala.dsl.builder.RouteBuilder
import org.apache.camel.scala.Preamble
import scala.collection.JavaConversions._
import scala.concurrent.duration.Duration
import scala.util.Random
import scala.xml.XML
import scalikejdbc.DB
import scalikejdbc.SQLInterpolation._

case class AddWorkflow(workflow: RouterWorkflow) extends ControlEvent
case class RemoveWorkflow(uri: String) extends ControlEvent

class FeedbackAggregationStrategy extends AggregationStrategy with Preamble {
  def aggregate(merged_exchange: Exchange, new_exchange: Exchange): Exchange = {
    if (merged_exchange == null) {
      new_exchange
    } else {
      val old_feedback = XML.loadString(merged_exchange.in[String])
      val new_feedback = XML.loadString(new_exchange.in[String])
      val merged_feedback = old_feedback.copy(child = old_feedback.child ++ new_feedback.child)
      merged_exchange.in = merged_feedback.toString

      merged_exchange
    }
  }
}

class ScatterGather(group: String, name: String, workflows: RouterWorkflow*)(implicit timeout: Duration, controller: ActorRef, camel_context: CamelContext) extends RouterWorkflow {
  entryUri = s"direct:$group-$name-scatter-gather-entry"
  exitUri = s"direct:$group-$name-scatter-gather-exit"

  val pubsub_entry = s"jms:topic:$group-$name"
  val pubsub_exit = s"jms:queue:$group-$name-aggregate"
  val pubsub_error = s"jms:queue:$group-error"

  controller ! AddHandlers {
    case AddWorkflow(child: RouterWorkflow) => {
      child.entryUri = pubsub_entry
      child.exitUri = pubsub_exit

      child.routes.foreach(camel_context.addRoutes(_))
    }

    // FIXME: also need to remove any of the child.routes workflows that may have been added!
    case RemoveWorkflow(uri) => {
      val matches = camel_context.asInstanceOf[ModelCamelContext].getRouteDefinitions().filter(rd => rd.toString.contains(s"From[$pubsub_entry] -> To[$uri]"))
      for(route <- matches) {
        camel_context.asInstanceOf[ModelCamelContext].removeRouteDefinition(route)
      }
    }
  }

  workflows.foreach(w => controller ! AddWorkflow(w))

  def enrichSubmission = { (exchange: Exchange) =>
    val student = exchange.in("replyTo")
    val last5 = DB autoCommit { implicit session =>
      sql"""
        SELECT f.message
          FROM ${SubmissionTable.name} AS s 
          INNER JOIN ${FeedbackTable.name} AS f ON s.id = f.submission_id
        WHERE 
          s.module=${group} 
          AND s.student=${student} 
        ORDER BY DATETIME(s.created_at) DESC 
        LIMIT 5
      """.map(_.string("message")).list.apply()
    }

    (exchange.in, last5)
  }

  val routes = Seq(new RouteBuilder {
    entryUri ==> {
      transform(enrichSubmission)
      to(pubsub_entry)
    }

    pubsub_exit ==> {
      errorHandler(deadLetterChannel(pubsub_error))

      aggregate(header("breadcrumbId"), new FeedbackAggregationStrategy()).completionTimeout(timeout.toMillis).to(exitUri)
    }
  })
}

object ScatterGather extends Helpers {
  private[this] val rand = new Random(new java.security.SecureRandom())

  def apply(workflows: RouterWorkflow*)(implicit group: String, timeout: Duration, controller: ActorRef, camel_context: CamelContext) = {
    val name = sha256(group+(new Date().getTime.toString)+rand.alphanumeric.take(64).mkString)

    new ScatterGather(group, name, workflows: _*)
  }
}

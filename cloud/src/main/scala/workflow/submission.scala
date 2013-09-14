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

import akka.actor.ActorRef
import cloud.lib.EndpointWorkflow
import cloud.lib.Helpers
import cloud.lib.RouterWorkflow
import cloud.workflow.controller.FeedbackTable
import cloud.workflow.controller.SubmissionTable
import com.typesafe.config._
import java.sql.Connection
import org.apache.camel.Exchange
import org.apache.camel.scala.dsl.builder.RouteBuilder
import scalikejdbc.DB
import scalikejdbc.SQLInterpolation._

class Submission(val group: String, controller: ActorRef, workflow: RouterWorkflow, endpoints: EndpointWorkflow*) extends EndpointWorkflow with Helpers {
  private[this] val config = getConfig(group)

  private[this] val mailFrom = config.getString("feedback.tutor")
  private[this] val subject  = config.getString("feedback.subject")
  private[this] val mailhost = config.getString("mail.host")
  private[this] val mailuser = config.getString("mail.user")
  private[this] val mailpw   = config.getString("mail.password")
  private[this] val webhost  = config.getString("web.host")
  private[this] val webuser  = config.getString("web.user")

  entryUri = s"jms:queue:$group-submission-entry"

  val msg_store = s"direct:$group-msg-store"

  def addSHA256Header = { (exchange: Exchange) =>
    val hash = sha256(exchange.in[String])
    exchange.getIn.setHeader("sha256", hash)
  }

  def submissionSQL: Exchange => Unit = { (exchange: Exchange) =>
    val message_id = exchange.in("breadcrumbId")
    val replyTo = exchange.in("replyTo")
    val body = exchange.in[String]
    DB autoCommit { implicit session =>
      sql"INSERT INTO ${SubmissionTable.name}(module, student, message_id, message, created_at) VALUES (${group}, ${replyTo}, ${message_id}, ${body}, DATETIME('now'))".update.apply()
    }
  }

  def feedbackSQL: Exchange => Unit = { (exchange: Exchange) =>
    val message_id = exchange.in("breadcrumbId")
    val sha256 = exchange.in("sha256")
    val body = exchange.in[String]
    DB autoCommit { implicit session =>
      sql"""
        INSERT INTO ${FeedbackTable.name}(submission_id, sha256, message, created_at) 
          SELECT id, ${sha256}, ${body}, DATETIME('now')
          FROM ${SubmissionTable.name}
          WHERE message_id = ${message_id}
      """.update.apply()
    }
  }

  val routes = Seq(new RouteBuilder {
    entryUri ==> {
      errorHandler(deadLetterChannel(error_channel))

      // Only process messages that have a replyTo header (i.e. the student's email address)
      when(_.in("replyTo") != null) {
        setHeader("table", SubmissionTable.name.value)
        wireTap(msg_store)
        to(workflow.entryUri)
      }
    }

    workflow.exitUri ==> {
      process(addSHA256Header)
      setHeader("table", FeedbackTable.name.value)
      wireTap(msg_store)
      to(endpoints.map(_.entryUri): _*)
    }

    msg_store ==> {
      choice {
        when (_.in("table") == SubmissionTable.name.value) {
          process(submissionSQL)
        }
        when (_.in("table") == FeedbackTable.name.value) {
          process(feedbackSQL)
        }
      }
    }
  }) ++ workflow.routes ++ endpoints.flatMap(_.routes)
}

object Submission {
  def apply(controller: ActorRef, workflow: RouterWorkflow, endpoints: EndpointWorkflow*)(implicit group: String) = {
    new Submission(group, controller, workflow, endpoints: _*)
  }
}

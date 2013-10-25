// Copyright (C) 2013  Carl Pulley
// 
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
// 
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with this program. If not, see <http://www.gnu.org/licenses/>.

package cloud.workflow

import akka.actor.ActorRef
import cloud.lib.Config
import cloud.lib.Workflow
import cloud.workflow.controller.FeedbackTable
import cloud.workflow.controller.SubmissionTable
import java.sql.SQLException
import scala.concurrent.duration._
import scalikejdbc.DB
import scalikejdbc.SQLInterpolation._
import scalaz._
import scalaz.camel.core._

class Submission(val controller: ActorRef, workflow: Conv.MessageRoute, endpoints: Conv.MessageRoute*)(implicit val group: String, val router: Router) extends Workflow {
  import Scalaz._

  protected[this] val config = Config(group)

  private[this] val mailFrom = "tutor@hud.ac.uk"
  private[this] val subject  = s"Assessment feedback for ${group.toUpperCase}"
  private[this] val mailhost = config.get[String]("mail.host")
  private[this] val mailuser = config.get[String]("mail.user")
  private[this] val mailpw   = config.get[String]("mail.password")
  private[this] val webhost  = config.get[String]("web.host")
  private[this] val webuser  = config.get[String]("web.user")

  val uri = s"jms:queue:$group-submission-entry"
  val msg_store = s"direct:$group-msg-store"
  val error_channel = s"jms:queue:$group-error"

  val submissionSQL = { (msg: Message) =>
    val message_id = msg.header("breadcrumbId")
    val replyTo = msg.header("replyTo")
    val body = msg.bodyAs[String]
    DB autoCommit { implicit session =>
      val num_rows = sql"INSERT INTO ${SubmissionTable.name}(module, student, message_id, message, created_at) VALUES (${group}, ${replyTo}, ${message_id}, ${body}, DATETIME('now'))".update.apply()

      if (num_rows != 1) {
        throw new Exception(s"Failed to insert submission into ${SubmissionTable.name.value} table - $num_rows changed")
      }
    }
    msg
  }

  val feedbackSQL = { (msg: Message) =>
    val message_id = msg.header("breadcrumbId")
    val sha256 = msg.header("sha256")
    val body = msg.bodyAs[String]
    DB autoCommit { implicit session =>
      val num_rows = sql"""
        INSERT INTO ${FeedbackTable.name}(submission_id, sha256, message, created_at) 
          SELECT id, ${sha256}, ${body}, DATETIME('now')
          FROM ${SubmissionTable.name}
          WHERE message_id = ${message_id}
      """.update.apply()

      if(num_rows != 1) {
        throw new Exception(s"Failed to insert feedback into ${FeedbackTable.name.value} table - $num_rows changed")
      }
    }
    msg
  }

  from(uri) {
    // Only process messages that have a replyTo header (i.e. the student's email address)
    attempt {
      oneway >=>
      choose {
        case Message(_, hdrs) if (hdrs.lift("replyTo").isDefined) => {
          { msg: Message => msg.addHeader("table" -> SubmissionTable.name.value) } >=>
          multicast(
            to(msg_store), 
            workflow >=> 
              to(s"validator:${group}/feedback.xsd") >=>
              ((msg: Message) => msg.setHeaders(Map("sha256" -> sha256(msg.bodyAs[String]), "table" -> FeedbackTable.name.value, "breadcrumbId" -> msg.headerAs[String]("breadcrumbId").get))) >=>
              multicast((messageRoute(to(msg_store)) +: endpoints.toSeq): _*)
          )
        }
        case _ =>
          { msg: Message => throw new Exception("Invalid message received") }
      }
    } fallback {
        case exn: Exception => to(error_channel) >=> failWith(exn)
    }
  }

  val retries = 5

  from(msg_store) {
    // SQLite DB insertion can fail (due to locking), so allow a number of attempts at inserting before we permanently fail
    attempt(retries) {
      oneway >=>
      choose {
        case Message(_, hdrs) if (hdrs("table") == SubmissionTable.name.value) => 
          submissionSQL
        case Message(_, hdrs) if (hdrs("table") == FeedbackTable.name.value) =>
          feedbackSQL
        case _ => 
          { msg: Message => throw new Exception("Invalid message received") }
      }
    } fallback {
        // After 2 failures, delay original message and retry
        case (exn: Exception, s: RetryState) if (retries >= s.count && s.count > retries - 2) => retry(s)
        case (exn: Exception, s: RetryState) if (retries - 2 >= s.count && s.count > 0) => orig(s) >=> delay(10.seconds) >=> retry(s)
        // If all else fails, send original message to the error channel
        case (exn: Exception, s: RetryState) if (s.count == 0) => orig(s) >=> to(error_channel) >=> failWith(exn)
    }
  }
}

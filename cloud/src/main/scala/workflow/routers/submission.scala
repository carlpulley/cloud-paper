package cloud

package workflow

package routers

import cloud.lib.EndpointWorkflow
import cloud.lib.Helpers
import cloud.lib.RouterWorkflow
import cloud.lib.SQLTable
import com.typesafe.config._
import java.sql.Connection
import org.apache.camel.Exchange
import org.apache.camel.scala.dsl.builder.RouteBuilder
import scalikejdbc.DB
import scalikejdbc.SQLInterpolation._

object SubmissionTable extends SQLTable {
  val name = sqls"submission"

  val columns = Map(
    "id" -> "INTEGER PRIMARY KEY",
    "message_id" -> "TEXT NOT NULL",
    "student" -> "TEXT NOT NULL", 
    "message" -> "TEXT NOT NULL", 
    "created_at" -> "TEXT NOT NULL"
  )

  val constraints = Seq(
    sqls"UNIQUE (message_id)"
  )
}

object FeedbackTable extends SQLTable {
  val name = sqls"feedback"

  val columns = Map(
    "id" -> "INTEGER PRIMARY KEY", 
    "submission_id" -> "INTEGER NOT NULL", 
    "sha256" -> "TEXT NOT NULL", 
    "message" -> "TEXT NOT NULL", 
    "created_at" -> "TEXT NOT NULL"
  )

  val constraints = Seq(
    sqls"FOREIGN KEY (submission_id) REFERENCES ${SubmissionTable.name}(id)"
  )
}

class Submission(workflow: RouterWorkflow, endpoints: EndpointWorkflow*) extends RouterWorkflow with Helpers {
  private[this] val config: Config = ConfigFactory.load("application.conf")

  private[this] val mailFrom = config.getString("feedback.tutor")
  private[this] val subject  = config.getString("feedback.subject")
  private[this] val mailhost = config.getString("mail.host")
  private[this] val mailuser = config.getString("mail.user")
  private[this] val mailpw   = config.getString("mail.password")
  private[this] val webhost  = config.getString("web.host")
  private[this] val webuser  = config.getString("web.user")

  def endpointUri = "direct:submission"

  def addSHA256Header = { (exchange: Exchange) =>
    val hash = sha256(exchange.getIn.getBody(classOf[String]))
    exchange.getIn.setHeader("sha256", hash)
  }

  def submissionSQL: Exchange => Unit = { (exchange: Exchange) =>
    val message_id = exchange.getIn.getHeader("breadcrumbId", classOf[String])
    val replyTo = exchange.getIn.getHeader("replyTo", classOf[String])
    val body = exchange.getIn.getBody(classOf[String])
    DB autoCommit { implicit session =>
      sql"INSERT INTO ${SubmissionTable.name}(student, message_id, message, created_at) VALUES (${replyTo}, ${message_id}, ${body}, DATETIME('now'))".update.apply()
    }
  }

  def feedbackSQL: Exchange => Unit = { (exchange: Exchange) =>
    val message_id = exchange.getIn.getHeader("breadcrumbId", classOf[String])
    val sha256 = exchange.getIn.getHeader("sha256", classOf[String])
    val body = exchange.getIn.getBody(classOf[String])
    DB autoCommit { implicit session =>
      sql"""
        INSERT INTO ${FeedbackTable.name}(submission_id, sha256, message, created_at) 
          SELECT id, ${sha256}, ${body}, DATETIME('now')
          FROM ${SubmissionTable.name}
          WHERE message_id = ${message_id}
      """.update.apply()
    }
  }

  // Submission workflow
  //
  // NOTES:
  //   1. we assume that incoming messages already have a replyTo header (the student's email address)
  def routes = Seq(new RouteBuilder {
    // Route 0
    endpointUri ==> {
      setHeader("table", "submission")
      wireTap("direct:msg_store")
      to(workflow.endpointUri)
      process(addSHA256Header)
      setHeader("table", "feedback")
      wireTap("direct:msg_store")
      to(endpoints.map(_.endpointUri): _*)
    }

    // Route 1: Message store
    "direct:msg_store" ==> {
      choice {
        when (_.in("table") == "submission") {
          process(submissionSQL)
        }
        when (_.in("table") == "feedback") {
          process(feedbackSQL)
        }
      }
    }
  }) ++ workflow.routes ++ endpoints.flatMap(_.routes)
}

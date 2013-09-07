package cloud

package workflow

package wrapper

import cloud.lib.Helpers
import cloud.lib.Workflow
import com.github.tototoshi.slick.JodaSupport._
import com.typesafe.config._
import java.sql.Timestamp
import org.apache.camel.Exchange
import org.apache.camel.scala.dsl.builder.RouteBuilder
import org.joda.time.DateTime
import scala.slick.driver.SQLiteDriver.simple._
import scala.slick.session.Session

object SubmissionTable extends Table[(Int, Int, String, String, DateTime)]("SUBMISSION") {
  def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
  def client_id = column[Int]("CLIENT_ID", O.NotNull)
  def student = column[String]("STUDENT", O.NotNull)
  def message = column[String]("MESSAGE")
  def created_at = column[DateTime]("CREATED_AT", O.NotNull)
  def * = id ~ client_id ~ student ~ message ~ created_at
}

object FeedbackTable extends Table[(Int, Int, String, String, DateTime)]("FEEDBACK") {
  def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
  def client_id = column[Int]("CLIENT_ID", O.NotNull)
  def sha256 = column[String]("SHA256", O.NotNull)
  def message = column[String]("MESSAGE")
  def created_at = column[DateTime]("CREATED_AT", O.NotNull)
  def * = id ~ client_id ~ sha256 ~ message ~ created_at
}

class Submission(db: Database, workflow: Workflow)(implicit val session: Session) extends Workflow with Helpers {
  private[this] val config: Config = ConfigFactory.load("application.conf")

  private[this] val mailFrom = config.getString("feedback.tutor")
  private[this] val subject  = config.getString("feedback.subject")
  private[this] val mailhost = config.getString("mail.host")
  private[this] val mailuser = config.getString("mail.user")
  private[this] val mailpw   = config.getString("mail.password")
  private[this] val webhost  = config.getString("web.host")
  private[this] val webuser  = config.getString("web.user")

  def addSHA256Header = { (exchange: Exchange) =>
    val hash = sha256(exchange.getIn.getBody.asInstanceOf[String])
    exchange.getIn.setHeader("sha256", hash)
  }

  def insertSubmission: Exchange => Unit = { (exchange: Exchange) =>
    db withSession {
      val client_id = exchange.getIn.getHeader("clientId").asInstanceOf[String].toInt
      val replyTo = exchange.getIn.getHeader("replyTo").asInstanceOf[String]
      val body = exchange.getIn.getBody.asInstanceOf[String]
      val created_at = new DateTime
      (SubmissionTable.client_id ~ SubmissionTable.student ~ SubmissionTable.message ~ SubmissionTable.created_at).insert((client_id, replyTo, body, created_at))
    }
  }

  def insertFeedback: Exchange => Unit = { (exchange: Exchange) =>
    db withSession {
      val client_id = exchange.getIn.getHeader("clientId").asInstanceOf[String].toInt
      val sha256 = exchange.getIn.getHeader("sha256").asInstanceOf[String]
      val body = exchange.getIn.getBody.asInstanceOf[String]
      val created_at = new DateTime
      (FeedbackTable.client_id ~ FeedbackTable.sha256 ~ FeedbackTable.message ~ FeedbackTable.created_at).insert((client_id, sha256, body, created_at))
    }
  }

  // Submission workflow
  //
  // NOTES:
  //   1. we assume that incoming messages already have a replyTo header (the student's email address)
  //   2. for durable jms mailboxes, we assume that a unique clientId header (this will be used as a submissionId) and
  //      a durableSubscriptionName header have been set
  def route = new RouteBuilder {
    // Route 0
    "direct:submission" ==> {
      setHeader("table", "submission")
      wireTap("direct:msg_store")
      workflow.route
      process(addSHA256Header)
      setHeader("table", "feedback")
      wireTap("direct:msg_store")
      to("direct:mail_endpoint", "direct:web_endpoint")
    }

    // Route 1: Message store
    "direct:msg_store" ==> {
      choice {
        when (_.in("table") == "submission") {
          process(insertSubmission)
        }
        when (_.in("table") == "feedback") {
          process(insertFeedback)
        }
      }
    }

    // Route 2: Email https link        
    "direct:mail_endpoint" ==> {
        // Here we send a template email containing a URL link to the actual assessment feedback
        setHeader("webuser", webuser)
        setHeader("webhost", webhost)
        to("velocity:feedback-email.vm")
        setHeader("username", mailuser)
        setHeader("password", mailpw)
        setHeader("from", mailFrom)
        setHeader("to", header("replyTo"))
        setHeader("subject", subject)
        to("smtp:%s".format(mailhost))
    }

    // Route 3: Web server interface
    //
    // NOTES:
    //   1. we assume that SSH certificates have been setup to allow passwordless login to $webuser@$webhost
    //   2. we also assume that Apache (or similar) can serve pages from ~$webuser/www/$crypto_link via the 
    //      URL https://$webhost/$webuser/$crypto_link
    //   3. here the message body contains the $crypto_link file contents (which are assumed to be text/HTML)
    "direct:web_endpoint" ==> {
        setHeader("student", header("replyTo"))
        setHeader("title", subject)
        to("velocity:feedback-file.vm")
        setHeader("CamelFileName", header("sha256"))
        to("sftp:%s@%s/www/".format(webuser, webhost))
    }
  }
}

package cloud.workflow

package test

import cloud.lib.Helpers
import cloud.workflow.wrapper.FeedbackTable
import cloud.workflow.wrapper.SubmissionTable
import com.typesafe.config._
import com.github.tototoshi.slick.JodaSupport._
import java.security.MessageDigest
import org.apache.activemq.ActiveMQConnectionFactory
import org.apache.camel.builder.AdviceWithRouteBuilder
import org.apache.camel.component.jms.JmsComponent
import org.apache.camel.scala.dsl.builder.RouteBuilder
import org.apache.camel.scala.dsl.builder.RouteBuilderSupport
import org.apache.camel.test.junit4.CamelTestSupport
import org.joda.time.DateTime
import org.scalatest.BeforeAndAfter
import org.scalatest.FunSuiteLike
import scala.collection.JavaConversions._
import scala.slick.driver.SQLiteDriver.simple._
import scala.slick.session.Session

trait ScalaTestSupport extends CamelTestSupport with RouteBuilderSupport with FunSuiteLike with BeforeAndAfter {
  val builder: RouteBuilder

  override protected def createRouteBuilder = builder

  override protected def getMockEndpoint(uri: String) = super.getMockEndpoint(uri)

  override protected def assertMockEndpointsSatisfied() = super.assertMockEndpointsSatisfied()
}

class SubmissionTests extends ScalaTestSupport with Helpers {

  val db = Database.forURL("jdbc:sqlite::memory:", driver = "org.sqlite.JDBC")
  implicit val session: Session = db.createSession

  val builder = new wrapper.Submission(db, new Logging("TEST", showAll=true))(session).route

  val config: Config = ConfigFactory.load("application.conf")

  val mailFrom = config.getString("feedback.tutor")
  val mailTo   = "student@hud.ac.uk"
  val subject  = config.getString("feedback.subject")
  val mailhost = config.getString("mail.host")
  val mailuser = config.getString("mail.user")
  val mailpw   = config.getString("mail.password")
  val webhost  = config.getString("web.host")
  val webuser  = config.getString("web.user")

  before {
    db withSession {
      (SubmissionTable.ddl ++ FeedbackTable.ddl).create
    }
    setUp
    context.setTracing(true)
    //val connectionFactory = new ActiveMQConnectionFactory("vm://localhost?broker.persistent=false")
    //context.addComponent("jms", JmsComponent.jmsComponentAutoAcknowledge(connectionFactory))
  }

  after {
    tearDown
    db withSession {
      (SubmissionTable.ddl ++ FeedbackTable.ddl).drop
    }
  }

  test("Check submission route") {
    context.getRouteDefinitions.get(0).adviceWith(context, new AdviceWithRouteBuilder {
      def configure = {
        weaveByToString("To[direct:mail_endpoint]").replace.to("mock:mail_endpoint")
        weaveByToString("To[direct:web_endpoint]").replace.to("mock:web_endpoint")
      }
    })
    val submission = "Dummy Submission"
    val hash = sha256(submission)

    val mock_mail = getMockEndpoint("mock:mail_endpoint")
    mock_mail.expectedMessageCount(1)
    mock_mail.expectedBodiesReceived(submission)
    mock_mail.expectedHeaderReceived("clientId", "1")
    mock_mail.expectedHeaderReceived("durableSubscriptionName", "submission")
    mock_mail.expectedHeaderReceived("replyTo", "student@hud.ac.uk")
    mock_mail.expectedHeaderReceived("sha256", hash)

    val mock_web = getMockEndpoint("mock:web_endpoint")
    mock_web.expectedMessageCount(1)
    mock_web.expectedBodiesReceived(submission)
    mock_web.expectedHeaderReceived("clientId", "1")
    mock_web.expectedHeaderReceived("durableSubscriptionName", "submission")
    mock_web.expectedHeaderReceived("replyTo", "student@hud.ac.uk")
    mock_web.expectedHeaderReceived("sha256", hash)

    template().sendBodyAndHeaders("direct:submission", submission, Map("replyTo" -> mailTo, "clientId" -> "1", "durableSubscriptionName" -> "submission"))

    assertMockEndpointsSatisfied
  }

  test("Check mail route") {
    context.getRouteDefinitions.get(2).adviceWith(context, new AdviceWithRouteBuilder {
      def configure = {
        weaveByToString("To[smtp:%s]".format(mailhost)).replace.to("log:DEBUG-smtp?showAll=true", "mock:smtp")
      }
    })
    val feedback = "Dummy Feedback"
    val hash = sha256(feedback)

    val mock_mail = getMockEndpoint("mock:smtp")
    mock_mail.expectedMessageCount(1)
    mock_mail.message(0).body.contains("https://%s/%s/%s".format(webhost, webuser, hash))
    mock_mail.expectedHeaderReceived("replyTo", mailTo)
    mock_mail.expectedHeaderReceived("sha256", hash)
    mock_mail.expectedHeaderReceived("webuser", webuser)
    mock_mail.expectedHeaderReceived("webhost", webhost)
    mock_mail.expectedHeaderReceived("username", mailuser)
    mock_mail.expectedHeaderReceived("password", mailpw)
    mock_mail.expectedHeaderReceived("from", mailFrom)
    mock_mail.expectedHeaderReceived("to", mailTo)
    mock_mail.expectedHeaderReceived("subject", subject)

    template().sendBodyAndHeaders("direct:mail_endpoint", feedback, Map("replyTo" -> mailTo, "sha256" -> hash))

    assertMockEndpointsSatisfied
  }

  test("Check web route") {
    context.getRouteDefinitions.get(3).adviceWith(context, new AdviceWithRouteBuilder {
      def configure = {
        weaveByToString("To[sftp:%s@%s/www/]".format(webuser, webhost)).replace.to("mock:sftp")
      }
    })
    val feedback = "Dummy Feedback"
    val hash = sha256(feedback)

    val mock_web = getMockEndpoint("mock:sftp")
    mock_web.expectedMessageCount(1)
    mock_web.message(0).body.contains(feedback)
    mock_web.expectedHeaderReceived("replyTo", mailTo)
    mock_web.expectedHeaderReceived("sha256", hash)
    mock_web.expectedHeaderReceived("student", mailTo)
    mock_web.expectedHeaderReceived("title", subject)
    mock_web.expectedHeaderReceived("CamelFileName", hash)

    template().sendBodyAndHeaders("direct:web_endpoint", feedback, Map("replyTo" -> mailTo, "sha256" -> hash))

    assertMockEndpointsSatisfied 
  }

  test("Check message store route") {
    val submission = "Dummy Submission"
    val submission_hash = sha256(submission)
    val feedback = "Dummy Feedback"
    val feedback_hash = sha256(feedback)

    db withSession {
      template().sendBodyAndHeaders("direct:msg_store", submission, Map("table" -> "submission", "clientId" -> "1", "replyTo" -> mailTo, "sha256" ->  submission_hash))
      assert(Query(SubmissionTable.length).first == 1)
      assert(Query(FeedbackTable.length).first == 0)
      val (submission_client_id, student, submission_message) = (for { row <- SubmissionTable } yield (row.client_id, row.student, row.message)).first
      assert(submission_client_id == 1)
      assert(student == mailTo)
      assert(submission_message == submission)
  
      template().sendBodyAndHeaders("direct:msg_store", feedback, Map("table" -> "feedback", "clientId" -> "1", "replyTo" -> mailTo, "sha256" ->  feedback_hash))
      assert(Query(SubmissionTable.length).first == 1)
      assert(Query(FeedbackTable.length).first == 1)
      val (feedback_client_id, sha256, feedback_message) = (for { row <- FeedbackTable } yield (row.client_id, row.sha256, row.message)).first
      assert(feedback_client_id == 1)
      assert(sha256 == feedback_hash)
      assert(feedback_message == feedback)
    }
  }
}

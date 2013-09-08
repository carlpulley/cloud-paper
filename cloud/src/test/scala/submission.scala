package cloud.workflow

package test

import akka.actor.Props
import akka.testkit.TestActorRef
import cloud.lib.Helpers
import cloud.workflow.wrapper.SubmissionTable
import cloud.workflow.wrapper.FeedbackTable
import com.typesafe.config._
import java.security.MessageDigest
import java.sql.DriverManager
import org.apache.activemq.ActiveMQConnectionFactory
import org.apache.camel.builder.AdviceWithRouteBuilder
import org.apache.camel.component.jms.JmsComponent
import org.apache.camel.scala.dsl.builder.RouteBuilder
import org.apache.camel.scala.dsl.builder.RouteBuilderSupport
import org.apache.camel.test.junit4.CamelTestSupport
import org.scalatest.BeforeAndAfter
import org.scalatest.FunSuiteLike
import scala.collection.JavaConversions._
import scalikejdbc.AutoSession
import scalikejdbc.ConnectionPool
import scalikejdbc.DB
import scalikejdbc.SQLInterpolation._

trait ScalaTestSupport extends CamelTestSupport with RouteBuilderSupport with FunSuiteLike with BeforeAndAfter {
  val builders: Seq[RouteBuilder]

  override protected def createRouteBuilders = builders.map(scalaToJavaBuilder _).toArray

  override protected def getMockEndpoint(uri: String) = super.getMockEndpoint(uri)

  override protected def assertMockEndpointsSatisfied() = super.assertMockEndpointsSatisfied()
}

class SubmissionTests extends ScalaTestSupport with Helpers {
  val config: Config = ConfigFactory.load("application.conf")

  val mailFrom  = config.getString("feedback.tutor")
  val mailTo    = "student@hud.ac.uk"
  val subject   = config.getString("feedback.subject")
  val sqldriver = config.getString("sql.driver")
  val sqlurl    = config.getString("sql.url")
  val sqluser   = config.getString("sql.user")
  val sqlpw     = config.getString("sql.password")
  val mailhost  = config.getString("mail.host")
  val mailuser  = config.getString("mail.user")
  val mailpw    = config.getString("mail.password")
  val webhost   = config.getString("web.host")
  val webuser   = config.getString("web.user")

  Class.forName(sqldriver)
  ConnectionPool.singleton(sqlurl, sqluser, sqlpw)
  implicit val session = AutoSession

  val builders = new wrapper.Submission(SimpleFeedback).routes

  before {
    setUp
    context.setTracing(true)
    DB autoCommit { implicit session =>
      SubmissionTable.create
      FeedbackTable.create
    }
    // FIXME: is this needed?
    //val connectionFactory = new ActiveMQConnectionFactory("vm://localhost?broker.persistent=false")
    //context.addComponent("jms", JmsComponent.jmsComponentAutoAcknowledge(connectionFactory))
  }

  after {
    tearDown
    DB autoCommit { implicit session =>
      SubmissionTable.drop
      FeedbackTable.drop
    }
  }

  test("Check submission route") {
    context.getRouteDefinitions.get(0).adviceWith(context, new AdviceWithRouteBuilder {
      def configure = {
        weaveByToString("To[direct:mail_endpoint]").replace.to("mock:mail_endpoint")
        weaveByToString("To[direct:web_endpoint]").replace.to("mock:web_endpoint")
        weaveByToString("WireTap[direct:msg_store]").replace.to("mock:wiretap")
      }
    })
    val submission = "Dummy Submission"
    val feedback = "<feedback><item id='1'><comment>Dummy Feedback</comment></item></feedback>"
    val hash = sha256(feedback)

    val mock_mail = getMockEndpoint("mock:mail_endpoint")
    mock_mail.expectedMessageCount(1)
    mock_mail.expectedBodiesReceived(feedback)
    mock_mail.expectedHeaderReceived("clientId", "1")
    mock_mail.expectedHeaderReceived("durableSubscriptionName", "submission")
    mock_mail.expectedHeaderReceived("replyTo", "student@hud.ac.uk")
    mock_mail.expectedHeaderReceived("sha256", hash)

    val mock_web = getMockEndpoint("mock:web_endpoint")
    mock_web.expectedMessageCount(1)
    mock_web.expectedBodiesReceived(feedback)
    mock_web.expectedHeaderReceived("clientId", "1")
    mock_web.expectedHeaderReceived("durableSubscriptionName", "submission")
    mock_web.expectedHeaderReceived("replyTo", "student@hud.ac.uk")
    mock_web.expectedHeaderReceived("sha256", hash)

    val mock_tap = getMockEndpoint("mock:wiretap")
    mock_tap.expectedMessageCount(2)
    mock_tap.message(0).body.contains(submission)
    mock_tap.message(0).header("table").isEqualTo("submission")
    mock_tap.message(1).body.contains(feedback)
    mock_tap.message(1).header("table").isEqualTo("feedback")
    mock_tap.message(1).header("sha256").isEqualTo(hash)

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
    val feedback = <feedback><item id="42"><comment>Dummy Feedback</comment></item></feedback>
    val html_feedback = <li>Dummy Feedback</li>
    val hash = sha256(feedback.toString)

    val mock_web = getMockEndpoint("mock:sftp")
    mock_web.expectedMessageCount(1)
    mock_web.message(0).body.contains(html_feedback.toString)
    mock_web.expectedHeaderReceived("replyTo", mailTo)
    mock_web.expectedHeaderReceived("sha256", hash)
    mock_web.expectedHeaderReceived("student", mailTo)
    mock_web.expectedHeaderReceived("title", subject)
    mock_web.expectedHeaderReceived("CamelFileName", hash)

    template().sendBodyAndHeaders("direct:web_endpoint", feedback.toString, Map("replyTo" -> mailTo, "sha256" -> hash))

    assertMockEndpointsSatisfied 
  }

  test("Check message store route") {
    val submission = "Dummy Submission"
    val submission_hash = sha256(submission)
    val feedback = "Dummy Feedback"
    val feedback_hash = sha256(feedback)

    DB autoCommit { implicit session =>
      template().sendBodyAndHeaders("direct:msg_store", submission, Map("table" -> "submission", "clientId" -> "1", "replyTo" -> mailTo, "sha256" -> submission_hash))

      val submission_count1 = sql"SELECT COUNT(*) FROM ${SubmissionTable.name}".map(_.int(1)).single.apply().get
      val feedback_count1 = sql"SELECT COUNT(*) FROM ${FeedbackTable.name}".map(_.int(1)).single.apply().get
      val (submission_client_id, submission_student, submission_message) = sql"SELECT client_id, student, message FROM ${SubmissionTable.name}".map(rs => (rs.int("client_id"), rs.string("student"), rs.string("message"))).single.apply().get

      assert(submission_count1 == 1)
      assert(feedback_count1 == 0)
      assert(submission_client_id == 1)
      assert(submission_student == mailTo)
      assert(submission_message == submission)
  
      template().sendBodyAndHeaders("direct:msg_store", feedback, Map("table" -> "feedback", "clientId" -> "1", "replyTo" -> mailTo, "sha256" ->   feedback_hash))

      val submission_count2 = sql"SELECT COUNT(*) FROM ${SubmissionTable.name}".map(_.int(1)).single.apply().get
      val feedback_count2 = sql"SELECT COUNT(*) FROM ${FeedbackTable.name}".map(_.int(1)).single.apply().get
      val (feedback_client_id, feedback_sha256, feedback_message) = sql"SELECT client_id, sha256, message FROM ${FeedbackTable.name}".map(rs => (rs.int("client_id"), rs.string("student"), rs.string("message"))).single.apply().get
  
      assert(submission_count2 == 1)
      assert(feedback_count2 == 1)
      assert(feedback_client_id == 1)
      assert(feedback_sha256 == feedback_hash)
      assert(feedback_message == feedback)
    }
  }
}

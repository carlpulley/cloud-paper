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

package cloud.workflow

package test

import akka.actor.ActorSystem
import akka.camel.CamelExtension
import akka.actor.Props
import akka.testkit.TestActorRef
import cloud.lib.Helpers
import cloud.workflow.endpoints.FeedbackTable
import cloud.workflow.endpoints.HTTP
import cloud.workflow.endpoints.SMTP
import cloud.workflow.endpoints.Submission
import cloud.workflow.endpoints.SubmissionTable
import com.typesafe.config._
import org.apache.activemq.ActiveMQConnectionFactory
import org.apache.camel.builder.AdviceWithRouteBuilder
import org.apache.camel.CamelContext
import org.apache.camel.component.jms.JmsComponent
import scala.collection.JavaConversions._
import scalikejdbc.ConnectionPool
import scalikejdbc.DB
import scalikejdbc.SQLInterpolation._

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
  val loglevel  = config.getString("log.level")

  val submission = "Dummy Submission"
  val submission_hash = sha256(submission)
  val feedback = "<feedback><item id='1'><comment>Dummy Feedback</comment></item></feedback>"
  val feedback_hash = sha256(feedback)

  setLogLevel(loglevel)

  Class.forName(sqldriver)
  ConnectionPool.singleton(sqlurl, sqluser, sqlpw)

  implicit val system = ActorSystem("testing")

  override def createCamelContext(): CamelContext = {
    val camel = CamelExtension(system)
    if (camel.context.hasComponent("jms") == null) {
      val connectionFactory = new ActiveMQConnectionFactory("vm://localhost?broker.persistent=false")
      camel.context.addComponent("jms", JmsComponent.jmsComponentAutoAcknowledge(connectionFactory))
    }
    camel.context.setTracing(true)
    camel.context
  }

  val simple_feedback = new SimpleFeedback(1)
  val smtp_endpoint = new SMTP()
  val http_endpoint = new HTTP()
  val submission_endpoint = new Submission(simple_feedback, smtp_endpoint, http_endpoint)

  val builders = submission_endpoint.routes

  before {
    setUp
    SubmissionTable.create
    FeedbackTable.create
  }

  after {
    tearDown
    SubmissionTable.drop
    FeedbackTable.drop
  }

  test("Check submission route") {
    val camel_context = context
    camel_context.getRouteDefinitions(s"From[${submission_endpoint.entryUri}]").head.adviceWith(camel_context, new AdviceWithRouteBuilder {
      def configure = {
        weaveByToString("WireTap[direct:msg_store]").replace.to("mock:wiretap")
      }
    })
    camel_context.getRouteDefinitions(s"From[${simple_feedback.exitUri}]").head.adviceWith(camel_context, new AdviceWithRouteBuilder {
      def configure = {
        weaveByToString(s"To[${smtp_endpoint.entryUri}]").replace.to("mock:mail_endpoint")
        weaveByToString(s"To[${http_endpoint.entryUri}]").replace.to("mock:web_endpoint")
        weaveByToString("WireTap[direct:msg_store]").replace.to("mock:wiretap")
      }
    })

    val mock_mail = getMockEndpoint("mock:mail_endpoint")
    mock_mail.expectedMessageCount(1)
    mock_mail.expectedBodiesReceived(feedback)
    mock_mail.expectedHeaderReceived("replyTo", "student@hud.ac.uk")
    mock_mail.expectedHeaderReceived("sha256", feedback_hash)

    val mock_web = getMockEndpoint("mock:web_endpoint")
    mock_web.expectedMessageCount(1)
    mock_web.expectedBodiesReceived(feedback)
    mock_web.expectedHeaderReceived("replyTo", "student@hud.ac.uk")
    mock_web.expectedHeaderReceived("sha256", feedback_hash)

    val mock_tap = getMockEndpoint("mock:wiretap")
    mock_tap.expectedMessageCount(2)
    mock_tap.message(0).body.contains(submission)
    mock_tap.message(0).header("table").isEqualTo(SubmissionTable.name.value)
    mock_tap.message(1).body.contains(feedback)
    mock_tap.message(1).header("table").isEqualTo(FeedbackTable.name.value)
    mock_tap.message(1).header("sha256").isEqualTo(feedback_hash)

    template().sendBodyAndHeaders(submission_endpoint.entryUri, submission, Map("replyTo" -> mailTo))

    assertMockEndpointsSatisfied
  }

  test("Check mail route") {
    context.getRouteDefinitions(s"From[${smtp_endpoint.entryUri}]").head.adviceWith(context, new AdviceWithRouteBuilder {
      def configure = {
        weaveByToString(s"To[smtp:$mailhost]").replace.to("mock:smtp")
      }
    })

    val mock_mail = getMockEndpoint("mock:smtp")
    mock_mail.expectedMessageCount(1)
    mock_mail.message(0).body.contains(s"https://$webhost/$webuser/$feedback_hash")
    mock_mail.expectedHeaderReceived("replyTo", mailTo)
    mock_mail.expectedHeaderReceived("sha256", feedback_hash)
    mock_mail.expectedHeaderReceived("webuser", webuser)
    mock_mail.expectedHeaderReceived("webhost", webhost)
    mock_mail.expectedHeaderReceived("username", mailuser)
    mock_mail.expectedHeaderReceived("password", mailpw)
    mock_mail.expectedHeaderReceived("from", mailFrom)
    mock_mail.expectedHeaderReceived("to", mailTo)
    mock_mail.expectedHeaderReceived("subject", subject)

    template().sendBodyAndHeaders(smtp_endpoint.entryUri, feedback, Map("replyTo" -> mailTo, "sha256" -> feedback_hash))

    assertMockEndpointsSatisfied
  }

  test("Check web route") {
    context.getRouteDefinitions(s"From[${http_endpoint.entryUri}]").head.adviceWith(context, new AdviceWithRouteBuilder {
      def configure = {
        weaveByToString(s"To[sftp:$webuser@$webhost/www/]").replace.to("mock:sftp")
      }
    })
    val html_feedback = "<li>Dummy Feedback</li>"

    val mock_web = getMockEndpoint("mock:sftp")
    mock_web.expectedMessageCount(1)
    mock_web.message(0).body.contains(html_feedback)
    mock_web.expectedHeaderReceived("replyTo", mailTo)
    mock_web.expectedHeaderReceived("sha256", feedback_hash)
    mock_web.expectedHeaderReceived("student", mailTo)
    mock_web.expectedHeaderReceived("title", subject)
    mock_web.expectedHeaderReceived("CamelFileName", feedback_hash)

    template().sendBodyAndHeaders(http_endpoint.entryUri, feedback, Map("replyTo" -> mailTo, "sha256" -> feedback_hash))

    assertMockEndpointsSatisfied 
  }

  test("Check message store route") {
    DB autoCommit { implicit session =>
      template().sendBodyAndHeaders("direct:msg_store", submission, Map("table" -> SubmissionTable.name.value, "replyTo" -> mailTo, "breadcrumbId" -> "submission-testing-1"))

      val submission_count1 = sql"SELECT COUNT(*) FROM ${SubmissionTable.name}".map(_.int(1)).single.apply().get
      val feedback_count1 = sql"SELECT COUNT(*) FROM ${FeedbackTable.name}".map(_.int(1)).single.apply().get
      val (submission_id, submission_student, submission_message, submission_message_id) = sql"SELECT id, student, message, message_id FROM ${SubmissionTable.name}".map(rs => (rs.int("id"), rs.string("student"), rs.string("message"), rs.string("message_id"))).single.apply().get

      assert(submission_count1 == 1)
      assert(feedback_count1 == 0)
      assert(submission_id == 1)
      assert(submission_student == mailTo)
      assert(submission_message == submission)
      assert(submission_message_id == "submission-testing-1")
  
      template().sendBodyAndHeaders("direct:msg_store", feedback, Map("table" -> FeedbackTable.name.value, "replyTo" -> mailTo, "sha256" ->   feedback_hash, "breadcrumbId" -> "submission-testing-1"))

      val submission_count2 = sql"SELECT COUNT(*) FROM ${SubmissionTable.name}".map(_.int(1)).single.apply().get
      val feedback_count2 = sql"SELECT COUNT(*) FROM ${FeedbackTable.name}".map(_.int(1)).single.apply().get
      val (feedback_submission_id, feedback_sha256, feedback_message) = sql"SELECT submission_id, sha256, message FROM ${FeedbackTable.name}".map(rs => (rs.int("submission_id"), rs.string("sha256"), rs.string("message"))).single.apply().get

      assert(submission_count2 == 1)
      assert(feedback_count2 == 1)
      assert(feedback_submission_id == 1)
      assert(feedback_sha256 == feedback_hash)
      assert(feedback_message == feedback)
    }
  }
}

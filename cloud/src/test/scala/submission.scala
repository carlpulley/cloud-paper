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

package test

import akka.actor.Props
import akka.testkit.TestActorRef
import cloud.lib.Config
import cloud.lib.Helpers
import cloud.workflow.controller.ControlBus
import cloud.workflow.controller.SubmissionTable
import cloud.workflow.controller.FeedbackTable
import cloud.workflow.producer.HTTP
import cloud.workflow.producer.SMTP
import cloud.workflow.Submission
import java.io.File
import org.apache.camel.component.mock.MockEndpoint
import org.streum.configrity._
import scala.collection.JavaConversions._
import scalikejdbc.ConnectionPool
import scalikejdbc.DB
import scalikejdbc.SQLInterpolation._
import scalaz._
import scalaz.camel.core._

class SubmissionTests extends ScalaTestSupport with Helpers {
  import Scalaz._

  val config = Config.load("application.conf")

  val mailFrom  = "tutor@hud.ac.uk"
  val mailTo    = "student@hud.ac.uk"
  val subject   = s"Assessment feedback for ${group.toUpperCase}"
  val sqldriver = config[String]("sql.driver")
  val sqlurl    = config[String]("sql.url")
  val sqluser   = config[String]("sql.user")
  val sqlpw     = config[String]("sql.password")
  val mailhost  = config[String]("mail.host")
  val mailuser  = config[String]("mail.user")
  val mailpw    = config[String]("mail.password")
  val webhost   = config[String]("web.host")
  val webuser   = config[String]("web.user")
  val loglevel  = config[String]("log.level")

  val submission = "Dummy Submission"
  val submission_hash = sha256(submission)
  val feedback = "<feedback><question value='1'><suite><test name='example' passed='true'><outcome name='testExample'><comment name='feedback'>Dummy Feedback</comment></outcome></test></suite></question></feedback>"
  val feedback_failure = "Dummy Feedback"
  val feedback_hash = sha256(feedback)

  setLogLevel(loglevel)

  Class.forName(sqldriver)
  ConnectionPool.singleton(sqlurl, sqluser, sqlpw)

  val controller = TestActorRef(Props(new ControlBus(Map.empty)))
  val simple_feedback = { msg: Message => {
    if (msg.header("xsdFailure").isEmpty) {
      msg.setBody("<feedback><question value='1'><suite><test name='example' passed='true'><outcome name='testExample'><comment name='feedback'>" + msg.bodyAs[String].replaceAll("Submission", "Feedback") + "</comment></outcome></test></suite></question></feedback>")
    } else {
      msg.setBody(msg.bodyAs[String].replaceAll("Submission", "Feedback"))
    }
  }}
  val submission_endpoint = new Submission(controller, simple_feedback, to("mock:mail"), to("mock:web"))
  from(submission_endpoint.error_channel) {
    { msg: Message => if (msg.exception.isDefined) msg.addHeader("Exception", msg.exception.get.getMessage) else msg } >=> 
    to("log:ERROR?showAll=true") >=> 
    to("mock:submission-error")
  }

  before {
    MockEndpoint.resetMocks(camel.context)
    SubmissionTable.create
    FeedbackTable.create
  }

  after {
    SubmissionTable.drop
    FeedbackTable.drop
    MockEndpoint.resetMocks(camel.context)
  }

  override def afterAll = {
    router.stop
    system.shutdown
    new File(sqlurl.split(":").last).delete
  }

  def getSubmissionCount = DB autoCommit { implicit session =>
    sql"SELECT COUNT(*) FROM ${SubmissionTable.name}".map(_.int(1)).single.apply().get
  }

  def getFeedbackCount = DB autoCommit { implicit session =>
    sql"SELECT COUNT(*) FROM ${FeedbackTable.name}".map(_.int(1)).single.apply().get
  }

  test("Check successful submission route") {
    val mock_mail = getMockEndpoint("mock:mail")
    mock_mail.expectedMessageCount(1)
    mock_mail.expectedBodiesReceived(feedback)
    mock_mail.expectedHeaderReceived("sha256", feedback_hash)

    val mock_web = getMockEndpoint("mock:web")
    mock_web.expectedMessageCount(1)
    mock_web.expectedBodiesReceived(feedback)
    mock_web.expectedHeaderReceived("sha256", feedback_hash)

    template.sendBodyAndHeaders(submission_endpoint.uri, submission, Map("replyTo" -> mailTo, "breadcrumbId" -> "submission-testing-1"))

    mock_mail.assertIsSatisfied
    mock_web.assertIsSatisfied

    assert(getSubmissionCount == 1)
    assert(getFeedbackCount == 1)
  }

  test("Check submission route fails correctly with an XSD validation failure") {
    val mock_mail = getMockEndpoint("mock:mail")
    mock_mail.expectedMessageCount(0)

    val mock_web = getMockEndpoint("mock:web")
    mock_web.expectedMessageCount(0)

    val mock_error = getMockEndpoint("mock:submission-error")
    mock_error.expectedMessageCount(1)
    mock_error.expectedBodiesReceived(feedback_failure)

    template.sendBodyAndHeaders(submission_endpoint.uri, submission, Map("replyTo" -> mailTo, "breadcrumbId" -> "submission-testing-1-1", "xsdFailure" -> "true"))

    mock_mail.assertIsSatisfied
    mock_web.assertIsSatisfied
    mock_error.assertIsSatisfied

    assert(getSubmissionCount == 1)
    assert(getFeedbackCount == 0)
  }

  test("Check failing submission route") {
    val mock_mail = getMockEndpoint("mock:mail")
    mock_mail.expectedMessageCount(0)

    val mock_web = getMockEndpoint("mock:web")
    mock_web.expectedMessageCount(0)

    val mock_error = getMockEndpoint("mock:submission-error")
    mock_error.expectedMessageCount(1)
    mock_error.expectedBodiesReceived(submission)

    template.sendBody(submission_endpoint.uri, submission)

    mock_mail.assertIsSatisfied
    mock_web.assertIsSatisfied
    mock_error.assertIsSatisfied

    assert(getSubmissionCount == 0)
    assert(getFeedbackCount == 0)
  }

  test("Check message store inserts") {
    DB autoCommit { implicit session =>
      template.sendBodyAndHeaders(submission_endpoint.msg_store, submission, Map("table" -> SubmissionTable.name.value, "replyTo" -> mailTo, "breadcrumbId" -> "submission-testing-2"))

      assert(getSubmissionCount == 1)
      assert(getFeedbackCount == 0)

      val (submission_id, submission_student, submission_message, submission_message_id) = sql"SELECT id, student, message, message_id FROM ${SubmissionTable.name}".map(rs => (rs.int("id"), rs.string("student"), rs.string("message"), rs.string("message_id"))).single.apply().get
      assert(submission_id == 1)
      assert(submission_student == mailTo)
      assert(submission_message == submission)
      assert(submission_message_id == "submission-testing-2")
  
      template.sendBodyAndHeaders(submission_endpoint.msg_store, feedback, Map("table" -> FeedbackTable.name.value, "replyTo" -> mailTo, "sha256" ->   feedback_hash, "breadcrumbId" -> "submission-testing-2"))

      assert(getSubmissionCount == 1)
      assert(getFeedbackCount == 1)

      val (feedback_submission_id, feedback_sha256, feedback_message) = sql"SELECT submission_id, sha256, message FROM ${FeedbackTable.name}".map(rs => (rs.int("submission_id"), rs.string("sha256"), rs.string("message"))).single.apply().get
      assert(feedback_submission_id == 1)
      assert(feedback_sha256 == feedback_hash)
      assert(feedback_message == feedback)
    }
  }

  test("Check message store behaviour with invalid table specified") {
    val mock_error = getMockEndpoint("mock:submission-error")
    mock_error.expectedMessageCount(1)
    mock_error.expectedBodiesReceived(submission)

    DB autoCommit { implicit session =>
      template.sendBodyAndHeaders(submission_endpoint.msg_store, submission, Map("table" -> "invalid-table", "replyTo" -> mailTo, "breadcrumbId" -> "submission-testing-3"))

      assert(getSubmissionCount == 0)
      assert(getFeedbackCount == 0)
      mock_error.assertIsSatisfied
    }
  }

  test("Check message store inserts with differing breadcrumb IDs") {
    val mock_error = getMockEndpoint("mock:submission-error")
    mock_error.expectedMessageCount(1)
    mock_error.expectedBodiesReceived(feedback)
    mock_error.expectedHeaderReceived("sha256", feedback_hash)

    DB autoCommit { implicit session =>
      template.sendBodyAndHeaders(submission_endpoint.msg_store, submission, Map("table" -> SubmissionTable.name.value, "replyTo" -> mailTo, "breadcrumbId" -> "submission-testing-3"))

      assert(getSubmissionCount == 1)
      assert(getFeedbackCount == 0)

      val (submission_id, submission_student, submission_message, submission_message_id) = sql"SELECT id, student, message, message_id FROM ${SubmissionTable.name}".map(rs => (rs.int("id"), rs.string("student"), rs.string("message"), rs.string("message_id"))).single.apply().get
      assert(submission_id == 1)
      assert(submission_student == mailTo)
      assert(submission_message == submission)
      assert(submission_message_id == "submission-testing-3")
  
      template.sendBodyAndHeaders(submission_endpoint.msg_store, feedback, Map("table" -> FeedbackTable.name.value, "replyTo" -> mailTo, "sha256" ->   feedback_hash, "breadcrumbId" -> "submission-testing-3-1"))

      assert(getSubmissionCount == 1)
      assert(getFeedbackCount == 0)
      mock_error.assertIsSatisfied
    }
  }
}

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

import akka.actor.ActorSystem
import akka.actor.Props
import akka.testkit.TestActorRef
import cloud.lib.Helpers
import cloud.workflow.controller.ControlBus
import cloud.workflow.controller.SubmissionTable
import cloud.workflow.controller.FeedbackTable
import cloud.workflow.producer.HTTP
import cloud.workflow.producer.SMTP
import cloud.workflow.Submission
import com.typesafe.config._
import org.apache.camel.builder.AdviceWithRouteBuilder
import org.apache.camel.CamelContext
import org.apache.camel.component.mock.MockEndpoint
import scala.collection.JavaConversions._
import scalikejdbc.ConnectionPool
import scalikejdbc.DB
import scalikejdbc.SQLInterpolation._
import scalaz._
import scalaz.camel.core._

class SubmissionTests extends ScalaTestSupport with Helpers {
  import Scalaz._

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
  val feedback = "Dummy Feedback"
  val feedback_hash = sha256(feedback)

  setLogLevel(loglevel)

  Class.forName(sqldriver)
  ConnectionPool.singleton(sqlurl, sqluser, sqlpw)

  val controller = TestActorRef(Props(new ControlBus(group, Map.empty)))
  val simple_feedback = { msg: Message => msg.setBody(msg.bodyAs[String].replaceAll("Submission", "Feedback")) }
  val submission_endpoint = new Submission(controller, simple_feedback, to("mock:smtp"), to("mock:http"))

  before {
    SubmissionTable.create
    FeedbackTable.create
  }

  after {
    SubmissionTable.drop
    FeedbackTable.drop
    MockEndpoint.resetMocks(camel.context)
  }

  override def afterAll = router.stop

  def getSubmissionCount = DB autoCommit { implicit session =>
    sql"SELECT COUNT(*) FROM ${SubmissionTable.name}".map(_.int(1)).single.apply().get
  }

  def getFeedbackCount = DB autoCommit { implicit session =>
    sql"SELECT COUNT(*) FROM ${FeedbackTable.name}".map(_.int(1)).single.apply().get
  }

  test("Check successful submission route") {
    val mock_mail = getMockEndpoint("mock:smtp")
    mock_mail.expectedMessageCount(1)
    mock_mail.expectedBodiesReceived(feedback)
    mock_mail.expectedHeaderReceived("sha256", feedback_hash)

    val mock_web = getMockEndpoint("mock:http")
    mock_web.expectedMessageCount(1)
    mock_web.expectedBodiesReceived(feedback)
    mock_web.expectedHeaderReceived("sha256", feedback_hash)

    template.sendBodyAndHeaders(submission_endpoint.uri, submission, Map("replyTo" -> mailTo, "breadcrumbId" -> "submission-testing-1"))

    mock_mail.assertIsSatisfied
    mock_web.assertIsSatisfied

    // TODO: mock msg_store
    assert(getSubmissionCount == 1)
    assert(getFeedbackCount == 1)
    Thread.sleep(1000)
  }

  test("Check failing submission route") {
    val mock_mail = getMockEndpoint("mock:smtp")
    mock_mail.expectedMessageCount(0)

    val mock_web = getMockEndpoint("mock:http")
    mock_web.expectedMessageCount(0)

    template.sendBody(submission_endpoint.uri, submission)

    mock_mail.assertIsSatisfied
    mock_web.assertIsSatisfied

    // TODO: mock error_channel and check msg for exception
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
    DB autoCommit { implicit session =>
      template.sendBodyAndHeaders(submission_endpoint.msg_store, submission, Map("table" -> "invalid-table", "replyTo" -> mailTo, "breadcrumbId" -> "submission-testing-3"))

      // TODO: mock error_channel and check msg for exception
      assert(getSubmissionCount == 0)
      assert(getFeedbackCount == 0)
    }
  }

  test("Check message store inserts with differing breadcrumb IDs") {
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

      // TODO: mock error_channel and check msg for exception
      assert(getSubmissionCount == 1)
      assert(getFeedbackCount == 0)
    }
  }
}

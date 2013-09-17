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

package cloud.workflow.consumer

package test

import cloud.lib.Helpers
import cloud.workflow.controller.ControlBus
import cloud.workflow.controller.SubmissionTable
import cloud.workflow.controller.FeedbackTable
import cloud.workflow.Submission
import cloud.workflow.test.ScalaTestSupport
import com.typesafe.config._
import org.apache.camel.component.direct.DirectComponent
import org.apache.camel.component.mock.MockEndpoint
import scala.collection.JavaConversions._
import scalaz._
import scalaz.camel.core._

class DropboxTests extends ScalaTestSupport with Helpers {
  import Scalaz._

  val config: Config = ConfigFactory.load("application.conf")
  
  val mailTo   = "student@hud.ac.uk"
  val folder   = "submissions"
  val loglevel = config.getString("log.level")

  setLogLevel(loglevel)

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
  }

  camel.context.addComponent("file", new DirectComponent())
  Dropbox(folder)
  val workflow_hook = to("mock:workflow") >=> to("log:STOPPED?showAll=true") >=> failWith(new Exception("stopped"))
  val submission_endpoint = new Submission(ControlBus(), workflow_hook) with Dropbox
  from(submission_endpoint.error_channel) {
    to("mock:error")
  }

  val submission = "Dummy submission file"

  test("Ensure that dropbox .tgz files are correctly processed") {
    val mock_workflow = getMockEndpoint("mock:workflow")

    mock_workflow.expectedMessageCount(1)
    mock_workflow.expectedHeaderReceived("replyTo", "u1234567@hud.ac.uk")
    mock_workflow.expectedBodiesReceived(submission+"-1")

    template.sendBodyAndHeaders(s"file:$folder", submission+"-1", Map("fileName" -> "u1234567.tgz"))

    mock_workflow.assertIsSatisfied
  }

  test("Ensure that dropbox .tar.gz files are correctly processed") {
    val mock_workflow = getMockEndpoint("mock:workflow")
    
    mock_workflow.expectedMessageCount(1)
    mock_workflow.expectedHeaderReceived("replyTo", "u1234567@hud.ac.uk")
    mock_workflow.expectedBodiesReceived(submission+"-2")

    template.sendBodyAndHeaders(s"file:$folder", submission+"-2", Map("fileName" -> "u1234567.tar.gz"))

    mock_workflow.assertIsSatisfied
  }

  test("Ensure that incorrectly typed dropbox files are rejected") {
    val mock_workflow = getMockEndpoint("mock:workflow")
    val mock_error = getMockEndpoint("mock:error")
    mock_workflow.reset
    mock_error.reset
    
    mock_workflow.expectedMessageCount(0)
    mock_error.expectedMessageCount(1)
    mock_error.expectedBodiesReceived(submission+"-3")

    template.sendBodyAndHeaders(s"file:$folder", submission+"-3", Map("fileName" -> "u1234567.txt"))

    mock_workflow.assertIsSatisfied
    mock_error.assertIsSatisfied
  }

  test("Ensure that incorrectly named dropbox files are rejected") {
    val mock_workflow = getMockEndpoint("mock:workflow")
    val mock_error = getMockEndpoint("mock:error")
    mock_workflow.reset
    mock_error.reset

    mock_workflow.expectedMessageCount(0)
    mock_error.expectedMessageCount(1)
    mock_error.expectedBodiesReceived(submission+"-4")

    template.sendBodyAndHeaders(s"file:$folder", submission+"-4", Map("fileName" -> "abcdefg.tgz"))

    mock_workflow.assertIsSatisfied
    mock_error.assertIsSatisfied
  }
}

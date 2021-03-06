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

package cloud.transport.consumer

package test

import cloud.lib.Config
import cloud.lib.Helpers
import cloud.transport.controller.ControlBus
import cloud.transport.controller.SubmissionTable
import cloud.transport.controller.FeedbackTable
import cloud.transport.Submission
import cloud.transport.test.ScalaTestSupport
import java.io.File
import org.apache.camel.component.mock.MockEndpoint
import scala.collection.JavaConversions._
import scala.tools.nsc.io.Directory
import scala.tools.nsc.io.Path
import scalikejdbc.ConnectionPool
import scalaz._
import scalaz.camel.core._

class DropboxTests extends ScalaTestSupport with Helpers {
  import Scalaz._

  val config = Config.load("application.conf")
  
  val sqldriver = config.get[String]("sql.driver")
  val sqlurl    = config.get[String]("sql.url")
  val sqluser   = config.get[String]("sql.user")
  val sqlpw     = config.get[String]("sql.password")
  val folder    = Directory.makeTemp().toString
  val loglevel  = config.get[String]("log.level")

  setLogLevel(loglevel)

  Class.forName(sqldriver)
  ConnectionPool.singleton(sqlurl, sqluser, sqlpw)

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

  Dropbox(folder)
  val workflow_hook = to("mock:dropbox-workflow") >=> to("log:STOPPED?showAll=true") >=> failWith(new Exception("stopped"))
  val submission_endpoint = new Submission(ControlBus(), workflow_hook) with Dropbox {
    override def error_handler {
      from(error_channel) {
        { msg: Message => if (msg.exception.isDefined) msg.addHeader("Exception", msg.exception.get.getMessage) else msg } >=> 
        to("log:ERROR?showAll=true") >=> 
        to("mock:dropbox-error")
      }
    }
  }

  val submission = "Dummy submission file"

  test("Ensure that dropbox .tgz files are correctly processed") {
    val mock_workflow = getMockEndpoint("mock:dropbox-workflow")

    mock_workflow.expectedMessageCount(1)
    mock_workflow.expectedHeaderReceived("replyTo", "u1234567@hud.ac.uk")
    mock_workflow.expectedHeaderReceived("ContentType", "application/x-tgz")
    mock_workflow.message(0).header("breadcrumbId").isNotNull
    mock_workflow.expectedBodiesReceived(submission+"-1")

    Path(s"$folder/u1234567").createDirectory(true, false)
    Path(s"$folder/u1234567/sample.tgz").toFile.writeAll(submission+"-1")

    mock_workflow.assertIsSatisfied
  }

  test("Ensure that dropbox .tar.gz files are correctly processed") {
    val mock_workflow = getMockEndpoint("mock:dropbox-workflow")
    
    mock_workflow.expectedMessageCount(1)
    mock_workflow.expectedHeaderReceived("replyTo", "u1234567@hud.ac.uk")
    mock_workflow.expectedHeaderReceived("ContentType", "application/x-tgz")
    mock_workflow.message(0).header("breadcrumbId").isNotNull
    mock_workflow.expectedBodiesReceived(submission+"-2")

    Path(s"$folder/u1234567").createDirectory(true, false)
    Path(s"$folder/u1234567/sample.tar.gz").toFile.writeAll(submission+"-2")

    mock_workflow.assertIsSatisfied
  }

  test("Ensure that incorrectly typed dropbox files are rejected") {
    val mock_workflow = getMockEndpoint("mock:dropbox-workflow")
    val mock_error = getMockEndpoint("mock:dropbox-error")
    mock_error.reset
    
    mock_workflow.expectedMessageCount(0)
    mock_error.expectedMessageCount(1)
    mock_error.expectedBodiesReceived(submission+"-3")

    Path(s"$folder/u1234567").createDirectory(true, false)
    Path(s"$folder/u1234567/sample.txt").toFile.writeAll(submission+"-3")

    mock_workflow.assertIsSatisfied
    mock_error.assertIsSatisfied
  }

  test("Ensure that incorrectly named dropbox folders are rejected") {
    val mock_workflow = getMockEndpoint("mock:dropbox-workflow")
    val mock_error = getMockEndpoint("mock:dropbox-error")
    mock_error.reset

    mock_workflow.expectedMessageCount(0)
    mock_error.expectedMessageCount(1)
    mock_error.expectedBodiesReceived(submission+"-4")

    Path(s"$folder/abcdefg").createDirectory(true, false)
    Path(s"$folder/abcdefg/sample.tgz").toFile.writeAll(submission+"-4")

    mock_workflow.assertIsSatisfied
    mock_error.assertIsSatisfied
  }

  test("Ensure that files without enclosing folders are rejected") {
    val mock_workflow = getMockEndpoint("mock:dropbox-workflow")
    val mock_error = getMockEndpoint("mock:dropbox-error")
    mock_error.reset

    mock_workflow.expectedMessageCount(0)
    mock_error.expectedMessageCount(1)
    mock_error.expectedBodiesReceived(submission+"-5")

    Path(s"$folder/u1234567.tgz").toFile.writeAll(submission+"-5")

    mock_workflow.assertIsSatisfied
    mock_error.assertIsSatisfied
  }
}

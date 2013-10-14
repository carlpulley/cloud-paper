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

package cloud.workflow.dsl.test

import akka.actor.Props
import akka.testkit.TestActorRef
import akka.util.Timeout
import cloud.lib.Config
import cloud.lib.Helpers
import cloud.workflow.controller.ControlBus
import cloud.workflow.dsl.Context
import cloud.workflow.test.MockImage
import cloud.workflow.test.ScalaTestSupport
import org.streum.configrity._
import scala.collection.JavaConversions._
import scala.concurrent.duration._
import scala.language.postfixOps
import scalaz._
import scalaz.camel.core._

class ContextTests extends ScalaTestSupport with Helpers {
  import Scalaz._

  val config = Config.load("application.conf")
  val loglevel  = config[String]("log.level")

  setLogLevel(loglevel)

  implicit val timeout: Timeout = Timeout(30 seconds)
  implicit val controller = TestActorRef(Props(new ControlBus(Map.empty)))
  val appender = { msg: Message => msg.setBody(msg.bodyAs[String] + "-done") }
  val image = new MockImage()
  val context_route = Context(image, ("test" -> (to("mock:workflow") >=> appender)))

  override def afterAll = {
    router.stop
    system.shutdown
  }

  test("Check that contexts launch and process messages correctly") {
    // TODO: check that expected actors have launched!

    val mock_workflow = getMockEndpoint("mock:workflow")
    mock_workflow.expectedMessageCount(1)
    mock_workflow.expectedBodiesReceived("send")

    context_route process Message("send") match {
      case Success(msg: Message) => msg.bodyAs[String] matches "send-done"
      case _ => fail("FIXME:")
    }

    mock_workflow.assertIsSatisfied
  }
}

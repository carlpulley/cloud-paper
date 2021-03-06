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

package cloud.transport.dsl.test

import akka.actor.Props
import akka.testkit.TestActorRef
import akka.util.Timeout
import cloud.lib.Config
import cloud.lib.Helpers
import cloud.lib.Image
import cloud.transport.controller.ControlBus
import cloud.transport.dsl.Context
import cloud.transport.test.MockImage
import cloud.transport.test.ScalaTestSupport
import java.util.concurrent.TimeoutException
import java.util.concurrent.TimeUnit
import org.streum.configrity._
import scala.collection.JavaConversions._
import scala.concurrent.duration._
import scala.language.postfixOps
import scalaz._
import scalaz.camel.core._

//class ContextTests extends ScalaTestSupport with Helpers {
//  import Scalaz._
//
//  val config = Config.load("application.conf")
//  val loglevel  = config.get[String]("log.level")
//
//  setLogLevel(loglevel)
//
//  implicit val timeout: Timeout = Timeout(30 seconds)
//  implicit val controller = TestActorRef(Props(new ControlBus(Map.empty)))
//  val appender = { msg: Message => msg.setBody(msg.bodyAs[String] + "-done") }
//  val image1 = new MockImage("image1")
//  val image2 = new MockImage("image2")
//  val context_route1 = Context(image1, "default", "test1", to("mock:image1_workflow1") >=> appender)
//  val context_route2 = Context(image1, "default", "test2", to("mock:image1_workflow2") >=> appender)
//  val context_route3 = Context(image2, "default", "test3", to("mock:image2_workflow1") >=> appender)
//  val context_route4 = Context(image2, "default", "test4", to("mock:image2_workflow2") >=> appender)
//
//  override def afterAll = {
//    router.stop
//    system.shutdown
//  }
//
//  test("Check that contexts process messages correctly") {
//    val mock_workflow = getMockEndpoint("mock:image1_workflow1")
//    mock_workflow.expectedMessageCount(1)
//    mock_workflow.expectedBodiesReceived("send")
//
//    context_route1 process(Message("send"), 30, TimeUnit.SECONDS) match {
//      case Success(msg: Message) => msg.bodyAs[String] matches "send-done"
//      case _ => fail("workflow failed to process message")
//    }
//
//    mock_workflow.assertIsSatisfied
//  }
//
//  test("Multiple contexts define correct number of VM instances") {
//    assert(Context.getImages.size == 2)
//  }
//}

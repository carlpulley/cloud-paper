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

package cloud.workflow.producer

package test

import cloud.lib.Config
import cloud.lib.Helpers
import cloud.workflow.test.ScalaTestSupport
import org.apache.camel.component.mock.MockComponent
import org.apache.camel.component.mock.MockEndpoint
import scala.collection.JavaConversions._
import scalaz._
import scalaz.camel.core._

class HTTPTests extends ScalaTestSupport with Helpers {
  import Scalaz._

  val config = Config.load("application.conf")
  
  val mailTo   = "student@hud.ac.uk"
  val subject  = s"Assessment feedback for ${group.toUpperCase}"
  val webhost  = config[String]("web.host")
  val webuser  = config[String]("web.user")
  val loglevel = config[String]("log.level")

  setLogLevel(loglevel)

  before {
    MockEndpoint.resetMocks(camel.context)
  }

  after {
    MockEndpoint.resetMocks(camel.context)
  }

  override def afterAll = {
    router.stop
    system.shutdown
  }

  val feedback = "<feedback><question value='2'><suite><test passed='false'><outcome><comment>Dummy comment 2</comment></outcome></test></suite></question><question value='1'><suite><test passed='false'><outcome><comment>Dummy comment 1</comment></outcome></test></suite></question></feedback>"
  val feedback_hash = sha256(feedback)

  camel.context.addComponent("sftp", new MockComponent())

  test("Ensure that HTML file is named correctly and contains expected feedback") {
    HTTP() process Message(feedback, Map("replyTo" -> mailTo, "sha256" -> feedback_hash)) match {
      case Success(msg: Message) => {
        assert(msg.bodyAs[String].contains(s"<li>Dummy comment 1</li>"))
        assert(msg.bodyAs[String].contains(s"<li>Dummy comment 2</li>"))
        assert(msg.headerAs[String]("CamelFileName").isDefined)
        assert(msg.headerAs[String]("CamelFileName").get == feedback_hash)
      }
      case _ => fail("success response expected")
    }
  }
}

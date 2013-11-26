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

package cloud.transport.producer

package test

import cloud.lib.Config
import cloud.lib.Helpers
import cloud.transport.test.ScalaTestSupport
import javax.mail.internet.InternetAddress
import org.jvnet.mock_javamail.Mailbox
import scala.collection.JavaConversions._
import scalaz._
import scalaz.camel.core._

class SMTPTests extends ScalaTestSupport with Helpers {
  import Scalaz._

  val config = Config.load("application.conf")

  val mailFrom  = "tutor@hud.ac.uk"
  val mailTo    = "student@hud.ac.uk"
  val subject   = s"Assessment feedback for ${group.toUpperCase}"
  val webhost   = config.get[String]("web.host")
  val webuser   = config.get[String]("web.user")
  val loglevel  = config.get[String]("log.level")

  setLogLevel(loglevel)

  before {
    Mailbox.clearAll
  }

  override def afterAll = {
    router.stop
    system.shutdown
  }

  val feedback = "<feedback><question value='2'><suite><test passed='false'><outcome><comment>Dummy comment 2</comment></outcome></test></suite></question><question value='1'><suite><test passed='false'><outcome><comment>Dummy comment 1</comment></outcome></test></suite></question></feedback>"
  val feedback_hash = sha256(feedback)

  test("Ensure that email has expected headers and contains correct URL") {    
    SMTP() process Message(feedback, Map("replyTo" -> mailTo, "sha256" -> feedback_hash)) match {
      case Success(msg: Message) => {
        val inbox = Mailbox.get(mailTo)
        assert(inbox.size() == 1)
        val message = inbox.get(0)
        assert(message.getFrom.map(_.asInstanceOf[InternetAddress].getAddress).contains(mailFrom))
        assert(message.getReplyTo.map(_.asInstanceOf[InternetAddress].getAddress).contains(mailFrom))
        assert(message.getAllRecipients.map(_.asInstanceOf[InternetAddress].getAddress).contains(mailTo))
        assert(message.getSubject == subject)
        assert(message.isMimeType("text/plain"))
        assert(message.getContent.asInstanceOf[String].contains(s"https://$webhost/$webuser/$group/$feedback_hash"))
      }
      case _ => fail("success response expected")
    }
  }
}

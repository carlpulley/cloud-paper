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

import cloud.lib.Config
import cloud.lib.Workflow
import scalaz._
import scalaz.camel.core._

object SMTP extends Workflow {
  import Scalaz._

  def apply()(implicit group: String, router: Router): MessageRoute = {
    val config = Config(group)
  
    val mailFrom = "tutor@hud.ac.uk"
    val subject  = s"Assessment feedback for ${group.toUpperCase}"
    val mailhost = config.get[String]("mail.host")
    val mailuser = config.get[String]("mail.user")
    val mailpw   = config.get[String]("mail.password")
    val webhost  = config.get[String]("web.host")
    val webuser  = config.get[String]("web.user")

    // Here we send a template email containing a URL link to the actual assessment feedback
    { msg: Message => msg.addHeaders(Map("webuser" -> webuser, "webhost" -> webhost, "module" -> group)) } >=>
    to(s"velocity:$group/feedback-email.vm") >=>
    { msg: Message => msg.setHeaders(Map("username" -> mailuser, "password" -> mailpw, "from" -> mailFrom, "to" -> msg.headerAs[String]("replyTo").get, "subject" -> subject)) } >=>
    to(s"smtp:$mailhost")
  }
}

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

import cloud.lib.Workflow
import scalaz._
import scalaz.camel.core._

object SMTP extends Workflow {
  import Scalaz._

  def apply()(implicit group: String, router: Router): MessageRoute = {
    val config = getConfig(group)
  
    val mailFrom = config.getString("feedback.tutor")
    val subject  = config.getString("feedback.subject")
    val mailhost = config.getString("mail.host")
    val mailuser = config.getString("mail.user")
    val mailpw   = config.getString("mail.password")
    val webhost  = config.getString("web.host")
    val webuser  = config.getString("web.user")

    // Here we send a template email containing a URL link to the actual assessment feedback
    { msg: Message => msg.setHeaders(Map("webuser" -> webuser, "webhost" -> webhost, "group" -> group)) } >=>
    to(s"velocity:$group/feedback-email.vm") >=>
    { msg: Message => msg.setHeaders(Map("username" -> mailuser, "password" -> mailpw, "from" -> mailFrom, "to" -> msg.headerAs[String]("replyTo"), "subject" -> subject)) } >=>
    to(s"smtp:$mailhost")
  }
}

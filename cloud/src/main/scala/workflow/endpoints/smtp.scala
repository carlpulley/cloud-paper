// Copyright (C) 2013  Carl Pulley
// 
// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU General Public License
// as published by the Free Software Foundation; either version 2
// of the License, or (at your option) any later version.
// 
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.

package cloud

package workflow

package endpoints

import cloud.lib.EndpointWorkflow
import com.typesafe.config._
import org.apache.camel.scala.dsl.builder.RouteBuilder

class SMTP extends EndpointWorkflow {
  private[this] val config: Config = ConfigFactory.load("application.conf")

  private[this] val mailFrom = config.getString("feedback.tutor")
  private[this] val subject  = config.getString("feedback.subject")
  private[this] val mailhost = config.getString("mail.host")
  private[this] val mailuser = config.getString("mail.user")
  private[this] val mailpw   = config.getString("mail.password")
  private[this] val webhost  = config.getString("web.host")
  private[this] val webuser  = config.getString("web.user")

  def entryUri = "direct:mail_endpoint"

  def routes = Seq(new RouteBuilder {
    entryUri ==> {
        // Here we send a template email containing a URL link to the actual assessment feedback
        setHeader("webuser", webuser)
        setHeader("webhost", webhost)
        to("velocity:feedback-email.vm")
        setHeader("username", mailuser)
        setHeader("password", mailpw)
        setHeader("from", mailFrom)
        setHeader("to", header("replyTo"))
        setHeader("subject", subject)
        to(s"smtp:$mailhost")
        stop
    }
  })
}

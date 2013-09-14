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

package cloud.workflow.controller.plugins

import cloud.lib.EventDrivenWorkflow
import cloud.lib.Helpers
import cloud.workflow.controller.ControlEvent
import com.typesafe.config._
import org.apache.camel.Exchange
import org.apache.camel.scala.dsl.builder.RouteBuilder
import scala.collection.JavaConversions._
import scala.concurrent.duration._

class Imap(group: String, folder: String, timeout: Duration) extends EventDrivenWorkflow with Helpers {
  private[this] val config = getConfig(group)

  private[this] val mailhost = config.getString("mail.host")
  private[this] val mailuser = config.getString("mail.user")
  private[this] val mailpw   = config.getString("mail.password")

  def extractAttachment = { (exchange: Exchange) =>
    assert(exchange.getIn().hasAttachments())
    val attachments = exchange.getIn().getAttachments()
    assert(attachments.size() == 1)
  
    val name = attachments.keySet().head
    val dh = attachments.get(name)
    assert(dh.getContentType() == "application/x-tgz")
  
    exchange.getContext().getTypeConverter().convertTo(classOf[Array[Byte]], dh.getInputStream())
  }

  val handlers: PartialFunction[ControlEvent, Unit] = Map.empty

  def routes = Seq(new RouteBuilder {
    s"imaps:$mailhost?username=$mailuser&password=$mailpw&folderName=$folder.$group&consumer.delay=${timeout.toMillis}" ==> {
      transform(extractAttachment)
      to("jms:queue:$group-submission")
    }
  })
}

object Imap {
  def apply(folder: String, timeout: Duration = 1.minute)(implicit group: String) = {
    new Imap(group, folder, timeout)
  }
}

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

import cloud.lib.Workflow
import cloud.workflow.Submission
import org.apache.camel.Exchange
import org.apache.camel.scala.dsl.builder.RouteBuilder
import org.apache.camel.scala.Preamble
import scala.collection.JavaConversions._
import scala.concurrent.duration._

// NOTE: as we need to access a CamelMessage's attachments, we can't use the scalaz-camel DSL here

trait Imap extends Preamble { this: Submission =>
  private[this] val mailhost = config.getString("mail.host")
  private[this] val mailuser = config.getString("mail.user")
  private[this] val mailpw   = config.getString("mail.password")

  private[this] val folder   = config.getString("imap.folder")
  private[this] val poll     = new DurationInt(if (config.hasPath("imap.poll")) config.getInt("imap.poll") else 1).minutes

  val extractAttachment = { (exchange: Exchange) =>
    assert(exchange.getIn().hasAttachments())
    val attachments = exchange.getIn().getAttachments()
    assert(attachments.size() == 1)
  
    val name = attachments.keySet().head
    val dh = attachments.get(name)
    assert(dh.getContentType() == "application/x-tgz")
  
    exchange.getContext().getTypeConverter().convertTo(classOf[Array[Byte]], dh.getInputStream())
  }

  router.context.addRoutes(new RouteBuilder {
    s"imaps:$mailhost?username=$mailuser&password=$mailpw&folderName=$folder&consumer.delay=${poll.toMillis}" ==> {
      transform(extractAttachment)
      to(uri)
    }
  })
}

object Imap {
  def apply(folder: String, poll: Duration = 1.minute) {
    System.setProperty("imap.folder", folder)
    System.setProperty("imap.poll", poll.toMinutes.toString)
  }
}

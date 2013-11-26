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

import cloud.lib.Config
import cloud.lib.Workflow
import cloud.transport.Submission
import org.apache.camel.Exchange
import org.apache.camel.scala.dsl.builder.RouteBuilder
import org.apache.camel.scala.Preamble
import scala.collection.JavaConversions._
import scala.concurrent.duration._

// NOTE: as we need to access a CamelMessage's attachments, we prefer the use of the Java fluent builder DSL over the scalaz-camel DSL

trait Imap extends Preamble { this: Submission =>
  private[this] val mailhost = config.get[String]("mail.host")
  private[this] val mailuser = config.get[String]("mail.user")
  private[this] val mailpw   = config.get[String]("mail.password")

  private[this] val folders  = config.get[List[String]]("imap.folders")
  private[this] val poll     = new DurationInt(config.get[Int]("imap.poll", 1)).minutes
  private[this] val proto    = if (config.get[Boolean]("imap.ssl", true)) "imaps" else "imap"

  val extractAttachment = { (exchange: Exchange) =>
    val replyTo = if (exchange.getIn.getHeader("ReplyTo") == null) exchange.getIn.getHeader("From") else exchange.getIn.getHeader("ReplyTo")
    assert(replyTo != null)
    exchange.getIn.setHeader("replyTo", replyTo)

    assert(exchange.getIn().hasAttachments())
    val attachments = exchange.getIn().getAttachments()
    assert(attachments.size() == 1)
  
    val name = attachments.keySet().head
    assert(name.endsWith(".tgz") || name.endsWith(".tar.gz"))
    val dh = attachments.get(name)
    assert(Seq("application/octet-stream", "application/x-tgz", "application/x-gzip").contains(dh.getContentType()))
    exchange.getIn.setHeader("ContentType", "application/x-tgz")
  
    val tarball = exchange.getContext().getTypeConverter().convertTo(classOf[Array[Byte]], dh.getInputStream())
    exchange.getIn.setBody(tarball, classOf[Array[Byte]])
  }

  for(folder <- folders) {
    router.context.addRoutes(new RouteBuilder {
      s"$proto:$mailhost?username=$mailuser&password=$mailpw&folderName=$folder&consumer.delay=${poll.toMillis}" ==> {
        errorHandler(deadLetterChannel(error_channel))
  
        process(extractAttachment)
        to(uri)
      }
    })
  }
}

object Imap {
  def apply(folders: String*)(implicit poll: Duration = 1.minute, ssl: Boolean = true) {
    Config.setValue("imap.folders", folders.toList)
    Config.setValue("imap.poll", poll.toMinutes)
    Config.setValue("imap.ssl", ssl)
  }
}

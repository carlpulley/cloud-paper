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

import cloud.lib.Config
import cloud.lib.Workflow
import cloud.workflow.Submission
import org.apache.camel.scala.dsl.builder.RouteBuilder
import scalaz._
import scalaz.camel.core._

trait Dropbox extends Workflow { this: Submission =>
  import Scalaz._

  private[this] val folders = config.get[List[String]]("dropbox.folders")
  
  def parse_filename(header: String) = {
    val filename = """([uU][0-9]{7})\.(tar\.gz|tgz)""".r
    val filename(student, extn) = header

    student
  }

  for(folder <- folders) {
    // Need this to be an actual Camel exchange for move etc. to work as expected, hence use of Java fluent DSL
    router.context.addRoutes(new RouteBuilder {
      s"file:$folder?moveFailed=.error&move=.done" ==> to("direct:dropbox")
    })
  }

  from("direct:dropbox") {
    // Only process tar ball files (we rely on file naming conventions here)
    attempt {
      choose {
        case Message(_, hdrs) if (hdrs.lift("CamelFileNameOnly").isDefined) => {
          { msg: Message => 
            val student = parse_filename(msg.headerAs[String]("CamelFileNameOnly").get)
  
            msg.addHeaders(Map("replyTo" -> "%s@hud.ac.uk".format(student), "ContentType" -> "application/x-tgz", "breadcrumbId" -> getUniqueName(student)))   
          } >=> 
          to(uri)
        }
        case _ =>
          { msg: Message => throw new Exception("Invalid message received") }
      }
    } fallback {
        case ex: Exception => to(error_channel) >=> failWith(ex)
    }
  }
}

object Dropbox {
  def apply(folders: String*) {
    Config.setValue("dropbox.folders", folders.toList)
  }
}

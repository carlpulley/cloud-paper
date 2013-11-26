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
import org.apache.camel.scala.dsl.builder.RouteBuilder
import scala.tools.nsc.io.Path
import scalaz._
import scalaz.camel.core._

trait Dropbox extends Workflow { this: Submission =>
  import Scalaz._

  private[this] val folders = config.get[List[String]]("dropbox.folders")
  
  def parseStudentId(header: String) = {
    val studentId = """([uU][0-9]{7})""".r
    val studentId(student) = header

    student
  }

  for(folder <- folders) {
    // Need this to be an actual Camel exchange for move etc. to work as expected, hence use of Java fluent DSL
    router.context.addRoutes(new RouteBuilder {
      s"file:$folder?recursive=true&moveFailed=.error&move=.done" ==> to("direct:dropbox")
    })
  }

  from("direct:dropbox") {
    // Only process tar ball files within folders matching a naming convention (see parseStudentId)
    attempt {
      choose {
        case Message(_, hdrs) if (hdrs.lift("CamelFileParent").isDefined) => {
          { msg: Message => 
            val student = parseStudentId(Path(msg.headerAs[String]("CamelFileParent").get).name)

            msg.addHeaders(Map("replyTo" -> "%s@hud.ac.uk".format(student), "ContentType" -> "application/x-tgz", "breadcrumbId" -> getUniqueName(student)))
          } >=> 
          to(uri)
        }

        case _ =>
          { msg: Message => throw new Exception("Invalid message received") }
      }
    } fallback {
      case exn: Exception => to(error_channel) >=> failWith(exn)
    }
  }
}

object Dropbox {
  def apply(folders: String*) {
    Config.setValue("dropbox.folders", folders.toList)
  }
}

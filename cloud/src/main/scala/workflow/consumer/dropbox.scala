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
import scalaz._
import scalaz.camel.core._

trait Dropbox extends Workflow { this: Submission =>
  import Scalaz._

  private[this] val folder = config[String]("dropbox.folder")
  
  def parse_filename(header: String) = {
    val filename = """([uU][0-9]{7})\.(tar\.gz|tgz)""".r
    val filename(student, extn) = header

    student
  }

  from(s"file:$folder?moveFailed=.error&move=.done") {
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
        case ex: Exception => { msg: Message => msg.setException(ex) } >=> to(error_channel)
    }
  }
}

object Dropbox {
  def apply(folder: String) {
    Config.setValue("dropbox.folder", folder)
  }
}

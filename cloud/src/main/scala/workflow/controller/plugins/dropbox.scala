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
import cloud.workflow.controller.ControlEvent
import org.apache.camel.scala.dsl.builder.RouteBuilder

class Dropbox(val group: String, folder: String) extends EventDrivenWorkflow {
  val handlers: PartialFunction[ControlEvent, Unit] = Map.empty

  val routes = Seq(new RouteBuilder {
    s"file:$folder/$group" ==> {
      errorHandler(deadLetterChannel(error_channel))

      // Only process tar ball files (we rely on file extension to convey type)
      when(simple("${file:ext} in 'tgz,tar.gz'")) {
        // We assume that file name is student ID
        setHeader("replyTo", simple("${file:onlyname.noext}@hud.ac.uk"))
        to(s"jms:queue:$group-submission")
      }
    }
  })
}

object Dropbox {
  def apply(folder: String)(implicit group: String) = {
    new Dropbox(group, folder)
  }
}

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
import scala.tools.nsc.io.File

class Git(val group: String, folder: String, cron: String) extends EventDrivenWorkflow {
  val handlers: PartialFunction[ControlEvent, Unit] = Map.empty

  val routes = Seq(new RouteBuilder {
    // FIXME: for security, we want this to run with *every* message!
    val tarball = File.makeTemp(suffix=".tgz")

    // Run a 'git pull' using cron (defaults to once per day)
    s"quartz:$group-git?cron=$cron" --> s"exec:git?args=pull&workingDir=$folder/$group"

    // Monitor git repository directory for changes and generate a submission tar ball
    s"file:$folder/$group?recursive=true" ==> {
      to(s"exec:tar?outFile=$tarball&args=-czf $tarball -C $folder/$group .")
      to(s"jms:queue:$group-submission")
    }
  })
}

object Git {
  def apply(folder: String, cron: String = "* * * * Mon-Sun")(implicit group: String) = {
    new Git(group, folder, cron)
  }
}

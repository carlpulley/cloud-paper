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
import scala.tools.nsc.io.File
import scalaz._
import scalaz.camel.core._

trait Git extends Workflow { this: Submission =>
  import Scalaz._

  private[this] val folders = config.get[String]("git.folders")
  private[this] val cron    = config.get[String]("git.cron")

  for(folder <- folders) {
    // Run a 'git pull' using cron (defaults to once per day)
    from(s"quartz:$group-git?cron=$cron") {
      to(s"exec:git?args=pull&workingDir=$folder") >=>
      choose {
        case Message("fatal: Not a git repository (or any of the parent directories): .git", _) => {
          failWith(new Exception(s"$folder is not a git repository"))
        }
      }
    }
    
    // Monitor git repository directory for changes and generate a submission tar ball
    from(s"file:$folder?recursive=true") {
      val tarball = File.makeTemp(suffix=".tgz")
    
      to(s"exec:tar?outFile=$tarball&args=-czf $tarball -C $folder .") >=> 
      { msg: Message => msg.addHeader("ContentType", "application/x-tgz") } >=>
      to(this.uri)
    }
  }
}

object Git {
  def apply(folders: String*)(implicit cron: String = "* * * * Mon-Sun") {
    Config.setValue("git.folders", folders.toList)
    Config.setValue("git.cron", cron)
  }
}

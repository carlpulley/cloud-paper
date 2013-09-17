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

package cloud.workflow.producer

import cloud.lib.Workflow
import scalaz._
import scalaz.camel.core._

object Printer extends Workflow {
  import Scalaz._

  def apply()(implicit group: String, router: Router): MessageRoute = {
    val config = getConfig(group)
  
    val subject    = config.getString("feedback.subject")
    val lpraddr    = if (config.hasPath("lpr.address")) config.getString("lpr.address") else "localhost"
    val lprpath    = if (config.hasPath("lpr.path")) config.getString("lpr.path") else "default"
    val lproptions = if (config.hasPath("lpr.options")) config.getString("lpr.options") else "sides=two-sided"

    { msg: Message => msg.addHeaders(Map("student" -> msg.headerAs[String]("replyTo").get, "title" -> subject)) } >=>
    to(s"xslt:$group/feedback-printer.xsl") >=>
    to("fop:application/pdf") >=>
    to(s"lpr:$lpraddr/$lprpath?$lproptions")
  }
}

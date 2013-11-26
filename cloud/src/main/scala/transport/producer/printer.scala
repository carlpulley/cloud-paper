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

package cloud.transport.producer

import cloud.lib.Config
import cloud.lib.Workflow
import scalaz._
import scalaz.camel.core._

object Printer extends Workflow {
  import Scalaz._

  def apply()(implicit group: String, router: Router): MessageRoute = {
    val config = Config(group)
  
    val lpraddr    = config.get[String]("lpr.address", "localhost")
    val lprpath    = config.get[String]("lpr.path", "default")
    val lproptions = config.get[String]("lpr.options", "sides=two-sided")

    { msg: Message => msg.addHeaders(Map("student" -> msg.headerAs[String]("replyTo").get, "module" -> group)) } >=>
    to(s"xslt:$group/feedback-printer.xsl") >=>
    to("fop:application/pdf") >=>
    to(s"lpr:$lpraddr/$lprpath?$lproptions")
  }
}

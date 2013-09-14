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

package cloud.lib

import cloud.workflow.controller.ControlEvent
import org.apache.camel.scala.dsl.builder.RouteBuilder
import org.apache.camel.scala.Preamble

trait Workflow extends Preamble {
  val group: String

  val error_channel = s"jms:queue:$group-error"

  def routes: Seq[RouteBuilder]
}

trait EventDrivenWorkflow extends Workflow {
  def handlers: PartialFunction[ControlEvent, Unit]
}

trait RouterWorkflow extends Workflow {
  var entryUri: String = null

  var exitUri: String = null
}

trait EndpointWorkflow extends Workflow {
  var entryUri: String = null
}

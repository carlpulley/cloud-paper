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

package cloud.workflow.routers

import akka.camel.CamelMessage
import cloud.lib.Helpers
import cloud.lib.RouterWorkflow
import org.apache.camel.Exchange
import org.apache.camel.scala.dsl.builder.RouteBuilder
import scala.collection.JavaConversions._

class Transform(val group: String, name: String, map: CamelMessage => CamelMessage) extends RouterWorkflow {
  entryUri = s"direct:$group-$name-transform-entry"
  exitUri = s"direct:$group-$name-transform-exit"

  val routes = Seq(new RouteBuilder {
    entryUri ==> {
      errorHandler(deadLetterChannel(error_channel))

      transform((ex: Exchange) => map(new CamelMessage(ex.in, mapAsScalaMap(ex.getIn.getHeaders))))
      to(exitUri)
    }
  })
}

object Transform extends Helpers {
  def apply(map: CamelMessage => CamelMessage)(implicit group: String) = {
    new Transform(group, getUniqueName(group), map)
  }
}

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

package cloud

package workflow

package routers

import akka.camel.CamelMessage
import cloud.lib.Helpers
import cloud.lib.RouterWorkflow
import org.apache.camel.Exchange
import org.apache.camel.scala.dsl.builder.RouteBuilder
import scala.collection.JavaConversions._

class UnitTest(val group: String, name: String, pred: CamelMessage => Boolean, success: Option[String], failure: String) extends RouterWorkflow {
  entryUri = s"direct:$group-$name-unit-test-entry"
  exitUri = s"direct:$group-$name-unit-test-exit"

  val routes = Seq(new RouteBuilder {
    entryUri ==> {
      errorHandler(deadLetterChannel(error_channel))

      when((ex: Exchange) => pred(new CamelMessage(ex.in, mapAsScalaMap(ex.getIn.getHeaders)))) {
        when((ex: Exchange) => success.isDefined) {
          setBody(s"<feedback><item id='$name'><comment>${success.get}</comment></item></feedback>")
        }
      } 
      otherwise {
        setBody(s"<feedback><item id='$name'><comment>$failure</comment></item></feedback>")
      }
      to(exitUri)
    }
  })
}

object UnitTest extends Helpers {
  def apply(pred: CamelMessage => Boolean, success: Option[String], failure: String)(implicit group: String) = {
    new UnitTest(group, getUniqueName(group), pred, success, failure)
  }
}

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

package cloud.workflow.test

import org.apache.camel.CamelContext
import org.apache.camel.model.RouteDefinition
import org.apache.camel.scala.dsl.builder.RouteBuilder
import org.apache.camel.scala.dsl.builder.RouteBuilderSupport
import org.apache.camel.test.junit4.CamelTestSupport
import org.scalatest.BeforeAndAfter
import org.scalatest.FunSuiteLike
import scala.collection.JavaConversions._
import scala.language.implicitConversions
import scala.util.matching.Regex

class CamelContextWrapper(context: CamelContext) {
  def getRouteDefinitions(pattern: String): List[RouteDefinition]  = {
    context.getRouteDefinitions.toList.filter(rd => rd.toString contains(pattern))
  }
}

trait ScalaTestSupport extends CamelTestSupport with RouteBuilderSupport with FunSuiteLike with BeforeAndAfter {
  val builders: Seq[RouteBuilder]

  override protected def createRouteBuilders = builders.map(scalaToJavaBuilder _).toArray

  override protected def getMockEndpoint(uri: String) = super.getMockEndpoint(uri)

  override protected def assertMockEndpointsSatisfied() = super.assertMockEndpointsSatisfied()

  implicit def wrapCamelContext(context: CamelContext): CamelContextWrapper = new CamelContextWrapper(context)
}

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

package cloud.workflow.test

import akka.actor.ActorSystem
import akka.camel.CamelExtension
import cloud.lib.Config
import org.apache.camel.model.RouteDefinition
import org.apache.camel.CamelContext
import org.apache.camel.component.mock.MockEndpoint
import org.apache.activemq.ActiveMQConnectionFactory
import org.apache.camel.component.jms.JmsComponent
import org.scalatest.BeforeAndAfter
import org.scalatest.BeforeAndAfterAll
import org.scalatest.FunSuiteLike
import org.scalatest.MustMatchers
import scala.collection.JavaConversions._
import scala.language.implicitConversions
import scalaz.camel.akka._
import scalaz.camel.core._
import scalaz.concurrent.Strategy

class CamelContextWrapper(context: CamelContext) {
  def getRouteDefinitions(pattern: String): List[RouteDefinition]  = {
    context.getRouteDefinitions.toList.filter(rd => rd.toString contains(pattern))
  }
}

trait ScalaTestSupport extends Camel with Akka with FunSuiteLike with MustMatchers with BeforeAndAfterAll with BeforeAndAfter {
  dispatchConcurrencyStrategy = Strategy.Sequential
  multicastConcurrencyStrategy = Strategy.Sequential

  implicit val group = "default"
  Config.setValue("group", group)
  implicit val system = ActorSystem(group)
  val camel = CamelExtension(system)

  val connectionFactory = new ActiveMQConnectionFactory("vm://localhost?broker.persistent=false")
  camel.context.addComponent("jms", JmsComponent.jmsComponentAutoAcknowledge(connectionFactory))
  camel.context.setTracing(true)

  val template = camel.context.createProducerTemplate

  implicit val router = new Router(camel.context)

  router.start

  def getMockEndpoint(uri: String) = camel.context.getEndpoint(uri, classOf[MockEndpoint])

  implicit def wrapCamelContext(context: CamelContext): CamelContextWrapper = new CamelContextWrapper(context)
}

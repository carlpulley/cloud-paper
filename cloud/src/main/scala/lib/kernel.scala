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

package cloud.lib

import akka.actor.ActorSystem
import akka.camel.CamelExtension
import akka.kernel.Bootable
import com.typesafe.config.ConfigFactory
import org.apache.activemq.ActiveMQConnectionFactory
import org.apache.camel.component.jms.JmsComponent
import scala.collection.JavaConversions._
import scalaz.camel.core.Router

class Kernel(name: String = "default", port: Int = 2552) extends Bootable {
  implicit val group = name.toLowerCase
  val config = ConfigFactory.load("application.conf").withFallback(ConfigFactory.parseMap(Map("akka.remote.server.port" -> port)))
  implicit val system = ActorSystem(name, config)
  val camel = CamelExtension(system)
  val connectionFactory = new ActiveMQConnectionFactory("vm://localhost?broker.persistent=false")
  camel.context.addComponent("jms", JmsComponent.jmsComponentAutoAcknowledge(connectionFactory))
  implicit val router = new Router(camel.context)

  def startup = {
    router.start
  }
 
  def shutdown = {
    router.stop
    system.shutdown
  }
}

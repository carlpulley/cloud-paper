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

package cloud.transport.controller

import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.Props
import cloud.lib.Image
import cloud.transport.dsl.VMInstance
import scalaz.camel.core.Conv.MessageRoute

trait ControlEvent
case class StartVM(image: Image) extends ControlEvent

class ControlBus(handlers: PartialFunction[ControlEvent, MessageRoute]) extends Actor {
  def receive = {
    case StartVM(image) => {
      sender ! context.actorOf(Props(new VMInstance(image)).withDispatcher("akka.actor.cloud-dispatcher"), image.group)
    }

    case msg: ControlEvent => {
      handlers(msg)
    }
  }
}

object ControlBus {
  def apply(handlers: PartialFunction[ControlEvent, MessageRoute] = Map.empty)(implicit system: ActorSystem) = {
    system.actorOf(Props(new ControlBus(handlers)), "control-bus")
  }
}

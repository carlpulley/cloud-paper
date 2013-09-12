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

package actors

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.Actor.Receive
import akka.actor.PoisonPill
import akka.actor.Props
import akka.camel.Ack
import akka.camel.CamelMessage
import cloud.lib.Image
import org.jclouds.compute.domain.NodeMetadata
import scala.concurrent.Future

trait ControlEvent

case class StartVM(image: Image) extends ControlEvent
case class BuildVM(caller: ActorRef) extends ControlEvent
case class StopVM(vm: ActorRef) extends ControlEvent
case class VMStarted(node: NodeMetadata) extends ControlEvent
case class AddHandlers(events: PartialFunction[ControlEvent, Unit]) extends ControlEvent

class VMInstance(image: Image) extends Actor {
  import context.dispatcher

  override def postStop() = {
    image.shutdown()
  }

  def booting: Receive = {
    case BuildVM(caller) => {
      context.become(waiting)
      val nodeF = Future {
        image.bootstrap()
      }
      // FIXME: when bootstrap returns, is the VM really up and running?
      nodeF map { node =>
        caller ! VMStarted(node)
        context.become(running)
      }
    }
  }

  def waiting: Receive = Actor.emptyBehavior

  def running: Receive = Actor.emptyBehavior

  def receive = booting
}

class ControlBus extends Actor {
  var handlers: PartialFunction[ControlEvent, Unit] = Map.empty

  def receive = {
    case StartVM(image) => {
      val vm = context.actorOf(Props(new VMInstance(image)), image.group)
      vm ! BuildVM(sender)
      sender ! CamelMessage(vm, Map())
    }

    case StopVM(vm) => {
      vm ! PoisonPill
      sender ! Ack
    }

    case AddHandlers(events) => {
      handlers = handlers orElse events
    }

    case msg: ControlEvent => {
      handlers(msg)
    }
  }
}

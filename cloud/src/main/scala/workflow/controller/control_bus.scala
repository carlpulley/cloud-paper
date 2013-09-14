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

package controller

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.Actor.Receive
import akka.actor.ActorSystem
import akka.actor.PoisonPill
import akka.actor.Props
import akka.camel.Ack
import akka.camel.CamelMessage
import cloud.lib.EventDrivenWorkflow
import cloud.lib.Image
import org.jclouds.compute.domain.NodeMetadata
import scala.concurrent.Future

trait ControlEvent
// Cloud compute instance interactions
case class StartVM(image: Image) extends ControlEvent
case class BuildVM(caller: ActorRef) extends ControlEvent
case class StopVM(vm: ActorRef) extends ControlEvent
case class VMStarted(node: NodeMetadata) extends ControlEvent
// Dynamic control event interactions
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

  // FIXME: should we add in any extra behaviour here?
  def running: Receive = Actor.emptyBehavior

  def receive = booting
}

class ControlBus(group: String, plugins: EventDrivenWorkflow*) extends Actor {
  var handlers: PartialFunction[ControlEvent, Unit] = Map.empty //plugins.foldLeft(Map.empty)((hs, h) => hs orElse h.handlers)

  //plugins.foreach(_.routes.foreach(camel_context.addRoutes(_)))

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

object ControlBus {
  def apply(plugins: EventDrivenWorkflow*)(implicit group: String, system: ActorSystem) = {
    system.actorOf(Props(new ControlBus(group, plugins: _*)), "control-bus")
  }
}

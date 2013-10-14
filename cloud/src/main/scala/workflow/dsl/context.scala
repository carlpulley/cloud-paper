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

package cloud.workflow.dsl

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.Actor.Receive
import akka.actor.ActorSystem
import akka.actor.AddressFromURIString
import akka.actor.Deploy
import akka.actor.PoisonPill
import akka.actor.Props
import akka.actor.Stash
import akka.camel.Ack
import akka.pattern.ask
import akka.remote.RemoteScope
import akka.util.Timeout
import cloud.lib.Image
import cloud.lib.Workflow
import cloud.workflow.controller.ControlEvent
import cloud.workflow.controller.StartVM
import org.jclouds.compute.domain.NodeMetadata
import scala.collection.JavaConversions._
import scala.collection.mutable
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.Future
import scala.language.postfixOps
import scalaz._
import scalaz.camel.core.Conv.MessageRoute
import scalaz.camel.core._

// Cloud compute instance interactions
case class AddWorkflow(workflow: (String, MessageRoute)) extends ControlEvent

object VMStarted extends ControlEvent

// This actor provides a bridge between the parent compute node's VMInstance actor and this actor's encapsulated workflow
// The actor is started when the compute node boots (as part of a Chef configured service script)
class WorkflowEndpoint(workflow: MessageRoute) extends Actor with Camel {
  def receive = {
    case msg: Message =>
      sender ! (workflow process msg)
  }
}

// This actor is used to control and interact with the cloud compute node that it launches
// Note: instances are launched with the cloud-dispatcher so that we have some tolerance when
//       code blocks during cloud image spin up
class VMInstance(image: Image) extends Actor with Stash {
  import context.dispatcher

  private[this] var node: Option[NodeMetadata] = None

  override def preStart() = {
    node = Some(image.bootstrap())
    self ! VMStarted
  }

  override def postStop() = {
    image.shutdown()
  }

  def booting: Receive = {
    case VMStarted => {
      unstashAll()
      context.become(running)
    }
    case msg =>
      // Stash all other messages until we are running
      stash()
  }

  def running: Receive = {
    case AddWorkflow((name, workflow)) => {
      val host = node.get.getPublicAddresses.head
      val addr = AddressFromURIString(s"akka.tcp://$host/user/${image.group}/${name}")
  
      sender ! context.actorOf(Props(new WorkflowEndpoint(workflow)).withDeploy(Deploy(scope = RemoteScope(addr))), name)
    }
  }

  def receive = booting
}

object Context extends Workflow {
  private[this] val vm: mutable.Map[Image, ActorRef] = mutable.Map()

  def apply(image: Image, workflow: (String, MessageRoute))(implicit router: Router, controller: ActorRef, system: ActorSystem, timeout: Timeout = Timeout(10 minutes)): MessageRoute = {
    if (! vm.contains(image)) {
      val node = (controller ? StartVM(image)).mapTo[ActorRef]
      vm(image) = Await.result(node, timeout.duration)
    }
    val endpoint = (vm(image) ? AddWorkflow(workflow)).mapTo[ActorRef]
    
    to(Await.result(endpoint, timeout.duration))
  }
}

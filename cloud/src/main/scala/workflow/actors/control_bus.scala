package cloud

package workflow

package actors

import cloud.lib.ActorWorkflow
import cloud.lib.Image
import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.Actor.Receive
import akka.actor.ActorSystem
import akka.actor.PoisonPill
import akka.actor.Props
import akka.camel.Ack
import akka.camel.CamelMessage
import org.jclouds.compute.domain.NodeMetadata

// TODO: add control events to:
//   1. query provider for currently running VMs
//   2. attaching VMInstance actors to orphaned VMs
// FIXME: how do we determine if a VM is orphaned or not?

trait ControlEvent

case class StartVM(image: Image) extends ControlEvent
case class BuildVM(caller: ActorRef) extends ControlEvent
case class StopVM(vm: ActorRef) extends ControlEvent
case class VMStarted(node: NodeMetadata) extends ControlEvent

class VMInstance(image: Image) extends Actor {
  override def postStop() = {
    image.shutdown()
  }

  def booting: Receive = {
    case BuildVM(caller) => {
      context.become(waiting)
      // TODO: run following in a future!?
      // FIXME: how do we know that VM is up and running?
      val node = image.bootstrap()
      // TODO: add in InetAddress instance to VMStarted message?
      caller ! VMStarted(node)
      context.become(running)
    }
  }

  def waiting: Receive = Actor.emptyBehavior

  def running: Receive = Actor.emptyBehavior

  def receive = booting
}

class ControlBus(system: ActorSystem) extends ActorWorkflow {
  def endpointUri = "direct:control_bus"

  def receive = {
    case CamelMessage(StartVM(image), _) => {
      val vm = system.actorOf(Props(new VMInstance(image)), image.group)
      vm ! BuildVM(sender)
      sender ! CamelMessage(vm, Map())
    }

    case CamelMessage(StopVM(vm), _) => {
      vm ! PoisonPill
      sender ! Ack
    }
  }
}

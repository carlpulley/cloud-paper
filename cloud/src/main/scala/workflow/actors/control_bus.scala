package cloud

package workflow

package actors

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.Actor.Receive
import akka.actor.ActorSystem
import akka.actor.PoisonPill
import akka.actor.Props
import akka.camel.Ack
import akka.camel.CamelMessage
import cloud.lib.ActorWorkflow
import cloud.lib.Image
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
case class AddHandlers(events: PartialFunction[ControlEvent, Unit]) extends ControlEvent

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
      caller ! VMStarted(node)
      context.become(running)
    }
  }

  def waiting: Receive = Actor.emptyBehavior

  def running: Receive = Actor.emptyBehavior

  def receive = booting
}

class ControlBus extends ActorWorkflow {
  def endpointUri = "direct:control_bus"

  // We intentionally do not receive/handle CamelMessage messages within this actor!

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

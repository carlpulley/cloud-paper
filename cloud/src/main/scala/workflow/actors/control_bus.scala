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

trait ControlEvent

case class BuildVM(image: Image) extends ControlEvent
case class StartVM(vm: ActorRef) extends ControlEvent
case class StopVM(vm: ActorRef) extends ControlEvent

class VMInstance(image: Image) extends Actor {
  override def postStop() = {
    image.shutdown()
  }

  def booting: Receive = {
    case StartVM(caller) => {
      image.bootstrap()
      // TODO: run following once we've booted - use a future!?
      context.become(running)
      caller ! Ack
    }
  }

  def running: Receive = Actor.emptyBehavior

  def receive = booting
}

class ControlBus(system: ActorSystem) extends ActorWorkflow {
  def endpointUri = "direct:control_bus"

  def receive = {
    case CamelMessage(BuildVM(image), _) => {
      sender ! CamelMessage(system.actorOf(Props(new VMInstance(image)), image.group), Map())
    }
    case CamelMessage(StartVM(vm), _) => {
      vm ! StartVM(sender)
    }
    case CamelMessage(StopVM(vm), _) => {
      vm ! PoisonPill
    }
  }
}

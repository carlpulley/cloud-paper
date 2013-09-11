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
  // We intentionally do not wish this class to be an ActorWorkflow!

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

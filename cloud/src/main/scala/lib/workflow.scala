package cloud.lib

import akka.actor.Actor
import org.apache.camel.scala.dsl.builder.RouteBuilder

trait Workflow

trait ActorWorkflow extends Actor with Workflow

trait RouterWorkflow extends Workflow {
  val rootUri: String

  def routes: Seq[RouteBuilder]
}

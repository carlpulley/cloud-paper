package cloud.lib

import akka.actor.Actor
import akka.camel.Consumer
import org.apache.camel.scala.dsl.builder.RouteBuilder

trait Workflow {
  def endpointUri: String
}

trait ActorWorkflow extends Consumer with Workflow

trait RouterWorkflow extends Workflow {
  def routes: Seq[RouteBuilder]
}

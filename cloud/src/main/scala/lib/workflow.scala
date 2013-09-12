package cloud.lib

import org.apache.camel.scala.dsl.builder.RouteBuilder

trait Workflow {
  def entryUri: String

  def routes: Seq[RouteBuilder]
}

trait RouterWorkflow extends Workflow {
  def exitUri: String
}

trait EndpointWorkflow extends Workflow

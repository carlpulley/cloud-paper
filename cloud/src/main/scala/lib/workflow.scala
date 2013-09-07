package cloud.lib

import org.apache.camel.scala.dsl.builder.RouteBuilder

trait Workflow {
  def route: RouteBuilder
}

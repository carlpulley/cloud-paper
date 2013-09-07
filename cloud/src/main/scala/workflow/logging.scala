package cloud

package workflow

import cloud.lib.Workflow
import org.apache.camel.scala.dsl.builder.RouteBuilder

class Logging(label: String, showAll: Boolean = false) extends Workflow {
  def route = new RouteBuilder {
    "direct:%s".format(label) ==> {
      "log:%s?showAll=%s".format(label, showAll)
    }
  }
}

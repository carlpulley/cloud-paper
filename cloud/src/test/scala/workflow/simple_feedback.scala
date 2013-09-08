package cloud

package workflow

import cloud.lib.Workflow
import org.apache.camel.Exchange
import org.apache.camel.scala.dsl.builder.RouteBuilder

class SimpleFeedback(label: String) extends Workflow {
  def simpleFeedback = { (exchange: Exchange) =>
    val newBody = exchange.getIn.getBody.asInstanceOf[String].replaceAll("Submission", "Feedback")
    exchange.getIn.setBody(newBody)
  }

  def route = new RouteBuilder {
    "direct:%s".format(label) ==> {
      transform(simpleFeedback)
    }
  }
}

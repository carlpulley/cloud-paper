package cloud.workflow

package test

import cloud.lib.RouterWorkflow
import org.apache.camel.Exchange
import org.apache.camel.scala.dsl.builder.RouteBuilder

class SimpleFeedback(id: Int) extends RouterWorkflow {
  def simpleFeedback = { (exchange: Exchange) =>
    val newBody = exchange.getIn.getBody(classOf[String]).replaceAll("Submission", "Feedback")
    s"<feedback><item id='$id'><comment>$newBody</comment></item></feedback>"
  }

  def endpointUri = "direct:simple_feedback"

  def routes = Seq(new RouteBuilder {
    endpointUri ==> {
      transform(simpleFeedback)
    }
  })
}

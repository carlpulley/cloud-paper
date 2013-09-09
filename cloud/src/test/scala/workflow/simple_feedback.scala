package cloud

package workflow

import cloud.lib.RouterWorkflow
import org.apache.camel.Exchange
import org.apache.camel.scala.dsl.builder.RouteBuilder

object SimpleFeedback extends RouterWorkflow {
  def simpleFeedback = { (exchange: Exchange) =>
    val newBody = exchange.getIn.getBody(classOf[String]).replaceAll("Submission", "Feedback")
    s"<feedback><item id='1'><comment>$newBody</comment></item></feedback>"
  }

  def endpointUri = "direct:simple_feedback"

  def routes = Seq(new RouteBuilder {
    endpointUri ==> {
      transform(simpleFeedback)
    }
  })
}

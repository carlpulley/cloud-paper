package cloud.workflow

package test

import cloud.lib.RouterWorkflow
import org.apache.camel.Exchange
import org.apache.camel.scala.dsl.builder.RouteBuilder

class SimpleFeedback(id: Int, typ: Symbol = 'flat) extends RouterWorkflow {
  def simpleFeedback = { (exchange: Exchange) =>
    typ match {
      case 'flat => {
        val newBody = exchange.getIn.getBody(classOf[String]).replaceAll("Submission", "Feedback")
        s"<feedback><item id='$id'><comment>$newBody</comment></item></feedback>"
      }

      case 'structured => {
        val (oldBody, _) = exchange.getIn.getBody(classOf[(String, List[String])])
        val newBody = oldBody.replaceAll("Submission", "Feedback")
        s"<feedback><item id='$id'><comment>$newBody</comment></item></feedback>"
      }
    }
  }

  def entryUri = s"direct:simple-feedback${id}-entry"

  def exitUri = s"direct:simple-feedback${id}-exit"

  def routes = Seq(new RouteBuilder {
    entryUri ==> {
      transform(simpleFeedback)
      to(exitUri)
    }
  })
}

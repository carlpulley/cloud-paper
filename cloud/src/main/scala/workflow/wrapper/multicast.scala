package cloud

package workflow

package wrapper

import cloud.workflow.actors.ControlEvent
import cloud.lib.RouterWorkflow
import cloud.lib.Workflow
import scala.concurrent.duration._
import org.apache.camel.Exchange
import org.apache.camel.processor.aggregate.AbstractListAggregationStrategy
import org.apache.camel.scala.dsl.builder.RouteBuilder

case class AddWorkflow(workflow: Workflow) extends ControlEvent
case class RemoveWorkflow(workflow: Workflow) extends ControlEvent

class ListAggregationStrategy extends AbstractListAggregationStrategy[String] {
  def getValue(exchange: Exchange): String = {
    exchange.getIn.getHeader("clientId").asInstanceOf[String]
  }
}

// TODO: want to extend control bus to allow dynamically adding/removing child workflows

class Multicast(name: String, children: List[RouterWorkflow], timeout: Duration = 3 seconds) extends RouterWorkflow {
  val rootUri = "direct:%s".format(name)

  def generateFeedback = { (exchange: Exchange) =>
    // TODO: use Scala's HTML/XML processing?
    val item_list = exchange.getIn.getBody.asInstanceOf[List[String]].reduceLeft((items, item) => s"$items</li>\n  <li>$item")
    exchange.getIn.setBody(s"<ul>\n  <li>$item_list</li>\n</ul>")
  }

  def routes = Seq(new RouteBuilder {
    rootUri ==> {
      // FIXME: need to content enrich message using control buses message store
      errorHandler(deadLetterChannel("jms:queue:error"))
      aggregate(header("clientId"), new ListAggregationStrategy()).completionTimeout(timeout.toMillis)
      process(generateFeedback)
    }
  
    for(child <- children) {
      s"jms:topic:$name" ==> {
        to(child.rootUri)
      }
    }
  }) ++ children.flatMap(_.routes)
}

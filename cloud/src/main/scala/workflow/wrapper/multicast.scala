package cloud

package workflow

package wrapper

import cloud.lib.Workflow
import scala.concurrent.duration._
import org.apache.camel.Exchange
import org.apache.camel.processor.aggregate.AbstractListAggregationStrategy
import org.apache.camel.scala.dsl.builder.RouteBuilder

class ListAggregationStrategy extends AbstractListAggregationStrategy[String] {
  def getValue(exchange: Exchange): String = {
    "FIXME"
  }
}

class Multicast(name: String, children: List[Workflow], timeout: Duration = 3 seconds) extends Workflow {
  def generateFeedback = { (exchange: Exchange) =>
    // TODO:
  }

  def route = new RouteBuilder {
    "direct:%s".format(name) ==> {
      // FIXME: need to content enrich message using control buses message store
      errorHandler(deadLetterChannel("jms:queue:error"))
      to("jms:queue:test")
      aggregate(header("clientId"), new ListAggregationStrategy()).completionTimeout(timeout.toMillis)
      process(generateFeedback)
    }
  
    for(child <- children) {
      "jms:topic:%s".format(name) ==> {
        child.route
      }
    }
  }
}

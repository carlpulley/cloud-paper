package cloud

package workflow

package endpoints

import cloud.lib.EndpointWorkflow
import com.typesafe.config._
import org.apache.camel.scala.dsl.builder.RouteBuilder

class Printer extends EndpointWorkflow {
  private[this] val config: Config = ConfigFactory.load("application.conf")

  private[this] val subject  = config.getString("feedback.subject")

  def entryUri = "direct:printer_endpoint"

  def routes = Seq(new RouteBuilder {
    entryUri ==> {
        setHeader("student", header("replyTo"))
        setHeader("title", subject)
        to("xslt:feedback-printer.xsl")
        to("fop:application/pdf")
        to("lpr:localhost/default?sides=two-sided")
        stop
    }
  })
}

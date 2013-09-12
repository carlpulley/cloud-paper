package cloud

package workflow

package endpoints

import cloud.lib.EndpointWorkflow
import com.typesafe.config._
import org.apache.camel.scala.dsl.builder.RouteBuilder

class HTTP extends EndpointWorkflow {
  private[this] val config: Config = ConfigFactory.load("application.conf")

  private[this] val subject  = config.getString("feedback.subject")
  private[this] val webhost  = config.getString("web.host")
  private[this] val webuser  = config.getString("web.user")

  def entryUri = "direct:web_endpoint"

  def routes = Seq(new RouteBuilder {
    // NOTES:
    //   1. we assume that SSH certificates have been setup to allow passwordless login to $webuser@$webhost
    //   2. we also assume that Apache (or similar) can serve pages from ~$webuser/www/$crypto_link via the 
    //      URL https://$webhost/$webuser/$crypto_link
    //   3. here the message body contains the $crypto_link file contents which are transformed from XML to HTML
    entryUri ==> {
        setHeader("student", header("replyTo"))
        setHeader("title", subject)
        to("xslt:feedback-file.xsl")
        setHeader("CamelFileName", header("sha256"))
        to("sftp:%s@%s/www/".format(webuser, webhost))
        stop
    }
  })
}

package cloud

package workflow

package endpoints

import cloud.lib.EndpointWorkflow
import com.typesafe.config._
import org.apache.camel.scala.dsl.builder.RouteBuilder

class Twitter extends EndpointWorkflow {
  private[this] val config: Config = ConfigFactory.load("application.conf")

  private[this] val subject           = config.getString("feedback.subject")
  private[this] val consumerKey       = config.getString("twitter.consumer.key")
  private[this] val consumerSecret    = config.getString("twitter.consumer.secret")
  private[this] val accessToken       = config.getString("twitter.access.token")
  private[this] val accessTokenSecret = config.getString("twitter.access.secret")

  def entryUri = "direct:twitter_endpoint"

  def routes = Seq(new RouteBuilder {
    entryUri ==> {
        setHeader("student", header("replyTo"))
        setHeader("title", subject)
        to("velocity:feedback-twitter.vm")
        to(s"twitter:directmessage?consumerKey=$consumerKey&consumerSecret=$consumerSecret&accessToken=$accessToken&accessTokenSecret=$accessTokenSecret")
        stop
    }
  })
}

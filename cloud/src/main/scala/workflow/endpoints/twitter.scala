// Copyright (C) 2013  Carl Pulley
// 
// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU General Public License
// as published by the Free Software Foundation; either version 2
// of the License, or (at your option) any later version.
// 
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.

package cloud

package workflow

package endpoints

import cloud.lib.EndpointWorkflow
import cloud.lib.Helpers
import com.typesafe.config._
import org.apache.camel.scala.dsl.builder.RouteBuilder

class Twitter(val group: String) extends EndpointWorkflow with Helpers {
  private[this] val config = getConfig(group)

  private[this] val subject           = config.getString("feedback.subject")
  private[this] val consumerKey       = config.getString("twitter.consumer.key")
  private[this] val consumerSecret    = config.getString("twitter.consumer.secret")
  private[this] val accessToken       = config.getString("twitter.access.token")
  private[this] val accessTokenSecret = config.getString("twitter.access.secret")

  entryUri = s"direct:$group-twitter-entry"

  val routes = Seq(new RouteBuilder {
    entryUri ==> {
        setHeader("student", header("replyTo"))
        setHeader("title", subject)
        to(s"velocity:$group/feedback-twitter.vm")
        to(s"twitter:directmessage?consumerKey=$consumerKey&consumerSecret=$consumerSecret&accessToken=$accessToken&accessTokenSecret=$accessTokenSecret")
        stop
    }
  })
}

object Twitter {
  def apply()(implicit group: String) = {
    new Twitter(group)
  }
}

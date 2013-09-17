// Copyright (C) 2013  Carl Pulley
// 
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
// 
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with this program. If not, see <http://www.gnu.org/licenses/>.

package cloud.workflow.producer

import cloud.lib.Config
import cloud.lib.Workflow
import scalaz._
import scalaz.camel.core._

object Twitter extends Workflow {
  import Scalaz._

  def apply()(implicit group: String, router: Router): MessageRoute = {
    val config = Config(group)
  
    val consumerKey       = config[String]("twitter.consumer.key")
    val consumerSecret    = config[String]("twitter.consumer.secret")
    val accessToken       = config[String]("twitter.access.token")
    val accessTokenSecret = config[String]("twitter.access.secret")

    { msg: Message => msg.addHeaders(Map("student" -> msg.headerAs[String]("replyTo").get, "module" -> group)) } >=>
    to(s"velocity:$group/feedback-twitter.vm") >=>
    to(s"twitter:directmessage?consumerKey=$consumerKey&consumerSecret=$consumerSecret&accessToken=$accessToken&accessTokenSecret=$accessTokenSecret")
  }
}

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

class HTTP(val group: String) extends EndpointWorkflow with Helpers {
  private[this] val config = getConfig(group)

  private[this] val subject  = config.getString("feedback.subject")
  private[this] val webhost  = config.getString("web.host")
  private[this] val webuser  = config.getString("web.user")

  entryUri = s"direct:$group-http-entry"

  val routes = Seq(new RouteBuilder {
    // NOTES:
    //   1. we assume that SSH certificates have been setup to allow passwordless login to $webuser@$webhost
    //   2. we also assume that Apache (or similar) can serve pages from ~$webuser/www/$crypto_link via the 
    //      URL https://$webhost/$webuser/$crypto_link
    //   3. here the message body contains the $crypto_link file contents which are transformed from XML to HTML
    entryUri ==> {
        setHeader("student", header("replyTo"))
        setHeader("title", subject)
        to(s"xslt:$group/feedback-file.xsl")
        setHeader("CamelFileName", header("sha256"))
        to(s"sftp:$webuser@$webhost/www/$group")
        stop
    }
  })
}

object HTTP {
  def apply()(implicit group: String) = {
    new HTTP(group)
  }
}

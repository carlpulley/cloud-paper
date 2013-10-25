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

object HTTP extends Workflow {
  import Scalaz._

  def apply()(implicit group: String, router: Router): MessageRoute = {
    val config = Config(group)
  
    val webhost = config.get[String]("web.host")
    val webuser = config.get[String]("web.user")

    // NOTES:
    //   1. we assume that SSH certificates have been setup to allow passwordless login to $webuser@$webhost
    //   2. we also assume that Apache (or similar) can serve pages from ~$webuser/www/$crypto_link via the 
    //      URL https://$webhost/$webuser/$crypto_link
    //   3. here the message body contains the $crypto_link file contents which are transformed from XML to HTML
    { msg: Message => msg.addHeaders(Map("student" -> msg.headerAs[String]("replyTo").get, "module" -> group)) } >=>
    to(s"xslt:$group/feedback-file.xsl") >=>
    { msg: Message => msg.setHeaders(Map("CamelFileName" -> msg.headerAs[String]("sha256").get)) } >=>
    to(s"sftp:$webuser@$webhost/www/$group")
  }
}

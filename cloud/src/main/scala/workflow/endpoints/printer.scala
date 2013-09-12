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

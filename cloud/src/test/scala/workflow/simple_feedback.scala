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

package cloud.workflow

package test

import cloud.lib.RouterWorkflow
import org.apache.camel.Exchange
import org.apache.camel.scala.dsl.builder.RouteBuilder
import scala.concurrent.duration._

class SimpleFeedback(val group: String, id: Int, typ: Symbol) extends RouterWorkflow {
  entryUri = s"direct:simple-feedback${id}-entry"
  exitUri = s"direct:simple-feedback${id}-exit"

  def simpleFeedback = { (exchange: Exchange) =>
    typ match {
      case 'flat => {
        val newBody = exchange.in[String].replaceAll("Submission", "Feedback")
        s"<feedback><item id='$id'><comment>$newBody</comment></item></feedback>"
      }

      case 'structured => {
        val (oldBody, _) = exchange.in[(String, List[String])]
        val newBody = oldBody.replaceAll("Submission", "Feedback")
        s"<feedback><item id='$id'><comment>$newBody</comment></item></feedback>"
      }

      case 'structured_delay => {
        val (oldBody, _) = exchange.in[(String, List[String])]
        val delay_period = exchange.in("delay").asInstanceOf[String]
        if (delay_period != null) {
          // Simulate a blocking delay in processing
          Thread.sleep(new DurationInt(delay_period.toInt).seconds.toMillis)
        }
        val newBody = oldBody.replaceAll("Submission", "Feedback")
        s"<feedback><item id='$id'><comment>$newBody</comment></item></feedback>"
      }
    }
  }

  def routes = Seq(new RouteBuilder {
    entryUri ==> {
      transform(simpleFeedback)
      to(exitUri)
    }
  })
}

object SimpleFeedback {
  def apply(id: Int, typ: Symbol = 'flat)(implicit group: String) = {
    new SimpleFeedback(group, id, typ)
  }
}

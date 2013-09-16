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

package cloud.workflow

package test

import cloud.lib.Workflow
import scala.concurrent.duration._
import scalaz._
import scalaz.camel.core._

object SimpleFeedback extends Workflow {
  def apply(id: Int, typ: Symbol = 'flat)(implicit group: String, router: Router): MessageRoute = {
    def simpleFeedback(id: Int, typ: Symbol) = { msg: Message =>
      typ match {
        case 'flat => {
          val newBody = msg.bodyAs[String].replaceAll("Submission", "Feedback")
          msg.setBody(s"<feedback><item id='$id'><comment>$newBody</comment></item></feedback>")
        }
  
        case 'structured => {
          val (oldBody, _) = msg.bodyAs[(String, List[String])]
          val newBody = oldBody.replaceAll("Submission", "Feedback")
          msg.setBody(s"<feedback><item id='$id'><comment>$newBody</comment></item></feedback>")
        }
  
        case 'structured_delay => {
          val (oldBody, _) = msg.bodyAs[(String, List[String])]
          val delay_period = msg.headerAs[Int]("delay").getOrElse(0)
          // Simulate a blocking delay during processing
          Thread.sleep(new DurationInt(delay_period).seconds.toMillis)
  
          val newBody = oldBody.replaceAll("Submission", "Feedback")
          msg.setBody(s"<feedback><item id='$id'><comment>$newBody</comment></item></feedback>")
        }
      }
    }

    simpleFeedback(id, typ)
  }
}

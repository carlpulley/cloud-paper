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

package example

import akka.actor.ActorRef
import akka.actor.ActorSystem
import cloud.lib.Config
import cloud.lib.Kernel
import cloud.lib.Workflow
import cloud.workflow.controller.ControlBus
import cloud.workflow.consumer.Dropbox
import cloud.workflow.controller.SubmissionTable
import cloud.workflow.controller.FeedbackTable
import cloud.workflow.Submission
import scala.concurrent.duration._
import scalikejdbc.ConnectionPool
import scalaz._
import scalaz.camel.core.Conv.MessageRoute
import scalaz.camel.core.Router

trait Creator {
    def apply()(implicit group: String, router: Router, controller: ActorRef, system: ActorSystem, timeout: Duration = 10.minutes): MessageRoute
}

abstract class AssessmentKernel(assessment: Creator) extends Kernel {
  override def startup = {
    super.startup

    import Scalaz._
    
    val config = Config.load("application.conf")
    
    val sqldriver = config.get[String]("sql.driver")
    val sqlurl    = config.get[String]("sql.url")
    val sqluser   = config.get[String]("sql.user")
    val sqlpw     = config.get[String]("sql.password")
    val dropbox   = config.get[String]("workflow.dropbox")

    Class.forName(sqldriver)
    ConnectionPool.singleton(sqlurl, sqluser, sqlpw)

    if (! SubmissionTable.exists) SubmissionTable.create
    if (! FeedbackTable.exists) FeedbackTable.create

    // On-demand dropbox submissions
    Dropbox(dropbox)

    implicit val controller = ControlBus()

    // Mock feedback delivery by (default) delivering to error channel
    new Submission(controller, assessment.apply()) with Dropbox
  }
}

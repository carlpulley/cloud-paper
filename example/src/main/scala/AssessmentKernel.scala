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

import cloud.lib.Kernel
import cloud.workflow.controller.ControlBus
import cloud.workflow.controller.plugin.Marking
import cloud.workflow.consumer.Imap
import cloud.workflow.consumer.Dropbox
import cloud.workflow.producer.HTTP
import cloud.workflow.producer.SMTP
import cloud.workflow.Submission
import scala.concurrent.duration._

class AssessmentKernel extends Kernel {
  override def startup = {
    super.startup

    val HOME = sys.env("HOME")
    // Only allow email submissions every 24 hours..
    implicit val poll = 24.hours
    Imap(s"INBOX.$group.Submissions")
    // .. and on-demand dropbox submissions
    Dropbox(s"$HOME/$group/Submissions")

    implicit val controller = ControlBus(Marking())

    // Feedback to be delivered via a (SHA256) web link emailed to student
    new Submission(controller, AssessmentWorkflow(), SMTP(), HTTP()) with Imap with Dropbox
  }
}

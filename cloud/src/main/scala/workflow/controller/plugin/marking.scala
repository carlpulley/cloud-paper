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

package cloud.workflow.controller.plugin

import cloud.lib.Workflow
import cloud.workflow.controller.ControlEvent
import cloud.workflow.controller.FeedbackTable
import cloud.workflow.controller.SubmissionTable
import info.folone.scala.poi._
import java.sql.Date
import scala.xml
import scala.xml.XML
import scalikejdbc.DB
import scalikejdbc.SQLInterpolation._
import scalaz._
import scalaz.camel.core._

case class GenerateFeedback(filename: String, grader: PartialFunction[String, Int]) extends ControlEvent

case class Feedback(student: String, submitted_at: Date, feedback: xml.Elem)

object Marking extends Workflow {
  import Scalaz._

  def apply()(implicit group: String, router: Router): PartialFunction[ControlEvent, MessageRoute] = {
    val config = getConfig(group)
  
    val mailhost = config.getString("mail.host")
    val mailuser = config.getString("mail.user")
    val mailpw   = config.getString("mail.password")

    val generateFeedback = { msg: Message =>
      val feedback = DB autoCommit { implicit session =>
        val last_submission = sql"""
          SELECT 
            LAST(s.created_at)
          FROM ${FeedbackTable.name} AS f
            INNER JOIN ${SubmissionTable.name} AS s ON s.id = f.submission_id
          WHERE
            module = ${group}
          ORDER BY s.created_at ASC
        """.map(_.date(1)).single.apply().get

        sql"""
          SELECT 
            s.student, s.created_at, f.message
          FROM ${FeedbackTable.name} AS f
            INNER JOIN ${SubmissionTable.name} AS s ON s.id = f.submission_id
          WHERE
            module = ${group}
            AND s.created_at = ${last_submission}
          GROUP BY
            s.student
        """.map(rs => Feedback(rs.string("student"), rs.date("created_at"), XML.loadString(rs.string("message")))).list.apply()
      }

      Message(feedback, Map("group" -> group))
    }

    { case GenerateFeedback(filename, grader) =>
        generateFeedback >=> 
        // FIXME: aggregate({ msg: Message => Message(List[(String, Date, List[Feedback])], Map("group" -> group)) }) >=>
        { msg: Message => msg.transform[List[(String, Date, List[Feedback])]](table => 
            Workbook {
              Set(
                Sheet(s"$group marking grid") {
                  Set(
                    Row(0) {
                      val hdr = table.head._3
    
                      Set(StringCell(1, "Student"), StringCell(2, "Submission Date"), StringCell(hdr.length+3, "Total")) ++
                        (hdr.zipWithIndex.map { pr => {
                          val (item, i) = pr
                          val n = item.feedback \ "feedback" \ "item" \ "@id"
    
                          StringCell(i+2, s"Question $n")
                        }})
                    }
                  ) ++
                  (for(((student, submitted_at, result), row) <- table.zipWithIndex)
                    yield Row(row+1) {
                      def grade(item: Feedback) = grader.lift((item.feedback \ "feedback" \ "item" \ "@id").toString).getOrElse(0)
  
                      Set(StringCell(1, student), StringCell(2, submitted_at.toString), NumericCell(result.length+3, result.map(grade(_)).sum)) ++
                        (result.zipWithIndex.map { pr => {
                          val (item, i) = pr
  
                          NumericCell(i, grade(item))
                        }})
                    }
                  )
                }
              )
            }.toFile(filename)
        ) }
    }
  }
}

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

package controller

package plugins

import cloud.lib.EndpointWorkflow
import org.apache.camel.processor.aggregate.UseLatestAggregationStrategy
import org.apache.camel.scala.dsl.builder.RouteBuilder

// TODO: define a CRON RE DSL

//object TagTable extends SQLTable {
//  val name = sqls"tag"
//
//  val columns = Map(
//    "id" -> "INTEGER PRIMARY KEY",
//    "name" -> "TEXT NOT NULL"
//  )
//
//  val constraints = Seq(
//    sqls"UNIQUE (name)"
//  )
//}
//
//object SubmissionTagTable extends SQLTable {
//  val name = sqls"submission_tag"
//
//  val columns = Map(
//    "tag_id" -> "INTEGER NOT NULL", 
//    "submission_id" -> "INTEGER NOT NULL",
//    "created_at" -> "TEXT NOT NULL"
//  )
//
//  val constraints = Seq(
//    sqls"FOREIGN KEY (tag_id) REFERENCES ${TagTable.name}(id)"
//    sqls"FOREIGN KEY (submission_id) REFERENCES ${SubmissionTable.name}(id)"
//  )
//}
//
//case class AddTag(name: String, submission_id: Int) extends ControlEvent
//
//class Reporting(cron: String, marker: String, endpoints: EndpointWorkflow*)(implicit controller: ActorRef) extends EventDrivenWorkflow {
//  controller ! AddHandlers {
//    case AddTag(name, submission_id) => {
//      DB autoCommit {
//        sql"UPDATE" // FIXME:
//      }
//    }
//  }
//
//  def retrieveFinalFeedback = { (exchange: Exchange) =>
//    // TODO: pull out messages that are tagged by marker, and default to latest if no such tag exists for student
//    DB autoCommit { implicit session =>
//      val students = sql"SELECT student FROM ${SubmissionTable.name}".map(_.string("student")).list.apply()
//      val messages = students.flatMap(stud => sql"""
//          SELECT 
//            s.student, s.message_id, f.message
//          FROM 
//            ${SubmissionTable.name} AS s
//            INNER JOIN ${FeedbackTable.name} AS f ON s.id = f.submission_id
//          WHERE
//            s.student = ${stud}
//          GROUP BY s.message_id
//          ORDER BY DATETIME(s.created_at) ASC
//        """.map().list.apply())
//      // TODO: and the rest!
//    }
//  }
//
//  def routes = Seq(new RouteBuilder {
//    s"quartz:marking?cron=$cron" ==> {
//      transform(retrieveFinalFeedback)
//      // FIXME: need to split retrieved feedback?
//      to(endpoints.map(_.entryUri): _*)
//    }
//  }) ++ endpoints.flatMap(_.routes)
//}
//
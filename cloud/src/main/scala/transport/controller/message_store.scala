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

package cloud.transport.controller

import cloud.lib.SQLTable
import scalikejdbc.SQLInterpolation._

object SubmissionTable extends SQLTable {
  val name = sqls"submission"

  val columns = Map(
    "id" -> "INTEGER PRIMARY KEY",
    "message_id" -> "TEXT NOT NULL",
    "module" -> "TEXT NOT NULL",
    "student" -> "TEXT NOT NULL", 
    "message" -> "TEXT NOT NULL", 
    "created_at" -> "TEXT NOT NULL"
  )

  val constraints = Seq(
    sqls"UNIQUE (message_id)"
  )
}

object FeedbackTable extends SQLTable {
  val name = sqls"feedback"

  val columns = Map(
    "id" -> "INTEGER PRIMARY KEY", 
    "submission_id" -> "INTEGER NOT NULL", 
    "sha256" -> "TEXT NOT NULL", 
    "message" -> "TEXT NOT NULL", 
    "created_at" -> "TEXT NOT NULL"
  )

  val constraints = Seq(
    sqls"FOREIGN KEY (submission_id) REFERENCES ${SubmissionTable.name}(id)"
  )
}

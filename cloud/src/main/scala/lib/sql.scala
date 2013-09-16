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

package cloud.lib

import scalikejdbc.DB
import scalikejdbc.SQL
import scalikejdbc.SQLInterpolation._

trait SQLTable {
  val name: SQLSyntax
  
  val columns: Map[String, String]

  val constraints: Seq[SQLSyntax]

  def create {
    DB autoCommit { implicit session =>
      val column_defs = columns.toList.map(p => p._1 + " " + p._2).mkString(", ")
      val constraint_defs = if (constraints.isEmpty) "" else ", " + constraints.map(_.value).mkString(", ")
      val query = s"CREATE TABLE ${name.value}(${column_defs}${constraint_defs})"
  
      SQL(query).execute.apply()
    }
  }

  def drop {
    DB autoCommit { implicit session =>
      SQL(s"DROP TABLE ${name.value}").execute.apply()
    }
  }
}

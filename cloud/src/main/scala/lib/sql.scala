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

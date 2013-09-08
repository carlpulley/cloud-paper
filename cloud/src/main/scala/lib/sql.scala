package cloud.lib

import scalikejdbc.DBSession
import scalikejdbc.SQL
import scalikejdbc.SQLInterpolation._

trait SQLTable {
  val name: SQLSyntax
  
  val columns: Map[String, String]

  val constraints: Seq[String]

  def create(implicit session: DBSession) {
    val column_defs = columns.toList.map(p => p._1 + " " + p._2).mkString(", ")
    val query = s"CREATE TABLE ${name.value}(${column_defs})"

    SQL(query).execute.apply()
  }

  def drop(implicit session: DBSession) {
    SQL(s"DROP TABLE ${name.value}").execute.apply()
  }
}

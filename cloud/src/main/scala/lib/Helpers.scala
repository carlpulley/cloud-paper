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

import java.security.MessageDigest
import java.util.Date
import org.apache.log4j.Level
import org.apache.log4j.Logger
import scalikejdbc.GlobalSettings
import scalikejdbc.LoggingSQLAndTimeSettings
import scala.util.Random

trait Helpers {
  private[this] val rand = new Random(new java.security.SecureRandom())

  def getUniqueName(seed: String) = sha256(seed+(new Date().getTime.toString)+rand.alphanumeric.take(64).mkString)

  def sha256(data: String) = MessageDigest.getInstance("SHA-256").digest(data.getBytes).map("%02X".format(_)).mkString

  def setLogLevel(level: String) {
    if (level == "DEBUG") {
      GlobalSettings.loggingSQLAndTime = LoggingSQLAndTimeSettings(
        enabled = true,
        singleLineMode = true,
        logLevel = 'DEBUG
      )
    }

    Logger.getRootLogger().setLevel(Level.toLevel(level))
  }
}

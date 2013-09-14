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

package cloud.lib

import com.typesafe.config._
import java.security.MessageDigest
import org.apache.log4j.Level
import org.apache.log4j.Logger
import scalikejdbc.GlobalSettings
import scalikejdbc.LoggingSQLAndTimeSettings

trait Helpers {
  private[this] val rand = new Random(new java.security.SecureRandom())

  def getUniqueName(group: String) = sha256(group+(new Date().getTime.toString)+rand.alphanumeric.take(64).mkString)

  def getConfig(group: String = "") = ConfigFactory.load("$group/application.conf").withFallback(ConfigFactory.load("application.conf"))

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

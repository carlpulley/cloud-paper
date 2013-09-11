package cloud.lib

import java.security.MessageDigest
import org.apache.log4j.Level
import org.apache.log4j.Logger
import scalikejdbc.GlobalSettings
import scalikejdbc.LoggingSQLAndTimeSettings

trait Helpers {
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

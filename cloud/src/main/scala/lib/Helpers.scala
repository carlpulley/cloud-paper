package cloud.lib

import java.security.MessageDigest

trait Helpers {
  def sha256(data: String) = MessageDigest.getInstance("SHA-256").digest(data.getBytes).map("%02X".format(_)).mkString
}

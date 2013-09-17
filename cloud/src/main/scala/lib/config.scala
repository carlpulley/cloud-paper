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

import java.io.File
import org.streum.configrity._

object Config {
  private val prefix = "cloud/src/test/resources"
  
  private var config = if (new File(s"$prefix/application.conf").exists) Configuration.load(s"$prefix/application.conf") else Configuration()

  def apply() = config

  def apply(group: String) = {
    val group_config = s"$prefix/$group/application.conf"
    if (new File(group_config).exists) {
      config ++ Configuration.load(group_config)
    } else {
      config
    }
  }

  def load(filename: String) = {
    val file_config = s"$prefix/$filename"
    if (new File(file_config).exists) {
      config ++ Configuration.load(file_config)
    } else {
      config
    }
  }

  def setValue[T](key: String, value: T) {
    config = config.set(key, value)
  }
}

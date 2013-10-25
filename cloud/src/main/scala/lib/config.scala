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
  private var group: Option[String] = None
  private var config: Configuration = Configuration()

  def apply(module: String = "default") = {
    group = Some(module)
    config = config include load("application.conf").config

    this
  }

  def load(filename: String) = {
    if (filename.startsWith("/")) {
      config = Configuration.load(filename)
    } else {
      val loader = Thread.currentThread.getContextClassLoader
      val group_file = if (loader.getResource(s"${group}/${filename}") == null) Configuration() else Configuration.load(loader.getResource(s"${group}/${filename}").getFile)
      val default_file = if (loader.getResource(s"default/${filename}") == null) Configuration() else Configuration.load(loader.getResource(s"default/${filename}").getFile)
      val orig_file = if (loader.getResource(filename) == null) Configuration() else Configuration.load(loader.getResource(filename).getFile)

      config = config include group_file include default_file include orig_file
    }

    this
  }

  def setValue[T](key: String, value: T) {
    config = config.set(key, value)
  }

  def setValue[T](key: String, value: List[T]) {
    config = config.set(key, value)
  }

  def get = {
    config
  }
}

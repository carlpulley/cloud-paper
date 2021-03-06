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

import scala.concurrent.duration.Duration
import scalaz.camel.akka.Akka
import scalaz.camel.core._

case class VerificationFailed(msg: Message) extends Exception

trait Workflow extends Camel with Akka with Helpers {
  val id = (msg: Message) => msg

  def delay(delay: Duration) = id // TODO:
}

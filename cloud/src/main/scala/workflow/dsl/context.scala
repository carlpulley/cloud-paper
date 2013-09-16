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

package cloud.workflow.dsl

import cloud.lib.Image
import cloud.lib.Workflow
import scalaz._
import scalaz.camel.core._

object Context extends Workflow {
  import Scalaz._

  def apply(image: Image, workflow: MessageRoute): MessageRoute = {
    // TODO: implement this!
    workflow
  }
}

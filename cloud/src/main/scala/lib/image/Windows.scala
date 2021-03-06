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

package image

import org.jclouds.compute.domain.OsFamily
import org.jclouds.openstack.nova.v2_0.compute.options.NovaTemplateOptions
import org.jclouds.openstack.nova.v2_0.compute.options.NovaTemplateOptions.Builder._

// WARNING: Windows is currently to be considered experimental!
abstract class Windows(version: String, group: String) extends Image(group) {
  template_builder
    .osFamily(OsFamily.WINDOWS)
    .osVersionMatches(version)
    .smallest()

  ports += 3389 // RDP
  ports += 5985 // winrm

  chef_runlist
    .addRecipe("windows")
}

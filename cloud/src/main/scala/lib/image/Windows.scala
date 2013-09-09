package cloud.lib

package image

import org.jclouds.compute.domain.OsFamily
import org.jclouds.openstack.nova.v2_0.compute.options.NovaTemplateOptions
import org.jclouds.openstack.nova.v2_0.compute.options.NovaTemplateOptions.Builder._

// WARNING: Windows is currently to be considered experimental!
abstract class Windows(version: String) extends Image {
  template_builder
    .osFamily(OsFamily.WINDOWS)
    .osVersionMatches(version)
    .smallest()

  ports += 3389 // RDP
  ports += 5985 // winrm

  chef_runlist
    .addRecipe("windows")
}

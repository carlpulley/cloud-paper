package cloud.lib

package image

import org.jclouds.compute.domain.OsFamily

abstract class Ubuntu(version: String) extends Image {
  template_builder
    .osFamily(OsFamily.UBUNTU)
    .osVersionMatches(version)
    .smallest()

  ports += 22 // SSH

  chef_runlist
    .addRecipe("apt")
}

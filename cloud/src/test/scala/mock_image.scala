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

package cloud.workflow.test

import cloud.lib.Image
import cloud.lib.provider
import java.net.InetAddress
import net.liftweb.json.JsonDSL._
import org.jclouds.compute.ComputeServiceContext
import org.jclouds.compute.domain.internal.NodeMetadataImpl
import org.jclouds.compute.domain.NodeMetadata
import org.jclouds.domain.LoginCredentials
import scala.collection.JavaConversions._

class LiveImage extends provider.AwsEc2.Ubuntu("12.04") {
  template_builder
    .minRam(1024)

  chef_runlist
    .addRecipe("java")
    .addRecipe("cloud")

  chef_attributes += ("cloud" -> ("boot_class" -> "cloud.lib.Kernel"))
}

class MockImage extends LiveImage {
  override def bootstrap() = {
    new NodeMetadataImpl(
      /* String providerId */ null,
      /* String name */ null, 
      /* String id */ "mockimage", 
      /* Location location */ null, 
      /* URI uri */ null, 
      /* Map<String, String> userMetadata */ Map[String, String](),
      /* Set<String> tags */ Set[String](), 
      /* @Nullable String group */ group, 
      /* @Nullable Hardware hardware */ null, 
      /* @Nullable String imageId */ null, 
      /* @Nullable OperatingSystem os */ null, 
      /* Status status */ NodeMetadata.Status.RUNNING, 
      /* @Nullable String backendStatus */ null, 
      /* int loginPort */ 22, 
      /* Iterable<String> publicAddresses */ Seq(InetAddress.getLocalHost.getHostAddress), 
      /* Iterable<String> privateAddresses */ Seq(), 
      /* @Nullable LoginCredentials credentials */ null, 
      /* String hostname */ "test"
    )
  }

  override def shutdown() {
    // Intentionally do nothing
  }
}

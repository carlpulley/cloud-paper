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
import org.jclouds.compute.ComputeServiceContext
import org.jclouds.compute.domain.internal.NodeMetadataImpl
import org.jclouds.compute.domain.NodeMetadata
import org.jclouds.domain.LoginCredentials
import scala.collection.JavaConversions._

class LiveImage extends provider.AwsEc2.Ubuntu("12.04")

class MockImage extends LiveImage {
  override def bootstrap() = {
    new NodeMetadataImpl(null, null, null, null, null, Map[String, String](), Set[String](), group, null, null, null, null, null, 22, Seq("127.0.0.1"), Seq(), null, "test")
  }

  override def shutdown() {
    // Intentionally do nothing
  }
}

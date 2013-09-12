// Copyright (C) 2013  Carl Pulley
// 
// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU General Public License
// as published by the Free Software Foundation; either version 2
// of the License, or (at your option) any later version.
// 
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.

package cloud.lib

package provider

package AwsEc2

import com.typesafe.config._
import scala.collection.JavaConversions._
import org.jclouds.ContextBuilder
import org.jclouds.compute.ComputeServiceContext
import org.jclouds.domain.LoginCredentials
import org.jclouds.sshj.config.SshjSshClientModule

trait Config { self: Image => 
  private[this] val id = config.getString("aws-ec2.id")
  private[this] val apikey = config.getString("aws-ec2.apikey")
  protected[this] val ec2_private_key = scala.io.Source.fromFile(System.getenv("HOME") + "/.ssh/aws-ec2.pem").mkString

  override lazy val client_context = ContextBuilder.newBuilder("aws-ec2")
      .credentials(id, apikey)
      .modules(Set(new SshjSshClientModule()))
      .buildView(classOf[ComputeServiceContext])
}

abstract class Ubuntu(version: String) extends image.Ubuntu(version) with Config {
  override lazy val admin = LoginCredentials.builder()
    .user("root")
    .privateKey(ec2_private_key)
    .authenticateSudo(false)
    .build()
}

// WARNING: Windows is currently to be considered experimental!
abstract class Windows(version: String) extends image.Windows(version) with Config {
  override lazy val admin = LoginCredentials.builder()
    .user("Administrator")
    .privateKey(ec2_private_key)
    .authenticateSudo(false)
    .build()
}

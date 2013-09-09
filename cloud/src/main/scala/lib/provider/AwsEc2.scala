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

abstract class Windows(version: String) extends image.Windows(version) with Config {
  override lazy val admin = LoginCredentials.builder()
    .user("Administrator")
    .privateKey(ec2_private_key)
    .authenticateSudo(false)
    .build()
}

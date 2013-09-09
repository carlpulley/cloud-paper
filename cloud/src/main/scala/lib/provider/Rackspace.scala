package cloud.lib

package provider

package Rackspace

import com.typesafe.config._
import scala.collection.JavaConversions._
import org.jclouds.ContextBuilder
import org.jclouds.compute.ComputeServiceContext
import org.jclouds.openstack.nova.v2_0.compute.options.NovaTemplateOptions
import org.jclouds.openstack.nova.v2_0.compute.options.NovaTemplateOptions.Builder._
import org.jclouds.domain.LoginCredentials
import org.jclouds.sshj.config.SshjSshClientModule

abstract class Ubuntu(version: String) extends image.Ubuntu(version) {
  private[this] val region = config.getString("rackspace.region")
  private[this] val username = config.getString("rackspace.username")
  private[this] val apikey = config.getString("rackspace.apikey")
  private[this] val rackspace_private_key = scala.io.Source.fromFile(System.getenv("HOME") + "/.ssh/rackspace").mkString
  private[this] val rackspace_public_key = scala.io.Source.fromFile(System.getenv("HOME") + "/.ssh/rackspace.pub").mkString

  override lazy val admin = LoginCredentials.builder()
    .user("root")
    .privateKey(rackspace_private_key)
    .authenticateSudo(false)
    .build()

  override lazy val client_context = ContextBuilder.newBuilder(s"rackspace-cloudservers-$region")
    .credentials(username, apikey)
    .modules(Set(new SshjSshClientModule()))
    .buildView(classOf[ComputeServiceContext])

  template_builder
    .options(template_builder.build.getOptions.asInstanceOf[NovaTemplateOptions].authorizePublicKey(rackspace_public_key))
}

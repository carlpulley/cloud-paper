package cloud.lib

import akka.actor.Actor
import akka.actor.Actor.Receive
import com.google.common.collect.ImmutableSet
import com.typesafe.config._
import java.io.File
import java.io.FileOutputStream
import java.util.Properties
import net.liftweb.json.compact
import net.liftweb.json.JsonAST.JObject
import net.liftweb.json.JsonDSL._
import net.liftweb.json.render
import org.jclouds.chef.ChefApi
import org.jclouds.chef.ChefApiMetadata
import org.jclouds.chef.ChefContext
import org.jclouds.chef.config.ChefProperties
import org.jclouds.chef.util.RunListBuilder
import org.jclouds.compute.ComputeService
import org.jclouds.compute.ComputeServiceContext
import org.jclouds.compute.config.ComputeServiceProperties
import org.jclouds.compute.domain.NodeMetadata
import org.jclouds.compute.domain.NodeMetadataBuilder
import org.jclouds.compute.domain.TemplateBuilder
import org.jclouds.compute.options.RunScriptOptions.Builder.overrideLoginCredentials
import org.jclouds.ContextBuilder
import org.jclouds.domain.JsonBall
import org.jclouds.domain.LoginCredentials
import org.jclouds.scriptbuilder.domain.Statement
import org.jclouds.scriptbuilder.domain.StatementList
import scala.collection.JavaConversions._
import scala.collection.mutable
import scala.language.implicitConversions

trait Image extends Actor {
  protected[this] def group: String = {
    this.getClass.getPackage.getName.split('.').last.toLowerCase().replaceAll("_", "-").toCharArray.filter("abcdefghijklmnopqrstuvwxyz0123456789-" contains _).mkString
  }
  protected[this] val config: Config = ConfigFactory.load("cloud.conf")

  private[this] val chef_server = config.getString("chef.url")
  private[this] val chef_user = config.getString("chef.user.name")
  private[this] val chef_pem = config.getString("chef.user.pem")
  private[this] val validation_name = config.getString("chef.validation.client_name")
  private[this] val validation_pem = config.getString("chef.validation.pem")

  private[this] val chef_config = new Properties()
  chef_config
    .put(ChefProperties.CHEF_VALIDATOR_NAME, validation_name)
  chef_config
    .put(ChefProperties.CHEF_VALIDATOR_CREDENTIAL, scala.io.Source.fromFile(validation_pem).mkString)

  private[this] val chef_context: ChefContext = ContextBuilder.newBuilder("chef")
    .endpoint(chef_server)
    .credentials(chef_user, scala.io.Source.fromFile(chef_pem).mkString)
    .overrides(chef_config)
    .build()

  protected[this] val chef_runlist: RunListBuilder = new RunListBuilder()
  protected[this] val chef_attributes = mutable.Map[String, JObject]()

  protected[this] val client_properties = new Properties()
  client_properties
    .put(ComputeServiceProperties.TIMEOUT_SCRIPT_COMPLETE, (config.getInt("jclouds.script-complete") * 1000).asInstanceOf[java.lang.Integer]) // Convert seconds to milliseconds

  protected[this] val client_context: ComputeServiceContext // 'override lazy val' in children

  private[this] val client: ComputeService = client_context.getComputeService()

  protected[this] val template_builder: TemplateBuilder = client.templateBuilder()
    
  protected[this] val admin: LoginCredentials // 'override lazy val' in children

  protected[this] var node: NodeMetadata = _ // initialised in bootstrap

  protected[this] val bootstrap_builder: ImmutableSet.Builder[Statement] = ImmutableSet.builder()

  protected[this] var ports = mutable.Set[Int]()

  def bootstrap() {
    val template = template_builder.options(template_builder.build.getOptions.inboundPorts(ports.toArray : _*)).build()
    val chef_attrs: Map[String, JObject] = chef_attributes.toMap
    chef_context.getChefService().updateBootstrapConfigForGroup(chef_runlist.build(), new JsonBall(compact(render(chef_attrs))), group)
    val chef_bootstrap = chef_context.getChefService().createBootstrapScriptForGroup(group)
    val bootstrap_node = new StatementList(bootstrap_builder.add(chef_bootstrap).build())
    node = client.createNodesInGroup(group, 1, template).head
    client.runScriptOnNode(node.getId(), bootstrap_node, overrideLoginCredentials(admin))
  }

  override def preStart() = {
    bootstrap()

    // Node now completed bootstrapping, so change actor state and notify channel subscribers
    context.become(active)
    context.system.eventStream.publish(Started(self))
  }

  override def postStop() = {
    val chef_service = chef_context.getChefService()
    val ipaddr = node.getPrivateAddresses().head
    val nodename = s"$group-$ipaddr"
    if (chef_service.listClientsDetails().toSeq.map(_.getName()) contains (nodename)) {
      chef_service.deleteAllClientsInList(Seq(nodename))
    }
    if (chef_service.listNodes().toSeq.map(_.getName()) contains (nodename)) {
      chef_service.deleteAllNodesInList(Seq(nodename))
    }
    val chef_api = chef_context.getApi(classOf[ChefApi])
    val bootstrap_databag = ChefApiMetadata.defaultProperties().get(ChefProperties.CHEF_BOOTSTRAP_DATABAG).asInstanceOf[String]
    if (chef_api.listDatabags.contains(bootstrap_databag) && chef_api.listDatabagItems(bootstrap_databag).contains(group)) {
      chef_api.deleteDatabagItem(bootstrap_databag, group)
    }
    client.destroyNode(node.getId())
  }

  def receive = waiting

  def waiting: Receive = Actor.emptyBehavior

  def active: Receive = {
    case Update(override_attributes: Map[String, JObject]) => {
      context.become(waiting)
      context.system.eventStream.publish(Updating(self))
  
      chef_attributes ++= (chef_attributes ++ override_attributes).keys.map(k => 
        if (chef_attributes.isDefinedAt(k) && override_attributes.isDefinedAt(k)) {
          (k -> (chef_attributes(k) merge override_attributes(k)))
        } else {
          (k -> override_attributes(k))
        }
      )
      bootstrap()
  
      context.become(active)
      context.system.eventStream.publish(Updated(self))
      sender ! Completed
    }
    case GetFiles(files: Set[String], base_dir: String) => {
      val ssh = client_context.utils.sshForNode.apply(NodeMetadataBuilder.fromNodeMetadata(node).credentials(admin).build())
  
      ssh.connect()
      for(filename <- files) {
        val file = new File(s"$base_dir/$filename")
        file.getParentFile().mkdirs()
        ssh.get(filename).writeTo(new FileOutputStream(file))
      }
      ssh.disconnect()

      sender ! Completed
    }
  }
}

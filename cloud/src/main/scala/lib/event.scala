package cloud.lib

import akka.actor.ActorRef
import java.io.File
import net.liftweb.json.JsonAST.JObject

trait Event {
  protected val IDENT = """[\w_-]+"""
}
case class GetFiles(files: Set[String], dump_dir: String) extends Event {
  assert(new File(dump_dir).isDirectory && new File(dump_dir).canWrite)
}
case class Update(override_attributes: Map[String, JObject]) extends Event
case class Completed() extends Event

trait ImageEvent
case class Started(image: ActorRef) extends ImageEvent
case class Updating(image: ActorRef) extends ImageEvent
case class Updated(image: ActorRef) extends ImageEvent

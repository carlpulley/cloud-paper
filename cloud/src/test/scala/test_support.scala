package cloud.workflow.test

import org.apache.camel.CamelContext
import org.apache.camel.model.RouteDefinition
import org.apache.camel.scala.dsl.builder.RouteBuilder
import org.apache.camel.scala.dsl.builder.RouteBuilderSupport
import org.apache.camel.test.junit4.CamelTestSupport
import org.scalatest.BeforeAndAfter
import org.scalatest.FunSuiteLike
import scala.collection.JavaConversions._
import scala.language.implicitConversions
import scala.util.matching.Regex

class CamelContextWrapper(context: CamelContext) {
  def getRouteDefinitions(pattern: String): List[RouteDefinition]  = {
    println("** DEBUG **", context.getRouteDefinitions.toList.map(_.toString))
    context.getRouteDefinitions.toList.filter(rd => rd.toString contains(pattern))
  }
}

trait ScalaTestSupport extends CamelTestSupport with RouteBuilderSupport with FunSuiteLike with BeforeAndAfter {
  val builders: Seq[RouteBuilder]

  override protected def createRouteBuilders = builders.map(scalaToJavaBuilder _).toArray

  override protected def getMockEndpoint(uri: String) = super.getMockEndpoint(uri)

  override protected def assertMockEndpointsSatisfied() = super.assertMockEndpointsSatisfied()

  implicit def wrapCamelContext(context: CamelContext): CamelContextWrapper = new CamelContextWrapper(context)
}

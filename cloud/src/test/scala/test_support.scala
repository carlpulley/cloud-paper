package cloud.workflow.test

import org.apache.camel.scala.dsl.builder.RouteBuilder
import org.apache.camel.scala.dsl.builder.RouteBuilderSupport
import org.apache.camel.test.junit4.CamelTestSupport
import org.scalatest.BeforeAndAfter
import org.scalatest.FunSuiteLike

trait ScalaTestSupport extends CamelTestSupport with RouteBuilderSupport with FunSuiteLike with BeforeAndAfter {
  val builders: Seq[RouteBuilder]

  override protected def createRouteBuilders = builders.map(scalaToJavaBuilder _).toArray

  override protected def getMockEndpoint(uri: String) = super.getMockEndpoint(uri)

  override protected def assertMockEndpointsSatisfied() = super.assertMockEndpointsSatisfied()
}

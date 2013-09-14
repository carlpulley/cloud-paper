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

package cloud.workflow

package test

import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Props
import akka.camel.CamelExtension
import akka.testkit.TestActorRef
import cloud.lib.Helpers
import cloud.workflow.controller.ControlBus
import cloud.workflow.controller.SubmissionTable
import cloud.workflow.controller.FeedbackTable
import cloud.workflow.routers.AddWorkflow
import com.typesafe.config._
import org.apache.activemq.ActiveMQConnectionFactory
import org.apache.camel.builder.AdviceWithRouteBuilder
import org.apache.camel.CamelContext
import org.apache.camel.component.jms.JmsComponent
import org.apache.camel.scala.SimplePeriod
import scala.collection.JavaConversions._
import scala.concurrent.duration._
import scalikejdbc.ConnectionPool
import scalikejdbc.DB
import scalikejdbc.SQLInterpolation._

class ScatterGatherTests extends ScalaTestSupport with Helpers {
  val config: Config = ConfigFactory.load("application.conf")

  val mailFrom  = config.getString("feedback.tutor")
  val mailTo    = "student@hud.ac.uk"
  val subject   = config.getString("feedback.subject")
  val sqldriver = config.getString("sql.driver")
  val sqlurl    = config.getString("sql.url")
  val sqluser   = config.getString("sql.user")
  val sqlpw     = config.getString("sql.password")
  val mailhost  = config.getString("mail.host")
  val mailuser  = config.getString("mail.user")
  val mailpw    = config.getString("mail.password")
  val webhost   = config.getString("web.host")
  val webuser   = config.getString("web.user")
  val loglevel  = config.getString("log.level")

  val submission = "Dummy Submission"
  val feedback = "Dummy Feedback"

  setLogLevel(loglevel)

  Class.forName(sqldriver)
  ConnectionPool.singleton(sqlurl, sqluser, sqlpw)

  implicit val group = "default"
  implicit val system = ActorSystem(group)

  override def createCamelContext(): CamelContext = {
    val camel = CamelExtension(system)
    if (camel.context.hasComponent("jms") == null) {
      val connectionFactory = new ActiveMQConnectionFactory("vm://localhost?broker.persistent=false")
      camel.context.addComponent("jms", JmsComponent.jmsComponentAutoAcknowledge(connectionFactory))
    }
    camel.context.setTracing(true)
    camel.context
  }

  implicit val timeout: Duration = 3.seconds
  implicit val controller: ActorRef = TestActorRef(Props(new ControlBus(group)))
  implicit var camel_context: CamelContext = createCamelContext()

  val simple_feedback1 = SimpleFeedback(1, 'structured_delay)
  val simple_feedback2 = SimpleFeedback(2, 'structured)
  val scatter_gather = routers.ScatterGather(simple_feedback1, simple_feedback2)
  val builders = scatter_gather.routes

  before {
    setUp
    SubmissionTable.create
    FeedbackTable.create
  }

  after {
    tearDown
    SubmissionTable.drop
    FeedbackTable.drop
  }

  test("Basic scatter-gather workflow functionality") {
    context.getRouteDefinitions(s"From[${scatter_gather.pubsub_exit}]").head.adviceWith(context, new AdviceWithRouteBuilder {
      def configure = {
        weaveByToString(s"To[${scatter_gather.exitUri}]").replace.to("mock:exit")
      }
    })

    val mock_exit = getMockEndpoint("mock:exit")
    mock_exit.expectedMessageCount(1)
    mock_exit.message(0).xpath("/feedback/item[@id='1']", classOf[Boolean])
    mock_exit.message(0).xpath("/feedback/item[@id='2']", classOf[Boolean])
    mock_exit.message(0).xpath(s"/feedback/item[@id='1']/comment[text()='$feedback']", classOf[Boolean])
    mock_exit.message(0).xpath(s"/feedback/item[@id='2']/comment[text()='$feedback']", classOf[Boolean])
    mock_exit.message(0).header("breadcrumbId").isEqualTo("scatter-gather-testing-2")
    mock_exit.expectedHeaderReceived("replyTo", mailTo)

    template().sendBodyAndHeaders(scatter_gather.entryUri, submission, Map("replyTo" -> mailTo, "breadcrumbId" -> "scatter-gather-testing-2"))

    assertMockEndpointsSatisfied
  
    mock_exit.expectedMessageCount(3)
    mock_exit.message(1).xpath("/feedback/item[@id='2']", classOf[Boolean])
    mock_exit.message(1).xpath(s"/feedback/item[@id='2']/comment[text()='$feedback']", classOf[Boolean])
    mock_exit.message(1).header("breadcrumbId").isEqualTo("scatter-gather-testing-3")
    mock_exit.message(2).xpath("/feedback/item[@id='1']", classOf[Boolean])
    mock_exit.message(2).xpath(s"/feedback/item[@id='1']/comment[text()='$feedback']", classOf[Boolean])
    mock_exit.message(2).header("breadcrumbId").isEqualTo("scatter-gather-testing-3")
    mock_exit.message(2).arrives().between(4, 5).seconds().afterPrevious()
    mock_exit.expectedHeaderReceived("replyTo", mailTo)

    template().sendBodyAndHeaders(scatter_gather.entryUri, submission, Map("replyTo" -> mailTo, "breadcrumbId" -> "scatter-gather-testing-3", "delay" -> "4"))

    assertMockEndpointsSatisfied
  }

  test("Add workflow to scatter-gather") {
    val workflow = SimpleFeedback(3, 'structured)
    controller ! AddWorkflow(workflow)

    val mock_exit = getMockEndpoint("mock:exit")
    mock_exit.expectedMessageCount(1)
    mock_exit.message(0).xpath("/feedback/item[@id='1']", classOf[Boolean])
    mock_exit.message(0).xpath("/feedback/item[@id='2']", classOf[Boolean])
    mock_exit.message(0).xpath("/feedback/item[@id='3']", classOf[Boolean])
    mock_exit.message(0).xpath(s"/feedback/item[@id='1']/comment[text()='$feedback']", classOf[Boolean])
    mock_exit.message(0).xpath(s"/feedback/item[@id='2']/comment[text()='$feedback']", classOf[Boolean])
    mock_exit.message(0).xpath(s"/feedback/item[@id='3']/comment[text()='$feedback']", classOf[Boolean])
    mock_exit.message(0).header("breadcrumbId").isEqualTo("scatter-gather-testing-6")
    mock_exit.expectedHeaderReceived("replyTo", mailTo)

    template().sendBodyAndHeaders(scatter_gather.entryUri, submission, Map("replyTo" -> mailTo, "breadcrumbId" -> "scatter-gather-testing-6"))

    assertMockEndpointsSatisfied
  }

  test("Basic scatter-gather with historical feedback") {
    DB autoCommit { implicit session =>
      for(n <- (1 to 3)) {
        val submission = s"Dummy Submission $n"
        sql"INSERT INTO ${SubmissionTable.name}(module, student, message_id, message, created_at) VALUES (${group}, ${mailTo}, ${n}, ${submission}, DATETIME('now'))".update.apply()

        val feedback = s"Dummy Feedback History $n"
        sql"INSERT INTO ${FeedbackTable.name}(submission_id, sha256, message, created_at) VALUES (${n}, 'Dummy SHA256', ${feedback}, DATETIME('now'))".update.apply()
      }
    }

    context.getRouteDefinitions(s"From[${scatter_gather.entryUri}]").head.adviceWith(context, new AdviceWithRouteBuilder {
      def configure = {
        weaveByToString(s"To[${scatter_gather.pubsub_entry}]").replace.to("mock:pubsub")
      }
    })

    val mock_pubsub = getMockEndpoint("mock:pubsub")
    mock_pubsub.expectedMessageCount(1)
    mock_pubsub.message(0).header("breadcrumbId").isEqualTo("scatter-gather-testing-5")
    mock_pubsub.expectedHeaderReceived("replyTo", mailTo)

    template().sendBodyAndHeaders(scatter_gather.entryUri, submission, Map("replyTo" -> mailTo, "breadcrumbId" -> "scatter-gather-testing-5"))

    assertMockEndpointsSatisfied

    val (body, history) = mock_pubsub.getExchanges.get(0).getIn.getBody(classOf[(String, List[String])])
    assert(body.contains(submission))
    assert(history.length == 3)
    for((item, n) <- history.zip(1 to 3)) {
      assert(item.contains(s"Dummy Feedback History $n"))
    }
  }
}
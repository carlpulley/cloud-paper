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
import com.typesafe.config._
import org.apache.activemq.ActiveMQConnectionFactory
import org.apache.camel.builder.AdviceWithRouteBuilder
import org.apache.camel.CamelContext
import org.apache.camel.component.jms.JmsComponent
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
  val feedback1 = "<item id=\"1\"><comment>Dummy Feedback</comment></item>"
  val feedback2 = "<item id=\"2\"><comment>Dummy Feedback</comment></item>"

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

  val scatter_gather = routers.ScatterGather(SimpleFeedback(1, 'structured), SimpleFeedback(2, 'structured))
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
    val mock_dummy = getMockEndpoint("mock:exit")
    mock_dummy.expectedMessageCount(1)
    mock_dummy.message(0).body.contains(feedback1)
    mock_dummy.message(0).body.contains(feedback2)
    mock_dummy.expectedHeaderReceived("replyTo", mailTo)

    template().sendBodyAndHeaders(scatter_gather.entryUri, submission, Map("replyTo" -> mailTo, "breadcrumbId" -> "scatter-gather-testing-1"))

    assertMockEndpointsSatisfied
  }

  // TODO: add in dead letter channel test

  // TODO: add in add/remove workflow tests
}
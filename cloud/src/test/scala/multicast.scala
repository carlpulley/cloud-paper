package cloud.workflow

package test

import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.camel.CamelExtension
import akka.testkit.TestActorRef
import cloud.lib.Helpers
import cloud.workflow.actors.ControlBus
import cloud.workflow.routers.SubmissionTable
import cloud.workflow.routers.FeedbackTable
import com.typesafe.config._
import org.apache.activemq.ActiveMQConnectionFactory
import org.apache.camel.CamelContext
import org.apache.camel.component.jms.JmsComponent
import scala.concurrent.duration._
import scalikejdbc.ConnectionPool
import scalikejdbc.DB
import scalikejdbc.SQLInterpolation._

class MulticastTests extends ScalaTestSupport with Helpers {
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
  val submission_hash = sha256(submission)
  val feedback1 = "<feedback><item id='1'><comment>Dummy Feedback</comment></item></feedback>"
  val feedback1_hash = sha256(feedback1)
  val feedback2 = "<feedback><item id='2'><comment>Dummy Feedback</comment></item></feedback>"
  val feedback2_hash = sha256(feedback2)

  setLogLevel(loglevel)

  Class.forName(sqldriver)
  ConnectionPool.singleton(sqlurl, sqluser, sqlpw)

  implicit val system = ActorSystem("testing")

  override def isCreateCamelContextPerClass() = {
    true
  }

  override def createCamelContext(): CamelContext = {
    val camel = CamelExtension(system)
    val connectionFactory = new ActiveMQConnectionFactory("vm://localhost?broker.persistent=false")
    camel.context.addComponent("jms", JmsComponent.jmsComponentAutoAcknowledge(connectionFactory))
    camel.context.setTracing(true)
    camel.context
  }

  implicit val timeout: Duration = 3 seconds
  implicit val controller: ActorRef = TestActorRef[ControlBus]
  implicit var camel_context: CamelContext = createCamelContext()

  val builders = new routers.Multicast("example", new SimpleFeedback(1), new SimpleFeedback(2)).routes

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

  test("FIXME") {
    // TODO:
    assert(true)
  }
}
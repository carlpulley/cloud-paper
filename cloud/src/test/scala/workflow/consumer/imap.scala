// Copyright (C) 2013  Carl Pulley
// 
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
// 
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with this program. If not, see <http://www.gnu.org/licenses/>.

package cloud.workflow.consumer

package test

import cloud.lib.Config
import cloud.lib.Helpers
import cloud.workflow.controller.ControlBus
import cloud.workflow.controller.SubmissionTable
import cloud.workflow.controller.FeedbackTable
import cloud.workflow.Submission
import cloud.workflow.test.ScalaTestSupport
import java.io.File
import java.util.Date
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart
import javax.mail.Address
import javax.mail.Folder
import javax.mail.{Message => JMessage}
import javax.mail.Session
import javax.mail.Store
import org.apache.camel.component.mock.MockEndpoint
import org.jvnet.mock_javamail.Mailbox
import scala.collection.JavaConversions._
import scalikejdbc.ConnectionPool
import scalaz._
import scalaz.camel.core._

class ImapTests extends ScalaTestSupport with Helpers {
  import Scalaz._

  val config = Config.load("application.conf")
  
  val sqldriver = config.get[String]("sql.driver")
  val sqlurl    = config.get[String]("sql.url")
  val sqluser   = config.get[String]("sql.user")
  val sqlpw     = config.get[String]("sql.password")
  val mailhost  = config.get[String]("mail.host")
  val mailuser  = config.get[String]("mail.user")
  val mailpw    = config.get[String]("mail.password")
  val loglevel  = config.get[String]("log.level")
  val mailFrom  = "student@hud.ac.uk"
  val mailTo    = s"$mailuser@hud.ac.uk"
  val subject   = s"Submission for ${group.toUpperCase}"
  val emailText = "Dummy imap submission"
  val folder    = "INBOX"

  setLogLevel(loglevel)

  Class.forName(sqldriver)
  ConnectionPool.singleton(sqlurl, sqluser, sqlpw)

  before {
    MockEndpoint.resetMocks(camel.context)
    SubmissionTable.create
    FeedbackTable.create
    Mailbox.clearAll
  }

  after {
    SubmissionTable.drop
    FeedbackTable.drop
    MockEndpoint.resetMocks(camel.context)
  }

  override def afterAll = {
    router.stop
    system.shutdown
    new File(sqlurl.split(":").last).delete
  }

  // Test IMAP without SSL (otherwise mock-javamail hits the real server!)
  implicit val ssl = false
  Imap(folder)
  val workflow_hook = to("mock:imap-workflow") >=> to("log:STOPPED?showAll=true") >=> failWith(new Exception("stopped"))
  val submission_endpoint = new Submission(ControlBus(), workflow_hook) with Imap
  from(submission_endpoint.error_channel) {
    { msg: Message => if (msg.exception.isDefined) msg.addHeader("Exception", msg.exception.get.getMessage) else msg } >=> 
    to("log:ERROR?showAll=true") >=> 
    to("mock:imap-error")
  }

  val options = System.getProperties()
  options.put("mail.store.protocol", "imap")
  options.put("mail.user", mailuser)

  def buildEmail(from: String, to: String, subject: String, message: String, filename: Option[String] = None)(implicit session: Session) = {
    val msg = new MimeMessage(session)
    msg.setFrom(new InternetAddress(from))
    val recipient: Address = new InternetAddress(to)
    msg.setRecipients(JMessage.RecipientType.TO, Array(recipient))
    msg.setSubject(subject)
    msg.setSentDate(new Date())

    if (filename.isDefined) {
      val text = new MimeBodyPart()
      text.setText(message)
      val file = new MimeBodyPart()
      file.attachFile(filename.get)
      val body = new MimeMultipart()
      body.addBodyPart(text)
      body.addBodyPart(file)
      msg.setContent(body)
    } else {
        msg.setText(message)
    }

    msg
  }

  test("Ensure IMAP emails are correctly processed") {
    val mock_workflow = getMockEndpoint("mock:imap-workflow")
    
    mock_workflow.expectedMessageCount(1)
    mock_workflow.expectedHeaderReceived("replyTo", mailFrom)
    mock_workflow.expectedHeaderReceived("ContentType", "application/x-tgz")
    mock_workflow.message(0).header("breadcrumbId").isNotNull

    implicit val session: Session = Session.getDefaultInstance(options, null)
    session.setDebug(true)
    val store = session.getStore("imap")
    store.connect(mailhost, mailuser, mailpw)
    val submission_box = store.getFolder(folder)
    submission_box.open(Folder.READ_WRITE)

    val filename = "cloud/src/test/resources/submission.tgz"
    val email = buildEmail(mailFrom, mailTo, subject, emailText+"-1", Some(filename))

    submission_box.appendMessages(Array(email))

    mock_workflow.assertIsSatisfied

    val exchange = mock_workflow.getExchanges.head
    val tarball: Array[Byte] = mock_workflow.message(0).body.evaluate(exchange, classOf[Array[Byte]])
    
    assert(sha256(tarball) == sha256(new File(filename)))
  }

  test("Ensure IMAP emails with invalid attachments are routed correctly") {
    val mock_workflow = getMockEndpoint("mock:imap-workflow")
    val mock_error = getMockEndpoint("mock:imap-error")
    
    mock_workflow.expectedMessageCount(0)
    mock_error.expectedMessageCount(1)
    mock_error.expectedHeaderReceived("replyTo", mailFrom)
    mock_error.message(0).body.contains(emailText+"-2")

    implicit val session: Session = Session.getDefaultInstance(options, null)
    session.setDebug(true)
    val store = session.getStore("imap")
    store.connect(mailhost, mailuser, mailpw)
    val submission_box = store.getFolder(folder)
    submission_box.open(Folder.READ_WRITE)

    val email = buildEmail(mailFrom, mailTo, subject, emailText+"-2", Some("cloud/src/test/resources/submission.txt"))

    submission_box.appendMessages(Array(email))

    mock_workflow.assertIsSatisfied
  }

  test("Ensure IMAP emails with no attachments are routed correctly") {
    val mock_workflow = getMockEndpoint("mock:imap-workflow")
    val mock_error = getMockEndpoint("mock:imap-error")

    mock_workflow.expectedMessageCount(0)
    mock_error.expectedMessageCount(1)
    mock_error.expectedHeaderReceived("replyTo", mailFrom)
    mock_error.message(0).body.contains(emailText+"-3")

    implicit val session: Session = Session.getDefaultInstance(options, null)
    session.setDebug(true)
    val store = session.getStore("imap")
    store.connect(mailhost, mailuser, mailpw)
    val submission_box = store.getFolder(folder)
    submission_box.open(Folder.READ_WRITE)

    val email = buildEmail(mailFrom, mailTo, subject, emailText+"-3")

    submission_box.appendMessages(Array(email))

    mock_workflow.assertIsSatisfied
  }
}

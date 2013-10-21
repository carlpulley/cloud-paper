// Copyright (C) 2003-2004, 2013  Carl Pulley
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

package example;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.framework.TestResult;
import junit.framework.TestFailure;
import org.apache.ecs.xml.XML;
import org.apache.ecs.xml.XMLDocument;
import java.util.Vector;
import java.util.Enumeration;

abstract public class FeedbackResult {

    protected final long timeout = 500;

    protected void runThread(FeedbackSuite question) {
        Thread questionThread = new Thread(question);
        questionThread.start();
        try {
            questionThread.join(timeout);
        } catch(InterruptedException exn) {
            // Someone else (who?) beat us to it!
            question.addError(exn.getMessage());
            questionThread.stop();
            return;
        } catch(Throwable exn) {
            question.addError(exn.getMessage());
            questionThread.stop();
            return;
        } // end of try-catch
        if (questionThread.isAlive()) {
            question.addError("TestTimeout");
            questionThread.stop();
        } // end of if-then
    } // end of method runThread

    abstract public XMLDocument toXML(XMLDocument doc);
    
    protected XML toXML(Vector quest, int nos) {
        XML question = new XML("question").addXMLAttribute("value", "" + nos);
        for (Enumeration testCases = quest.elements(); testCases.hasMoreElements();) {
            FeedbackSuite testCase = (FeedbackSuite)(testCases.nextElement());
            XML suite = (XML)(new XML("suite").setPrettyPrint(true));
            suite.setPrettyPrint(true);
            for (Enumeration successful = testCase.getSuccesses(); successful.hasMoreElements();) {
                Object[] testData = (Object[])(successful.nextElement());
                FeedbackCase test = (FeedbackCase)(testData[0]);
                String testName = (String)(testData[1]);
                suite.addElement(((XML)(new XML("test").setPrettyPrint(true)))
                                 .addXMLAttribute("name", test.getClass().getSuperclass().getName())
                                 .addXMLAttribute("passed", "true")
                                 .addElement(((XML)(new XML("outcome").setPrettyPrint(true))).addXMLAttribute("name", testName)));
            } // end of for-loop
            for (Enumeration failed = testCase.getFailures(); failed.hasMoreElements();) {
                Object[] testData = (Object[])(failed.nextElement());
                FeedbackCase test = (FeedbackCase)testData[0];
                String testName = (String)testData[1];
                TestResult testResult = (TestResult)testData[2];
                XML testFeedback = ((XML)(new XML("test").setPrettyPrint(true)))
                                   .addXMLAttribute("name", test.getClass().getSuperclass().getName())
                                   .addXMLAttribute("passed", "false");
                for (Enumeration outcomes = testResult.failures(); outcomes.hasMoreElements();) {
                    TestFailure outcome = (TestFailure)(outcomes.nextElement());
                    testFeedback.addElement(((XML)(new XML("outcome").setPrettyPrint(true))).addXMLAttribute("name", testName).addElement(outcome.exceptionMessage()));
                } // end of for-loop
                suite.addElement(testFeedback);
            } // end of for-loop
            for (Enumeration errors = testCase.getErrors(); errors.hasMoreElements();) {
                Object[] testData = (Object[])(errors.nextElement());
                FeedbackCase test = (FeedbackCase)testData[0];
                String testName = (String)testData[1];
                TestResult testResult = (TestResult)testData[2];
                XML testError = ((XML)(new XML("error").setPrettyPrint(true)))
                                   .addXMLAttribute("name", test.getClass().getSuperclass().getName())
                                   .addXMLAttribute("msg", testName)
                                   .addElement(testResult.toString());
                for (Enumeration outcomes = testResult.errors(); outcomes.hasMoreElements();) {
                    TestFailure outcome = (TestFailure)(outcomes.nextElement());
                    testError.addElement(((XML)(new XML("outcome").setPrettyPrint(true))).addXMLAttribute("name", testName).addElement(outcome.exceptionMessage()));
                } // end of for-loop
                suite.addElement(testError);
            } // end of for-loop
            question.addElement(suite);
        } // end of for-loop
        return question;
    } // end of method toXML

} // end of class FeedbackResult

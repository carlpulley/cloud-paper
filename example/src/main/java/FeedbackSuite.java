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

import junit.framework.TestResult;
import junit.framework.TestSuite;
import java.util.Vector;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Collections;

public class FeedbackSuite extends TestSuite implements Runnable {

    private Vector tests = new Vector();

    public void addTest(FeedbackCase test) {
        java.lang.reflect.Method[] methods = test.getClass().getMethods();
        for (int nos = 0; nos < methods.length; nos++) {
            String testName = methods[nos].getName();
            if (testName.startsWith("test")) {
                tests.add(new Object[]{test, testName});
            } // end of if-then
        } // end of for-loop
    } // endof method addTest
        
    private Vector successes = new Vector();
    private Vector failures = new Vector();
    private Vector errors = new Vector();
    
    public Enumeration getSuccesses() {
        return successes.elements();
    } // end of method getSuccesses
    
    public Enumeration getFailures() {
        return failures.elements();
    } // end of method getFailures
    
    public Enumeration getErrors() {
        return errors.elements();
    } // end of method getErrors

    public void addError(String errorMsg) {
        errors.add(new Object[]{this.test, errorMsg + ":" + this.testName, this.results});
    } // end of method addError

    private TestResult results;
    private FeedbackCase test;
    private String testName;

    public void run() {
        for(java.util.Enumeration tests = this.tests.elements(); tests.hasMoreElements();) {
            this.results = new TestResult();
            Object[] testCase = (Object[])(tests.nextElement());
            this.test = (FeedbackCase)(testCase[0]);
            this.testName = (String)(testCase[1]);
            test.setName(testName);
            try {
                this.runTest(test, results);
            } catch(Throwable exn) {
                errors.add(new Object[]{test, testName + ":" + exn.getClass().getName(), results});
                return;
            } // end of try-catch
            if (results.errorCount() > 0) {
                errors.add(new Object[]{test, testName, results});
            } else {
                if (results.wasSuccessful()) {
                    successes.add(new Object[]{test, testName});                
                } else {
                    failures.add(new Object[]{test, testName, results});
                } // end of if-then-else
            } // end of if-then-else
        } // end of for-loop
    } // end of method run

} // end of class FeedbackSuite

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

import org.apache.ecs.xml.XML;
import org.apache.ecs.xml.XMLDocument;
import java.util.Vector;

public class CourseworkTests extends FeedbackResult {

    private Vector question1 = new Vector();
    private Vector question2 = new Vector();
    private Vector question3 = new Vector();

    public CourseworkTests(Question[] answer) {
        if (answer == null) return;
        for (int nos = 0; nos < answer.length; nos++) {
            if (answer[nos] != null && answer[nos] instanceof Question1) {
                FeedbackSuite question1 = new Question1Tests((Question1)(answer[nos]));
                runThread(question1);
                this.question1.add(question1);
            } // end of if-then
            if (answer[nos] != null && answer[nos] instanceof Question2) {
                FeedbackSuite question2 = new Question2Tests((Question2)(answer[nos]));
                runThread(question2);
                this.question2.add(question2);
            } // end of if-then
            if (answer[nos] != null && answer[nos] instanceof Question3) {
                FeedbackSuite question3 = new Question3Tests((Question3)(answer[nos]));
                runThread(question3);
                this.question3.add(question3);
            } // end of if-then
        } // end of for-loop
    } // end of constructor function
    
    public XMLDocument toXML() {
        XML feedback = new XML("feedback");
        XML question1 = toXML(this.question1, 1);
        XML question2 = toXML(this.question2, 2);
        XML question3 = toXML(this.question3, 3);
        feedback.setPrettyPrint(true);
        question1.setPrettyPrint(true);
        question2.setPrettyPrint(true);
        question3.setPrettyPrint(true);
        return new XMLDocument().addElement(feedback
                                            .addElement(question1)
                                            .addElement(question2)
                                            .addElement(question3));
    } // end of method toXML

} // end of class CourseworkTests

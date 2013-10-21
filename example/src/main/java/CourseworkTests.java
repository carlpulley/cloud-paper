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
import java.util.Set;
import java.util.Vector;

public class CourseworkTests extends FeedbackResult {

    private Vector question1 = new Vector();
    private Vector question2 = new Vector();
    private Vector question3 = new Vector();

    public CourseworkTests(Set<Question> answers) {
        if (answers == null) return;
        for (Question ans: answers) {
            if (ans != null && ans instanceof Question1) {
                FeedbackSuite question1 = new Question1Tests((Question1)(ans));
                runThread(question1);
                this.question1.add(question1);
            } // end of if-then
            if (ans != null && ans instanceof Question2) {
                FeedbackSuite question2 = new Question2Tests((Question2)(ans));
                runThread(question2);
                this.question2.add(question2);
            } // end of if-then
            if (ans != null && ans instanceof Question3) {
                FeedbackSuite question3 = new Question3Tests((Question3)(ans));
                runThread(question3);
                this.question3.add(question3);
            } // end of if-then
        } // end of for-loop
    } // end of constructor function
    
    public XMLDocument toXML(XMLDocument doc) {
        XML question1 = toXML(this.question1, 1);
        XML question2 = toXML(this.question2, 2);
        XML question3 = toXML(this.question3, 3);
        question1.setPrettyPrint(true);
        question2.setPrettyPrint(true);
        question3.setPrettyPrint(true);
        return doc
                .addElement(question1)
                .addElement(question2)
                .addElement(question3);
    } // end of method toXML

} // end of class CourseworkTests

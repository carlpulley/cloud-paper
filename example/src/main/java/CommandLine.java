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

import java.io.FileWriter;
import java.io.BufferedWriter;
import org.apache.ecs.xml.XML;
import org.apache.ecs.xml.XMLDocument;

public class CommandLine {

    public static void main(String[] args) throws Exception {
        System.out.println("started ...");
        XMLDocument feedback = new XMLDocument();
        feedback.addElement((XML)(new XML("feedback").setPrettyPrint(true)));
        int minArgSize = 2;
        if (args == null || args.length == 0) {
            throw new Exception("Usage: CommandLine <XML file> <fully qualified class>+");
        } // end of if-then
        String outputFile = args[0];
        BufferedWriter outputF = new BufferedWriter(new FileWriter(outputFile));
        if (args.length < minArgSize) {
            feedback.addElement(error("No answer classes supplied!"));
        } else {
            String[] argClasses = new String[args.length - 1];
            for(int i = 1; i < args.length; i++) {
                argClasses[i-1] = args[i];
            }
            Class[] answerClass = new Class[argClasses.length];
            int count = argClasses.length;
            for(int nos = 0; nos < argClasses.length; nos++) {
                argClasses[nos] = argClasses[nos].trim();
                try {
                    answerClass[nos] = Class.forName(argClasses[nos]);
                    if (!Question.class.isAssignableFrom(answerClass[nos])) {
                        feedback.addElement(((XML)(new XML("ignored").setPrettyPrint(true))).addXMLAttribute("file", argClasses[nos]).addElement("it is not a subclass of any Question class!"));
                        answerClass[nos] = null;
                        count--;
                    } else if (argClasses[nos].matches(".*ModelAnswer[0-9]+.class")) {
                        feedback.addElement(((XML)(new XML("ignored").setPrettyPrint(true))).addXMLAttribute("file", argClasses[nos]).addElement("Answer class names may <b>not</b> start with the name <i>ModelAnswer</i>!"));
                        answerClass[nos] = null;
                        count--;
                    } // end of if-then-else
                } catch(NoClassDefFoundError exn) {
                    feedback.addElement(warning(argClasses[nos], "Could not find the class " + argClasses[nos] + " [" + exn.getMessage() + "]"));
                } catch(ClassNotFoundException exn) {
                    feedback.addElement(warning(argClasses[nos], "Could not load the class " + argClasses[nos] + " [" + exn.getMessage() + "]"));
                } catch(Throwable exn) {
                    feedback.addElement(warning(argClasses[nos], "An error occured in loading the class " + argClasses[nos] + " [" + exn.getMessage() + "]"));
                } // end of try-catch
            } // end of for-loop
            Question[] answers = new Question[count];
            int index = 0;
            for(int nos = 0; nos < answerClass.length; nos++) {
                if (answerClass[nos] != null) {
                    try {
                        if (Question1.class.isAssignableFrom(answerClass[nos])) {
                            try {
                                answers[index] = (Question)(answerClass[nos].getConstructor(new Class[]{Object.class}).newInstance(new Object[]{null}));
                            } catch(NoSuchMethodException exn) {
                                feedback.addElement(warning(answerClass[nos].getName(), "The required constructor function does not exist, so we could not create an instance of " + answerClass[nos].getName() + ". Check that:<ul><li>your constructor functions are public</li> <li>and that they have the correct signatures (see the <i>Question1</i> class).</li></ul>"));
                            } catch(java.lang.reflect.InvocationTargetException exn) {
                                feedback.addElement(warning(answerClass[nos].getName(), "The required constructor function does not exist, so we could not create an instance of " + answerClass[nos].getName() + ". Check that:<ul><li>your constructor functions are public</li> <li>and that they have the correct signatures (see the <i>Question1</i> class).</li></ul>"));
                            } // end of try-catch
                        } else {
                            if (Question2.class.isAssignableFrom(answerClass[nos])) {
                                try {
                                    answers[index] = (Question)(answerClass[nos].getConstructor(new Class[]{Question1.class}).newInstance(new Question1[]{null}));
                                } catch(NoSuchMethodException exn) {
                                    feedback.addElement(warning(answerClass[nos].getName(), "The required constructor function does not exist, so we could not create an instance of " + answerClass[nos].getName() + ". Check that:<ul><li>your constructor functions are public</li> <li>and that they have the correct signatures (see the <i>Question2</i> class).</li></ul>"));
                                } catch(java.lang.reflect.InvocationTargetException exn) {
                                    feedback.addElement(warning(answerClass[nos].getName(), "The required constructor function does not exist, so we could not create an instance of " + answerClass[nos].getName() + ". Check that:<ul><li>your constructor functions are public</li> <li>and that they have the correct signatures (see the <i>Question2</i> class).</li></ul>"));
                                } // end of try-catch
                            } else {
                                if (Question3.class.isAssignableFrom(answerClass[nos])) {
                                    try {
                                            answers[index] = (Question)(answerClass[nos].getConstructor(new Class[]{Question2.class}).newInstance(new Question2[]{null}));
                                    } catch(NoSuchMethodException exn) {
                                        feedback.addElement(warning(answerClass[nos].getName(), "The required constructor function does not exist, so we could not create an instance of " + answerClass[nos].getName() + ". Check that:<ul><li>your constructor functions are public</li> <li>and that they have the correct signatures (see the <i>Question3</i> class).</li></ul>"));
                                    } catch(java.lang.reflect.InvocationTargetException exn) {
                                        feedback.addElement(warning(answerClass[nos].getName(), "The required constructor function does not exist, so we could not create an instance of " + answerClass[nos].getName() + ". Check that:<ul><li>your constructor functions are public</li> <li>and that they have the correct signatures (see the <i>Question3</i> class).</li></ul>"));
                                    } // end of try-catch
                                } else {
                                    feedback.addElement(warning(answerClass[nos].getName(), answerClass[nos].getName() + " is not recognised!"));
                                } // end of if-then-else
                            } // end of if-then-else
                        } // end of if-then-else
                    } catch(IllegalAccessException exn) {
                        feedback.addElement(warning(answerClass[nos].getName(), "Could not create an instance of " + answerClass[nos].getName()));
                    } catch(InstantiationException exn) {
                        feedback.addElement(warning(answerClass[nos].getName(), "Could not create an instance of " + answerClass[nos].getName()));
                    } catch(Throwable exn) {
                        feedback.addElement(warning(answerClass[nos].getName(), "Could not create an instance of " + answerClass[nos].getName()));
                    } // end of try-catch
                    index++;
                } // end of if-then
            } // end of for-loop
            FeedbackResult results = new CourseworkTests(answers);
            outputF.write(results.toXML().toString().replaceAll("<\\?xml .*?\\?>", ""));
        } // end of if-then-else
        outputF.write(feedback.toString().replaceAll("<\\?xml .*?\\?>", ""));
        outputF.close();
        System.out.println("... finished!");
    } // end of main method

    private static XML error(String msg) {
        return ((XML)(new XML("error").setPrettyPrint(true))).addElement(msg);
    } // end of method usage

    private static XML warning(String msg) {
        return ((XML)(new XML("warning").setPrettyPrint(true))).addElement(msg);
    } // end of method usage
    
    private static XML warning(String name, String msg) {
        return ((XML)(new XML("warning").setPrettyPrint(true))).addXMLAttribute("file", name).addElement(msg);
    } // end of method usage

} // end of class CommandLine

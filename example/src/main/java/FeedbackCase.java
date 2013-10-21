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

abstract public class FeedbackCase extends junit.framework.TestCase {
    
    protected Question answer;
    protected Question model;

    protected void fail(Throwable exn, String msg) {
        if (exn instanceof junit.framework.AssertionFailedError) {
            throw (junit.framework.AssertionFailedError)exn;
        } // end of if-then
        fail("<comment name='" + (exn == null ? "null" : exn.getClass().getName()) + "'>" + msg + (exn == null ? "" : "<stack>" + toXML(exn)+ "</stack>") + "</comment>");        
    } // end of method fail
    
    protected void assertTrue(Throwable exn, String msg, boolean test) {
        if (exn instanceof junit.framework.AssertionFailedError) {
            throw (junit.framework.AssertionFailedError)exn;
        } // end of if-then
        assertTrue("<comment name='" + (exn == null ? "null" : exn.getClass().getName()) + "'>" + msg + (exn == null ? "" : "<stack>" + toXML(exn)+ "</stack>") + "</comment>", test);
    } // end of method assertTrue
    
    protected void assertTrue(Throwable exn, boolean test) {
        if (exn instanceof junit.framework.AssertionFailedError) {
            throw (junit.framework.AssertionFailedError)exn;
        } // end of if-then
        assertTrue("<comment name='" + (exn == null ? "null" : exn.getClass().getName()) + (exn == null ? "'/>" : "><stack>" + toXML(exn)+ "</stack></comment>"), test);
    } // end of method assertTrue
    
    protected void success() {
        assertTrue((Throwable)null, true);
    } // end of method success

    private String toXML(Throwable exn) {
        String result = "";
        StackTraceElement[] stack = exn.getStackTrace();
        for(int frame = 0; frame < stack.length; frame++) {
            result += "<frame>" + "<class>" + specialChars(stack[frame].getClassName()) + "</class><file>" + specialChars(stack[frame].getFileName()) + "</file><line>" + stack[frame].getLineNumber() + "</line><method>" + specialChars(stack[frame].getMethodName()) + "</method></frame>\n";
        } // end of for-loop
        return result;
    } // end of method toXML

    private String specialChars(String word) {
        return word.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");
    } // end of method specialChars

} // end of class FeedbackCase

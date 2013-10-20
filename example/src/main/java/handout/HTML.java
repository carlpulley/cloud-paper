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

import java.io.*;
import java.net.*;
import javax.swing.text.*;
import javax.swing.text.html.*;
import javax.swing.text.html.HTML.*;
import javax.swing.text.html.parser.*;
import java.util.*;

/**
 * This class may be used to create tree instances for testing purposes.
 * 
 * <p><b>WARNING:</b> This code <b>SHOULD NOT</b> be modified in any manner what so 
 * ever.
 */

public class HTML {

    /** 
     * This <i>static</i> method is used to generate tree instances.
     * 
     * @param url Specifies a HTML page or file for which a <i>TreeNode</i> instance is to be generated:
     * <ul>
     * <li>to parse the CIS2344 module web page, use the argument:
     * <center><code>"http://scom.hud.ac.uk/scomcjp/cis2344"</code></center>
     * <li>to parse a HTML file in the directory <code>/home/myacct/myfile.html</code>, use the argument:
     * <center><code>"/home/myacct/myfile.html"</code></center>
     * </ul>
     * 
     * @return Returns an instance of <i>TreeNode</i> that is an <i>abstract</i> representation of 
     * the given HTML page or file.
     */

    public static TreeNode parseURL(String url) {
        Reader r;
        try {
            if (url.indexOf("://") > 0) {
                URL u = new URL(url);
                Object content = u.getContent();
                if (content instanceof InputStream) {
                    r = new InputStreamReader((InputStream)content);
                } else {
                    if (content instanceof Reader) {
                        r = (Reader)content;
                    } else {
                        throw new Exception("Bad URL content type.");
                    } // end of if-then-else
                } // end of if-then-else
            } else {
                r = new FileReader(url);
            } // end of if-then-else

            HTMLEditorKit.Parser parser;
            parser = new ParserDelegator();
            HTMLParser result = new HTMLParser();
            parser.parse(r, result, true);
            r.close();
            return (TreeNode)(result.stack.pop());
        } catch (Exception e) {
            System.err.println("Error: " + e);
            e.printStackTrace(System.err);
            return null;
        } // end of try-catch

    } // end of method parseURL
    
    private static class HTMLParser extends HTMLEditorKit.ParserCallback {
    
        public Stack stack = new Stack();
    
        public void handleText(char[] data, int pos) {
            // ignore all text!
        } // end of method handleText

        public void handleComment(char[] data, int pos) {
            // ignore all comments!
        } // end of method handleComment

        public void handleStartTag(Tag t, MutableAttributeSet a, int pos) {
            stack.push(new TreeNode(t.toString(), 0));
        } // end of method handleStartTag

        public void handleEndTag(Tag t, int pos) {
            int ptr;
            TreeNode[] branches;
            int length = stack.size();
            for(ptr = length - 1; ptr >= 0 && ! ((String)((TreeNode)(stack.elementAt(ptr))).data).equals(t.toString()); ptr--) {
                // do nothing
            } // end of for-loop
            if (length - ptr - 1 > 0) {
                branches = new TreeNode[length - ptr - 1];
            } else {
                branches = null;
            } // end of if-then-else
            for(int index = 0; index < length - ptr - 1; index++) {
                branches[index] = (TreeNode)(stack.pop());
            } // end of for-loop
            if (ptr != length && length > 0) {
                // Let us not forget to remove the old start tag!
                stack.pop();
            } // end of if-then-else
            TreeNode node = new TreeNode(t.toString());
            if (branches != null) {
                node.branch = branches;
            } // end of if-then
            stack.push(node);
        } // end of method handleEndTag

        public void handleSimpleTag(Tag t, MutableAttributeSet a, int pos) {
            stack.push(new TreeNode(t.toString()));
        } // end of method handleSimpleTag

        public void handleError(String errorMsg, int pos){
            // ignore errors!
        } // end of method handleError
        
    }  // end of inner class HTMLParser  

} // end of class HTML

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

import java.util.HashMap;
import java.util.Map;
import java.util.Collections;

public class rootNodeTests extends FeedbackCase {

    public Question1 setTree(TreeNode tr) {
        ((Question2)answer).tree = new ModelAnswer1(tr.data);
        ((Question2)answer).tree.branch = tr.branch;
        return ((Question2)answer).tree;
    } // end of method setTree
    
    public void testNull1() {
        setTree(new TreeNode("dummy"));
        try {
            assertTrue(null, "Your code failed to deal with a **null** parameter.", ((Question2)answer).rootNode(null) == null);
        } catch(NullPointerException exn) {
            fail(exn, "Your code should return **null** when presented with a `null` parameter.");
        } catch(Throwable exn) {
            fail(exn, "No exception's should be generated, but your code threw: " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testNull1
    
    public void testNull2() {
        setTree(new TreeNode("dummy"));
        Map refcounts = Collections.synchronizedMap(new HashMap());
        refcounts.put(null, new Integer(0));
        try {
            assertTrue(null, "Your code failed to correctly deal with a Map containing a **null** root node.", ((Question2)answer).rootNode(refcounts) == null);
        } catch(NullPointerException exn) {
            fail(exn, "Your code should return **null** when it encounters a key that is **null**.");
        } catch(Throwable exn) {
            fail(exn, "No exception's should be generated, but your code threw: " + exn.getClass().getName());
            exn.printStackTrace();
        } // end of try-catch
    } // end of method testNull2
   
    public void testNull3() {
        setTree(new TreeNode("dummy"));
        Map refcounts = Collections.synchronizedMap(new HashMap());
        refcounts.put(new TreeNode("leaf"), null);
        try {
            assertTrue(null, "Your code failed to correctly deal with a Map containing a **null** root node.", ((Question2)answer).rootNode(refcounts) == null);
        } catch(NullPointerException exn) {
            fail(exn, "Your code should return **null** when it encounters a value that is **null**.");
        } catch(Throwable exn) {
            fail(exn, "No exception's should be generated, but your code threw: " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testNull3

    public void testRoot1() {
        setTree(new TreeNode("dummy"));
        Map refcounts = Collections.synchronizedMap(new HashMap());
        refcounts.put(new TreeNode("leaf"), new Integer(0));
        try {
            TreeNode result = ((Question2)answer).rootNode(refcounts);
            assertTrue(null, "Your code failed to correctly find the `only` root node of a Map.", 
              result != null 
              && result.getData() != null
              && result.getData().equals("leaf") 
              && result.getBranch() != null 
              && result.getBranch().length == 0);
        } catch(ClassCastException exn) {
            fail(exn, "Caught a `ClassCastException`: you should **only** need to cast to the types `TreeNode` and `Integer`. Check that:\n* your `Map` keys have type **TreeNode**\n* your `Map` values have type **Integer**");                          
        } catch(Throwable exn) {
            fail(exn, "No exception's should be generated, but your code threw: " + exn.getClass().getName());            
        } // end of try-catch
    } // end of method testRoot1
    
    public void testRoot2() {
        setTree(new TreeNode("dummy"));
        Map refcounts = Collections.synchronizedMap(new HashMap());
        refcounts.put(new TreeNode("root"), new Integer(0));
        refcounts.put(new TreeNode("leaf"), new Integer(1));
        try {
            TreeNode result = ((Question2)answer).rootNode(refcounts);
            assertTrue(null, "Your code failed to correctly find the `only` root node of a Map.", 
              result != null 
              && result.getData() != null
              && result.getData().equals("root") 
              && result.getBranch() != null 
              && result.getBranch().length == 0);
        } catch(ClassCastException exn) {
            fail(exn, "Caught a `ClassCastException`: you should **only** need to cast to the types `TreeNode` and `Integer`. Check that:\n* your `Map` keys have type **TreeNode**\n* your `Map` values have type **Integer**");                          
        } catch(Throwable exn) {
            fail(exn, "No exception's should be generated, but your code threw: " + exn.getClass().getName());            
        } // end of try-catch
    } // end of method testRoot2
    
    public void testRoot3() {
        setTree(new TreeNode("dummy"));
        Map refcounts = Collections.synchronizedMap(new HashMap());
        refcounts.put(new TreeNode("leaf1"), new Integer(1));
        refcounts.put(new TreeNode("root1"), new Integer(0));
        refcounts.put(new TreeNode("leaf2"), new Integer(1));
        refcounts.put(new TreeNode("root2"), new Integer(0));
        try {
            TreeNode result = ((Question2)answer).rootNode(refcounts);
            assertTrue(null, "Your code failed to correctly deal with a Map containing `multiple` root nodes correctly.", 
              result != null 
              && result.getData() != null
              && (result.getData().equals("root1") || result.getData().equals("root2"))
              && result.getBranch() != null 
              && result.getBranch().length == 0);
        } catch(ClassCastException exn) {
            fail(exn, "Caught a `ClassCastException`: you should **only** need to cast to the types `TreeNode` and `Integer`. Check that:\n* your `Map` keys have type **TreeNode**\n* your `Map` values have type **Integer**");                          
        } catch(Throwable exn) {
            fail(exn, "No exception's should be generated, but your code threw: " + exn.getClass().getName());            
        } // end of try-catch
    } // end of method testRoot3

    public void testNoRoot1() {
        setTree(new TreeNode("dummy"));
        Map refcounts = Collections.synchronizedMap(new HashMap());
        refcounts.put(new TreeNode("leaf"), new Integer(1));
        try {
            assertTrue(null, "Your code failed to correctly deal with a Map containing **no** root nodes!", ((Question2)answer).rootNode(refcounts) == null);
        } catch(ClassCastException exn) {
            fail(exn, "Caught a `ClassCastException`: you should **only** need to cast to the types `TreeNode` and `Integer`. Check that:\n* your `Map` keys have type **TreeNode**\n* your `Map` values have type **Integer**");                          
        } catch(Throwable exn) {
            fail(exn, "No exception's should be generated, but your code threw: " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testNoRoot1

} // end of class rootNodeTests

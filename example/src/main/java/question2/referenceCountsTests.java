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

import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

public class referenceCountsTests extends FeedbackCase {

    public int getWeight() {
        return 15;
    } // end of method getWeight

    public Question1 setTree(TreeNode tr) {
        ((Question2)answer).tree = new ModelAnswer1(tr.data);
        ((Question2)answer).tree.branch = tr.branch;
        return ((Question2)answer).tree;
    } // end of method setTree
    
    private Map synchronizeTree(Map tree) {
        return tree == null ? null : Collections.synchronizedMap(tree);
    } // end of method synchronizeTree
    
    public void testNull1() {
        setTree(new TreeNode("dummy"));
        try {
            Map result = synchronizeTree(((Question2)answer).referenceCounts(null));
            assertTrue(null, "Your code fails to deal with a <b>null</b> parameter.", result == null);
        } catch(Throwable exn) {
            fail(exn, "No exception's should be generated, but your code threw: " + exn.getClass().getName());            
        } // end of try-catch
    } // end of testNull1
       
    public void testNull2() {
        Map refCounts = synchronizeTree(new HashMap());
        TreeNode key = null;
        TreeNode[] value = new TreeNode[]{};
        refCounts.put(key, value);
        setTree(new TreeNode("dummy"));
        try {
            Map result = synchronizeTree(((Question2)answer).referenceCounts(refCounts));
            assertTrue(null, "Your code fails to return a <i>null</i> value when it encounters a <i>null</i> key.", result == null);
        } catch(Throwable exn) {
            fail(exn, "No exception's should be generated, but your code threw: " + exn.getClass().getName());            
        } // end of try-catch
    } // end of testNull2
       
    public void testNull3() {
        Map refCounts = synchronizeTree(new HashMap());
        TreeNode key = new TreeNode("leaf");
        TreeNode[] value = null;
        refCounts.put(key, value);
        setTree(new TreeNode("dummy"));
        try {
            Map result = synchronizeTree(((Question2)answer).referenceCounts(refCounts));
            assertTrue(null, "Your code fails to return a <b>null</b> value when it encounters a <i>null</i> value.", result == null);
        } catch(Throwable exn) {
            fail(exn, "No exception's should be generated, but your code threw: " + exn.getClass().getName());            
        } // end of try-catch
    } // end of testNull3
       
    public void testNull4() {
        Map refCounts = synchronizeTree(new HashMap());
        TreeNode key1 = new TreeNode("root1", 1);
        TreeNode key2 = new TreeNode("root2", 1);
        TreeNode leaf = new TreeNode("leaf");
        key1.branch = new TreeNode[]{ leaf };
        refCounts.put(key1, key1.branch);
        refCounts.put(key2, key2.branch);
        refCounts.put(leaf, leaf.branch);
        setTree(new TreeNode("dummy"));
        try {
            Map result = synchronizeTree(((Question2)answer).referenceCounts(refCounts));
            assertTrue(null, "Your code fails to return a <b>null</b> value when it encounters a <i>null</i> subtree.", result == null);
        } catch(Throwable exn) {
            fail(exn, "No exception's should be generated, but your code threw: " + exn.getClass().getName());            
        } // end of try-catch
    } // end of testNull4

    public void testTree1() {
        Map refCounts = synchronizeTree(new HashMap());
        TreeNode key = new TreeNode("leaf");
        TreeNode[] value = new TreeNode[]{};
        refCounts.put(key, value);
        setTree(new TreeNode("dummy"));
        try {
            Map result = synchronizeTree(((Question2)answer).referenceCounts(refCounts));
            assertTrue(null, "Your code fails to deal with trees that are leaves.", 
              result != null
              && result.size() == 1
              && result.containsKey(key)
              && result.get(key) != null
              && result.get(key) instanceof Integer
              && ((Integer)(result.get(key))).intValue() == 0);
        } catch(ClassCastException exn) {
            fail(exn, "Caught a <i>ClassCastException</i>: you should <b>only</b> need to cast to the types <i>TreeNode</i> and <i>Integer</i>. Check that:<UL><LI>your <i>Map</i> keys have type <b>TreeNode</b></LI><LI>your <i>Map</i> values have type <b>Integer</b></LI></UL>");                          
        } catch(Throwable exn) {
            fail(exn, "No exception's should be generated, but your code threw: " + exn.getClass().getName());            
        } // end of try-catch        
    } // end of method testTree1

    public void testTree2() {
        Map refCounts = synchronizeTree(new HashMap());
        TreeNode key1 = new TreeNode("leaf1");
        TreeNode key2 = new TreeNode("leaf2");
        TreeNode[] value1 = new TreeNode[]{};
        refCounts.put(key1, value1);
        TreeNode[] value2 = new TreeNode[]{};
        refCounts.put(key2, value2);
        setTree(new TreeNode("dummy"));
        try {
            Map result = synchronizeTree(((Question2)answer).referenceCounts(refCounts));
            assertTrue(null, "Your code fails to deal with trees that <i>only</i> contain leaf nodes.", 
              result != null
              && result.size() == 2
              && result.containsKey(key1)
              && result.containsKey(key2)
              && result.get(key1) != null
              && result.get(key2) != null
              && result.get(key1) instanceof Integer
              && result.get(key2) instanceof Integer
              && ((Integer)(result.get(key1))).intValue() == 0
              && ((Integer)(result.get(key2))).intValue() == 0);
        } catch(ClassCastException exn) {
            fail(exn, "Caught a <i>ClassCastException</i>: you should <b>only</b> need to cast to the types <i>TreeNode</i> and <i>Integer</i>. Check that:<UL><LI>your <i>Map</i> keys have type <b>TreeNode</b></LI><LI>your <i>Map</i> values have type <b>Integer</b></LI></UL>");                          
        } catch(Throwable exn) {
            fail(exn, "No exception's should be generated, but your code threw: " + exn.getClass().getName());            
        } // end of try-catch        
    } // end of method testTree2

    public void testTree3() {
        Map refCounts = synchronizeTree(new HashMap());
        TreeNode key1 = new TreeNode("root", 1);
        TreeNode key2 = new TreeNode("leaf");
        key1.branch[0] = key2;
        TreeNode[] value1 = new TreeNode[]{ key2 };
        refCounts.put(key1, value1);
        TreeNode[] value2 = new TreeNode[]{};
        refCounts.put(key2, value2);
        setTree(new TreeNode("dummy"));
        try {
            Map result = synchronizeTree(((Question2)answer).referenceCounts(refCounts));
            assertTrue(null, "Your code fails to deal with trees that are chains.", 
              result != null
              && result.size() == 2
              && result.containsKey(key1)
              && result.containsKey(key2)
              && result.get(key1) != null
              && result.get(key2) != null
              && result.get(key1) instanceof Integer
              && result.get(key2) instanceof Integer
              && ((Integer)(result.get(key1))).intValue() == 0
              && ((Integer)(result.get(key2))).intValue() == 1);
        } catch(Throwable exn) {
            fail(exn, "No exception's should be generated, but your code threw: " + exn.getClass().getName());            
        } // end of try-catch        
    } // end of method testTree3
    
    public void testTree4() {
        Map refCounts = synchronizeTree(new HashMap());
        TreeNode key1 = new TreeNode("root", 2);
        TreeNode key2 = new TreeNode("leaf1");
        TreeNode key3 = new TreeNode("leaf2");
        key1.branch[0] = key2;
        key1.branch[1] = key3;
        TreeNode[] value1 = new TreeNode[]{ key2, key3 };
        refCounts.put(key1, value1);
        TreeNode[] value2 = new TreeNode[]{};
        refCounts.put(key2, value2);
        TreeNode[] value3 = new TreeNode[]{};
        refCounts.put(key3, value3);
        setTree(new TreeNode("dummy"));
        try {
            Map result = synchronizeTree(((Question2)answer).referenceCounts(refCounts));
            assertTrue(null, "Your code fails to deal with branching trees.", 
              result != null
              && result.size() == 3
              && result.containsKey(key1)
              && result.containsKey(key2)
              && result.containsKey(key3)
              && result.get(key1) != null
              && result.get(key2) != null
              && result.get(key3) != null
              && result.get(key1) instanceof Integer
              && result.get(key2) instanceof Integer
              && result.get(key3) instanceof Integer
              && ((Integer)(result.get(key1))).intValue() == 0
              && ((Integer)(result.get(key2))).intValue() == 1
              && ((Integer)(result.get(key3))).intValue() == 1);
        } catch(ClassCastException exn) {
            fail(exn, "Caught a <i>ClassCastException</i>: you should <b>only</b> need to cast to the types <i>TreeNode</i> and <i>Integer</i>. Check that:<UL><LI>your <i>Map</i> keys have type <b>TreeNode</b></LI><LI>your <i>Map</i> values have type <b>Integer</b></LI></UL>");                          
        } catch(Throwable exn) {
            fail(exn, "No exception's should be generated, but your code threw: " + exn.getClass().getName());            
        } // end of try-catch        
    } // end of method testTree4
    
    public void testTree5() {
        Map refCounts = synchronizeTree(new HashMap());
        TreeNode key1 = new TreeNode("root1", 1);
        TreeNode key2 = new TreeNode("root2", 1);
        TreeNode key3 = new TreeNode("leaf");
        key1.branch[0] = key3;
        key2.branch[0] = key3;
        TreeNode[] value1 = new TreeNode[]{ key3 };
        refCounts.put(key1, value1);
        TreeNode[] value2 = new TreeNode[]{ key3 };
        refCounts.put(key2, value2);
        TreeNode[] value3 = new TreeNode[]{};
        refCounts.put(key3, value3);
        setTree(new TreeNode("dummy"));
        try {
            Map result = synchronizeTree(((Question2)answer).referenceCounts(refCounts));
            assertTrue(null, "Your code fails to deal with trees that have <i>shared</i> subtrees.", 
              result != null
              && result.size() == 3
              && result.containsKey(key1)
              && result.containsKey(key2)
              && result.containsKey(key3)
              && result.get(key1) != null
              && result.get(key2) != null
              && result.get(key3) != null
              && result.get(key1) instanceof Integer
              && result.get(key2) instanceof Integer
              && result.get(key3) instanceof Integer
              && ((Integer)(result.get(key1))).intValue() == 0
              && ((Integer)(result.get(key2))).intValue() == 0
              && ((Integer)(result.get(key3))).intValue() == 2);
        } catch(ClassCastException exn) {
            fail(exn, "Caught a <i>ClassCastException</i>: you should <b>only</b> need to cast to the types <i>TreeNode</i> and <i>Integer</i>. Check that:<UL><LI>your <i>Map</i> keys have type <b>TreeNode</b></LI><LI>your <i>Map</i> values have type <b>Integer</b></LI></UL>");                          
        } catch(Throwable exn) {
            fail(exn, "No exception's should be generated, but your code threw: " + exn.getClass().getName());            
        } // end of try-catch        
    } // end of method testTree5
     
    public void testTree6() {
        Map refCounts = synchronizeTree(new HashMap());
        TreeNode key1 = new TreeNode("root", 1);
        TreeNode key2 = new TreeNode("leaf1");
        TreeNode key3 = new TreeNode("leaf2");
        key1.branch[0] = key3;
        TreeNode[] value1 = new TreeNode[]{ key3 };
        refCounts.put(key1, value1);
        TreeNode[] value3 = new TreeNode[]{};
        refCounts.put(key2, value3);
        refCounts.put(key3, value3);
        setTree(new TreeNode("dummy"));
        try {
            Map result = synchronizeTree(((Question2)answer).referenceCounts(refCounts));
            assertTrue(null, "Your code returns a <i>null</i> map when it shouldn't!", 
              result != null);
            assertTrue(null, "Your code a map with an incorrect number of elements!", 
              result.size() == 3);
            assertTrue(null, "Your code fails to produce a map with the correct keys!", 
              result.containsKey(key1)
              && result.containsKey(key2)
              && result.containsKey(key3)
              && result.get(key1) != null
              && result.get(key2) != null
              && result.get(key3) != null);
            assertTrue(null, "Your code fails to produce a map with the correct value types!", 
              result.get(key1) instanceof Integer
              && result.get(key2) instanceof Integer
              && result.get(key3) instanceof Integer);
            assertTrue(null, "Your code fails to produce a map with the correct values!", 
              ((Integer)(result.get(key1))).intValue() == 0
              && ((Integer)(result.get(key2))).intValue() == 0
              && ((Integer)(result.get(key3))).intValue() == 1);
        } catch(ClassCastException exn) {
            fail(exn, "Caught a <i>ClassCastException</i>: you should <b>only</b> need to cast to the types <i>TreeNode</i> and <i>Integer</i>. Check that:<UL><LI>your <i>Map</i> keys have type <b>TreeNode</b></LI><LI>your <i>Map</i> values have type <b>Integer</b></LI></UL>");                          
        } catch(Throwable exn) {
            fail(exn, "No exception's should be generated, but your code threw: " + exn.getClass().getName());            
        } // end of try-catch        
    } // end of method testTree6
   
    public void testCorrupt1() {
        Map refCounts = synchronizeTree(new HashMap());
        TreeNode key1 = new TreeNode("root", 1);
        TreeNode key2 = new TreeNode("leaf1");
        TreeNode key3 = new TreeNode("leaf2");
        key1.branch[0] = key3;
        TreeNode[] value1 = new TreeNode[]{};
        refCounts.put(key1, value1);
        TreeNode[] value3 = new TreeNode[]{};
        refCounts.put(key3, value3);
        setTree(new TreeNode("dummy"));
        try {
            Map result = synchronizeTree(((Question2)answer).referenceCounts(refCounts));
            assertTrue(null, "Your code fails to deal with trees whose <i>Map</i> representation is corrupted. Check that: <ul><li>you are using the correct type of equality (eg. <i>==</i> or <i>equals</i>?)</li></ul>", result == null);
        } catch(Throwable exn) {
            fail(exn, "No exception's should be generated, but your code threw: " + exn.getClass().getName());            
        } // end of try-catch        
    } // end of method testCorrupt1
      
    public void testCorrupt2() {
        Map refCounts = synchronizeTree(new HashMap());
        TreeNode key1 = new TreeNode("root1", 1);
        TreeNode key2 = new TreeNode("root2", 1);
        TreeNode leaf = new TreeNode("leaf");
        key1.branch = new TreeNode[]{ leaf };
        refCounts.put(key1, key1.branch);
        refCounts.put(key2, key2.branch);
        setTree(new TreeNode("dummy"));
        try {
            Map result = synchronizeTree(((Question2)answer).referenceCounts(refCounts));
            assertTrue(null, "Your code fails to deal with trees whose <i>Map</i> representation is corrupted.", result == null);
        } catch(Throwable exn) {
            fail(exn, "No exception's should be generated, but your code threw: " + exn.getClass().getName());            
        } // end of try-catch
    } // end of testCorrupt2

} // end of class referenceCountsTests

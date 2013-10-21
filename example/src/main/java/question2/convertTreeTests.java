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
import java.util.Iterator;
import java.util.Collections;

public class convertTreeTests extends FeedbackCase {

    private Map synchronizeTree(Map tree) {
        return tree == null ? null : Collections.synchronizedMap(tree);
    } // end of method synchronizeTree

    public Question1 setTree(TreeNode tr) {
        ((Question2)answer).tree = new ModelAnswer1(tr.data);
        ((Question2)answer).tree.branch = tr.branch;
        return ((Question2)answer).tree;
    } // end of method setTree
    
    private TreeNode mkChain(TreeNode[] node) {
        TreeNode tree = node[node.length - 1];
        for(int nos = node.length - 2; nos >= 0; nos--) {
            TreeNode oldtree = tree;
            tree = node[nos];
            tree.branch = new TreeNode[]{ oldtree };
        } // end of for-loop
        return tree;
    } // end of method mkChain
   
    private String toString(Map tree) {
        if (tree == null) {
            return "null";
        } // end of if-then
        String result = "";
        if (tree.size() > 0) {
            Iterator iter = tree.keySet().iterator();
            TreeNode key = (TreeNode)(iter.next());
            result = key.toString() + "=" + toString((TreeNode[])(tree.get(key)));
            while(iter.hasNext()) {
                key = (TreeNode)(iter.next());
                result += ", " + key.toString() + "=" + toString((TreeNode[])(tree.get(key)));
            } // end of while-loop
        } // end of if-then
        return "{" + result + "}";
    } // end of method toString
   
    private String toString(TreeNode[] branches) {
        if (branches == null) {
            return "null";
        } // end of if-then
        String result = "";
        if (branches.length > 0) {
            result = (branches[0] == null ? "null" : branches[0].toString());
            for (int edge = 1; edge < branches.length; edge++) {
                result += ", " + (branches[edge] == null ? "null" : branches[edge].toString());
            } // end of for-loop
        } // end of if-then
        return "[" + result + "]";
    } // end of method toString
   
    public void testNull1() {
        setTree(new TreeNode("dummy"));
        ((Question2)answer).tree = null;
        try {
            ((Question2)answer).convertTree();
            fail(null, "Your code failed to throw a `TreeException` when the tree property was **null**.");
        } catch(NullPointerException exn) {
            fail(exn, "Your code failed to throw a `TreeException` when the tree property was **null**.");
        } catch(TreeException exn) {
            assertTrue(exn, true);
        } catch(Throwable exn) {
            fail(exn, "No exception's should be generated, but your code threw: " + exn.getClass().getName());                        
        } // end of try-catch
    } // end of testNull1
   
    public void testNull2() {
        TreeNode tree = new TreeNode(null);
        tree.branch = null;
        setTree(tree);
        try {
            ((Question2)answer).convertTree();
            fail(null, "Your code failed to throw a `TreeException` when the tree has a **null** branch.");
        } catch(NullPointerException exn) {
            fail(exn, "Your code failed to throw a `TreeException` when the tree has a **null** branch.");
        } catch(TreeException exn) {
            assertTrue(exn, true);
        } catch(Throwable exn) {
            fail(exn, "No exception's should be generated, but your code threw: " + exn.getClass().getName());                        
        } // end of try-catch
    } // end of testNull2
   
    public void testNull3() {
        TreeNode root = new TreeNode("root", 1);
        TreeNode leaf = new TreeNode("leaf", 1);
        root.branch = new TreeNode[]{ leaf};
        leaf.branch = null;
        setTree(root);
        try {
            ((Question2)answer).convertTree();
            fail(null, "Your code failed to throw a `TreeException` when the tree has a **null** branch.");
        } catch(NullPointerException exn) {
            fail(exn, "Your code failed to throw a `TreeException` when the tree has a **null** branch.");
        } catch(TreeException exn) {
            assertTrue(exn, true);
        } catch(Throwable exn) {
            fail(exn, "No exception's should be generated, but your code threw: " + exn.getClass().getName());                        
        } // end of try-catch
    } // end of testNull3
   
    public void testNull4() {
        TreeNode root = new TreeNode("root", 1);
        TreeNode leaf = new TreeNode("leaf", 1);
        root.branch = new TreeNode[]{ leaf };
        leaf.branch = new TreeNode[]{ null };
        setTree(root);
        try {
            ((Question2)answer).convertTree();
            fail(null, "Your code failed to throw a `TreeException` when the tree has a **null** branch.");
        } catch(NullPointerException exn) {
            fail(exn, "Your code failed to throw a `TreeException` when the tree has a **null** branch.");
        } catch(TreeException exn) {
            assertTrue(exn, true);
        } catch(Throwable exn) {
            fail(exn, "No exception's should be generated, but your code threw: " + exn.getClass().getName());                        
        } // end of try-catch
    } // end of testNull4

    public void testChain1() {
        TreeNode key = new TreeNode("leaf");
        TreeNode tree = mkChain(new TreeNode[]{ key });
        setTree(tree);
        Question1 originalTree = ((Question2)answer).tree;
        try {
            Map result = synchronizeTree(((Question2)answer).convertTree());
            if (result == null) {
                fail(null, "You returned a `null` map from a leaf node!");
            } // end of if-then
            if (result.size() != 1) {
                fail(null, "You generated a map (" + toString(result) + ") with " + result.size() + " elements from a leaf node!");
            } // end of if-then
            if (! result.containsKey(originalTree)) {
                fail(null, "Your map does not contain `this` tree as a key!");
            } // end of if-then
            if (result.get(originalTree) == null) {
                fail(null, "You generated a map that associates a `null` value with a key!");
            } // end of if-then
            if (! (result.get(originalTree) instanceof TreeNode[])) {
                if (result.get(originalTree) instanceof TreeNode) {
                    fail(null, "Your keys should **not** be associated with values that have type `TreeNode`!");
                } // end of if-then
                fail(null, "Your keys are associated with values that are not of type `TreeNode[]`!");
            } // end of if-then
            assertTrue(null, "Your code failed to correctly convert a leaf node tree.",
              ((TreeNode[])(result.get(originalTree))).length == 0);
        } catch(TreeException exn) {
            fail(exn, "An unexpected `TreeException` was generated!");
        } catch(ClassCastException exn) {
            fail(exn, "Your `keys` should have type **TreeNode** and your `values` should have type **TreeNode[]**.");
        } catch(Throwable exn) {
            fail(exn, "No exception's should be generated, but your code threw: " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testChain1

    public void testChain2() {
        TreeNode key1 = new TreeNode("root");
        TreeNode key2 = new TreeNode("leaf");
        TreeNode tree = mkChain(new TreeNode[]{ key1, key2 });
        setTree(tree);
        Question1 realKey1 = ((Question2)answer).tree;
        try {
            Map result = synchronizeTree(((Question2)answer).convertTree());
            if (result == null) {
                fail(null, "You returned a `null` map from a chain!");
            } // end of if-then
            if (result.size() != 2) {
                fail(null, "You generated a map (" + toString(result) + ") with an incorrect number of elements (" + result.size() + ") from a chain!");
            } // end of if-then
            if (! result.containsKey(realKey1)) {
                fail(null, "Your map does not contain one of the nodes of `this` tree!");
            } // end of if-then
            if (! result.containsKey(key2)) {
                fail(null, "Your map does not contain ones of the nodes of `this` tree!");
            } // end of if-then
            if (result.get(realKey1) == null) {
                fail(null, "You generated a map that associates a `null` value with a key!");
            } // end of if-then
            if (result.get(key2) == null) {
                fail(null, "You generated a map that associates a `null` value with a key!");
            } // end of if-then
            if (! (result.get(realKey1) instanceof TreeNode[])) {
                fail(null, "Your keys are associated with values that are not of type `TreeNode[]`!");
            } // end of if-then
            if (! (result.get(key2) instanceof TreeNode[])) {
                fail(null, "Your keys are associated with values that are not of type `TreeNode[]`!");
            } // end of if-then
            if (((TreeNode[])(result.get(realKey1))).length != 1) {
                fail(null, "Your code failed to correctly convert a tree that is a chain of nodes.");
            } // end of if-then
            if (((TreeNode[])(result.get(key2))).length != 0) {
                fail(null, "Your code failed to correctly convert a tree that is a chain of nodes.");
            } // end of if-then
            if (((TreeNode[])(result.get(realKey1)))[0] == null) {
                fail(null, "Your code failed to correctly convert a tree that is a chain of nodes.");
            } // end of if-then
            assertTrue(null, "Your code failed to correctly convert a tree that is a chain of nodes.",
              ((TreeNode[])(result.get(realKey1)))[0] == key2);
        } catch(TreeException exn) {
            fail(exn, "An unexpected `TreeException` was generated!");
        } catch(ClassCastException exn) {
            fail(exn, "Your `keys` should have type **TreeNode** and your `values` should have type **TreeNode[]**.");
        } catch(Throwable exn) {
            fail(exn, "No exception's should be generated, but your code threw: " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testChain2

    public void testKey1() {
        TreeNode key1 = new TreeNode("root");
        TreeNode tree = mkChain(new TreeNode[]{ key1 });
        setTree(tree);
        Question1 realKey1 = ((Question2)answer).tree;
        try {
            Map result = synchronizeTree(((Question2)answer).convertTree());
            for (Iterator iter = result.keySet().iterator(); iter.hasNext() ;) {
                Object key = iter.next();
                if (! (key instanceof TreeNode)) {
                    fail(null, "Your `keys` should have type **TreeNode**.");
                } // end of if-then
            } // end of for-loop
            assertTrue((Exception)null, true);
        } catch(Throwable exn) {
            fail(exn, "No exception's should be generated, but your code threw: " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testKey1

    public void testKey2() {
        TreeNode key1 = new TreeNode("root");
        TreeNode key2 = new TreeNode("leaf");
        TreeNode tree = mkChain(new TreeNode[]{ key1, key2 });
        setTree(tree);
        Question1 realKey1 = ((Question2)answer).tree;
        try {
            Map result = synchronizeTree(((Question2)answer).convertTree());
            for (Iterator iter = result.keySet().iterator(); iter.hasNext() ;) {
                Object key = iter.next();
                if (! (key instanceof TreeNode)) {
                    fail(null, "Your `keys` should have type **TreeNode**.");
                } // end of if-then
            } // end of for-loop
            assertTrue((Exception)null, true);
        } catch(Throwable exn) {
            fail(exn, "No exception's should be generated, but your code threw: " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testKey2

    public void testValue1() {
        TreeNode key1 = new TreeNode("root");
        TreeNode tree = mkChain(new TreeNode[]{ key1 });
        setTree(tree);
        Question1 realKey1 = ((Question2)answer).tree;
        try {
            Map result = synchronizeTree(((Question2)answer).convertTree());
            for (Iterator iter = result.keySet().iterator(); iter.hasNext() ;) {
                Object key = iter.next();
                Object value = result.get(key);
                if (! (value instanceof TreeNode[])) {
                    fail(null, "Your `values` should have type **TreeNode[]**.");
                } // end of if-then
            } // end of for-loop
            assertTrue((Exception)null, true);
        } catch(Throwable exn) {
            fail(exn, "No exception's should be generated, but your code threw: " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testValue1

    public void testValue2() {
        TreeNode key1 = new TreeNode("root");
        TreeNode key2 = new TreeNode("leaf");
        TreeNode tree = mkChain(new TreeNode[]{ key1, key2 });
        setTree(tree);
        Question1 realKey1 = ((Question2)answer).tree;
        try {
            Map result = synchronizeTree(((Question2)answer).convertTree());
            for (Iterator iter = result.keySet().iterator(); iter.hasNext() ;) {
                Object key = iter.next();
                Object value = result.get(key);
                if (! (value instanceof TreeNode[])) {
                    fail(null, "Your `values` should have type **TreeNode[]**.");
                } // end of if-then
            } // end of for-loop
            assertTrue((Exception)null, true);
        } catch(Throwable exn) {
            fail(exn, "No exception's should be generated, but your code threw: " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testValue2
    
    public void testProperty1() {
        TreeNode key1 = new TreeNode("root");
        TreeNode tree = mkChain(new TreeNode[]{ key1 });
        setTree(tree);
        Question1 realKey1 = ((Question2)answer).tree;
        try {
            Map result = synchronizeTree(((Question2)answer).convertTree());
            result = ((Question2)answer).convertTree();
            for (Iterator iter = result.keySet().iterator(); iter.hasNext() ;) {
                Object key = iter.next();
                Object value = result.get(key);
                if (! (value instanceof TreeNode[])) {
                    fail(null, "Your `values` should have type **TreeNode[]**.");
                } // end of if-then
            } // end of for-loop
            assertTrue((Exception)null, true);
        } catch(Throwable exn) {
            fail(exn, "No exception's should be generated, but your code threw: " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testValue1

    public void testProperty2() {
        TreeNode key1 = new TreeNode("root");
        TreeNode key2 = new TreeNode("leaf");
        TreeNode tree = mkChain(new TreeNode[]{ key1, key2 });
        setTree(tree);
        Question1 realKey1 = ((Question2)answer).tree;
        try {
            Map result = synchronizeTree(((Question2)answer).convertTree());
            result = ((Question2)answer).convertTree();
            for (Iterator iter = result.keySet().iterator(); iter.hasNext() ;) {
                Object key = iter.next();
                Object value = result.get(key);
                if (! (value instanceof TreeNode[])) {
                    fail(null, "Your `values` should have type **TreeNode[]**.");
                } // end of if-then
            } // end of for-loop
            assertTrue((Exception)null, true);
        } catch(Throwable exn) {
            fail(exn, "No exception's should be generated, but your code threw: " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testProperty2

    public void testPollution1() {
        setTree(new TreeNode("dummy"));
        Question1 tree1 = new ModelAnswer1(new TreeNode("tree1"));
        Question1 tree2 = new ModelAnswer1(new TreeNode("tree2"));
        ((Question2)answer).tree = tree1;
        try {
            ((Question2)answer).convertTree();
            ((Question2)answer).tree = tree2;
            Map result = synchronizeTree(((Question2)answer).convertTree());
            assertTrue(null, "Not initialized your result data structure - data pollution", result != null && ! result.containsKey(tree1));
        } catch(Throwable exn) {
            fail(exn, "No exception's should be generated, but your code threw: " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testPollution1

} // end of class convertTreeTests

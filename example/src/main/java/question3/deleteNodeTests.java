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

public class deleteNodeTests extends FeedbackCase {

    public Question1 setTree(TreeNode tr) {
        ((Question3)answer).tree = new ModelAnswer2(new ModelAnswer1(tr.data));
        ((Question3)answer).tree.tree.branch = tr.branch;
        return ((Question3)answer).tree.tree;
    } // end of emthod setTree

    public void testNull1() {
        try {
            ((Question3)answer).deleteNode(null, Collections.synchronizedMap(new HashMap()), new TreeNode("root"));
            success();
        } catch(Throwable exn) {
            fail(exn, "No exception's should be generated, but your code threw: " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testNull1
    
    public void testNull2() {
        try {
            ((Question3)answer).deleteNode(Collections.synchronizedMap(new HashMap()), null, new TreeNode("root"));
            success();
        } catch(Throwable exn) {
            fail(exn, "No exception's should be generated, but your code threw: " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testNull2
   
    public void testNull3() {
        try {
            ((Question3)answer).deleteNode(Collections.synchronizedMap(new HashMap()), Collections.synchronizedMap(new HashMap()), null);
            success();
        } catch(Throwable exn) {
            fail(exn, "No exception's should be generated, but your code threw: " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testNull3

    public void testEmptyTree1() {
        setTree(new TreeNode("dummy"));
        Map refCounts = Collections.synchronizedMap(new HashMap());
        try {
            ((Question3)answer).deleteNode(Collections.synchronizedMap(new HashMap()), refCounts, new TreeNode("root"));
            success();
        } catch(Throwable exn) {
            fail(exn, "No exception's should be generated, but your code threw: " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testEmptyTree1
   
    public void testValid1() {
        setTree(new TreeNode("dummy"));
        TreeNode root = new TreeNode("root");
        Map tree = Collections.synchronizedMap(new HashMap());
        tree.put(root, new TreeNode[]{});
        Map refCounts = Collections.synchronizedMap(new HashMap());
        refCounts.put(root, new Integer(0));
        TreeNode node = root;
        //setTree(root);
        try {
            ((Question3)answer).deleteNode(tree, refCounts, node);
            assertTrue(null, "You failed to correctly update the reference count data structure", refCounts != null && refCounts.size() == 0);
            assertTrue(null, "You failed to correctly update the tree data structure", tree != null && tree.size() == 0);
        } catch(Throwable exn) {
            fail(exn, "No exception's should be generated, but your code threw: " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testValid1
   
    public void testValid2a() {
        setTree(new TreeNode("dummy"));
        TreeNode root = new TreeNode("root", 1);
        TreeNode leaf = new TreeNode("leaf");
        root.setBranch(new TreeNode[]{ leaf });
        Map tree = Collections.synchronizedMap(new HashMap());
        tree.put(root, new TreeNode[]{ leaf });
        tree.put(leaf, new TreeNode[]{});
        Map refCounts = Collections.synchronizedMap(new HashMap());
        refCounts.put(root, new Integer(0));
        refCounts.put(leaf, new Integer(1));
        TreeNode node = root;
        //setTree(root);
        try {
            ((Question3)answer).deleteNode(tree, refCounts, node);
            assertTrue(null, "You failed to correctly update the reference count data structure", refCounts != null && refCounts.size() == 1 && refCounts.containsKey(leaf));
            assertTrue(null, "You failed to correctly update the tree data structure", tree != null && tree.size() == 1 && tree.containsKey(leaf));
        } catch(Throwable exn) {
            fail(exn, "No exception's should be generated, but your code threw: " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testValid2a
   
    public void testValid2b() {
        setTree(new TreeNode("dummy"));
        TreeNode root = new TreeNode("root", 1);
        TreeNode leaf = new TreeNode("leaf");
        root.setBranch(new TreeNode[]{ leaf });
        Map tree = Collections.synchronizedMap(new HashMap());
        tree.put(root, new TreeNode[]{ leaf });
        tree.put(leaf, new TreeNode[]{});
        Map refCounts = Collections.synchronizedMap(new HashMap());
        refCounts.put(root, new Integer(0));
        refCounts.put(leaf, new Integer(1));
        //setTree(root);
        try {
            ((Question3)answer).deleteNode(tree, refCounts, root);
            ((Question3)answer).deleteNode(tree, refCounts, leaf);
            assertTrue(null, "You failed to remove the <i>only</i> node of a leaf tree!", refCounts != null && refCounts.size() == 0);
            assertTrue(null, "You failed to remove the <i>only</i> node of a leaf tree!", tree != null && tree.size() == 0);
        } catch(Throwable exn) {
            fail(exn, "No exception's should be generated, but your code threw: " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testValid2b
  
    public void testValid3() {
        setTree(new TreeNode("dummy"));
        TreeNode root = new TreeNode("root", 1);
        TreeNode subtree = new TreeNode("subtree", 1);
        TreeNode leaf = new TreeNode("leaf");
        root.branch[0] = subtree;
        subtree.branch[0] = leaf;
        Map tree = Collections.synchronizedMap(new HashMap());
        tree.put(root, root.branch);
        tree.put(subtree, subtree.branch);
        tree.put(leaf, leaf.branch);
        Map refCounts = Collections.synchronizedMap(new HashMap());
        refCounts.put(root, new Integer(0));
        refCounts.put(subtree, new Integer(1));
        refCounts.put(leaf, new Integer(1));
        //setTree(root);
        try {
            ((Question3)answer).deleteNode(tree, refCounts, root);
            assertTrue(null, "You failed to correctly update the tree data structure", tree != null && tree.size() == 2 && tree.containsKey(subtree) && tree.containsKey(leaf));
            assertTrue(null, "You failed to correctly update the reference count data structure", refCounts != null && refCounts.size() == 2 && refCounts.containsKey(subtree) && refCounts.containsKey(leaf));
        } catch(Throwable exn) {
            fail(exn, "No exception's should be generated, but your code threw: " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testValid3
   
    public void testNodeRemoval1a() {
        setTree(new TreeNode("dummy"));
        TreeNode root = new TreeNode("root", 1);
        TreeNode leaf = new TreeNode("leaf");
        root.setBranch(new TreeNode[]{ leaf });
        Map tree = Collections.synchronizedMap(new HashMap());
        tree.put(root, new TreeNode[]{ leaf });
        tree.put(leaf, new TreeNode[]{});
        Map refCounts = Collections.synchronizedMap(new HashMap());
        refCounts.put(root, new Integer(0));
        refCounts.put(leaf, new Integer(1));
        TreeNode node = leaf;
        //setTree(root);
        try {
            ((Question3)answer).deleteNode(tree, refCounts, node);
            assertTrue(null, "You failed to remove nodes from the tree <i>Map</i>", tree != null && tree.size() == 1);
        } catch(Throwable exn) {
            fail(exn, "No exception's should be generated, but your code threw: " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testNodeRemoval1a
   
    public void testNodeRemoval1b() {
        setTree(new TreeNode("dummy"));
        TreeNode root = new TreeNode("root", 1);
        TreeNode leaf = new TreeNode("leaf");
        root.setBranch(new TreeNode[]{ leaf });
        Map tree = Collections.synchronizedMap(new HashMap());
        tree.put(root, new TreeNode[]{ leaf });
        tree.put(leaf, new TreeNode[]{});
        Map refCounts = Collections.synchronizedMap(new HashMap());
        refCounts.put(root, new Integer(0));
        refCounts.put(leaf, new Integer(1));
        TreeNode node = leaf;
        //setTree(root);
        try {
            ((Question3)answer).deleteNode(tree, refCounts, node);
            assertTrue(null, "You failed to remove nodes from the reference count <i>Map</i>", refCounts != null && refCounts.size() == 1);
        } catch(Throwable exn) {
            fail(exn, "No exception's should be generated, but your code threw: " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testNodeRemoval1b
   
    public void testNodeRemoval2a() {
        setTree(new TreeNode("dummy"));
        TreeNode root = new TreeNode("root", 1);
        TreeNode leaf = new TreeNode("leaf");
        root.setBranch(new TreeNode[]{ leaf });
        Map tree = Collections.synchronizedMap(new HashMap());
        tree.put(root, new TreeNode[]{ leaf });
        tree.put(leaf, new TreeNode[]{});
        Map refCounts = Collections.synchronizedMap(new HashMap());
        refCounts.put(root, new Integer(0));
        refCounts.put(leaf, new Integer(1));
        TreeNode node = root;
        //setTree(root);
        try {
            ((Question3)answer).deleteNode(tree, refCounts, node);
            assertTrue(null, "Your code removes the wrong key!", tree != null && tree.size() == 1 && tree.containsKey(leaf) && refCounts != null && refCounts.size() == 1 && refCounts.containsKey(leaf));
        } catch(Throwable exn) {
            fail(exn, "No exception's should be generated, but your code threw: " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testNodeRemoval2a
   
    public void testNodeRemoval2b() {
        setTree(new TreeNode("dummy"));
        TreeNode root = new TreeNode("root", 1);
        TreeNode leaf = new TreeNode("leaf");
        root.setBranch(new TreeNode[]{ leaf });
        Map tree = Collections.synchronizedMap(new HashMap());
        tree.put(root, new TreeNode[]{ leaf });
        tree.put(leaf, new TreeNode[]{});
        Map refCounts = Collections.synchronizedMap(new HashMap());
        refCounts.put(root, new Integer(0));
        refCounts.put(leaf, new Integer(1));
        TreeNode node = root;
        int count = 0;
        //setTree(root);
        try {
            ((Question3)answer).deleteNode(tree, refCounts, node);
            assertTrue(null, "Your code doesn't update the reference counts for nodes!", tree != null && tree.size() == 1 && tree.containsKey(leaf) && refCounts != null && refCounts.size() == 1 && refCounts.containsKey(leaf) && ((Integer)(refCounts.get(leaf))).intValue() == 0);
        } catch(Throwable exn) {
            fail(exn, "No exception's should be generated, but your code threw: " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testNodeRemoval2b
  
    public void testNodeRemoval3() {
        setTree(new TreeNode("dummy"));
        TreeNode root = new TreeNode("root", 1);
        TreeNode subtree = new TreeNode("subtree", 1);
        TreeNode leaf = new TreeNode("leaf");
        root.branch[0] = subtree;
        subtree.branch[0] = leaf;
        Map tree = Collections.synchronizedMap(new HashMap());
        tree.put(root, root.branch);
        tree.put(subtree, subtree.branch);
        tree.put(leaf, leaf.branch);
        Map refCounts = Collections.synchronizedMap(new HashMap());
        refCounts.put(root, new Integer(0));
        refCounts.put(subtree, new Integer(1));
        refCounts.put(leaf, new Integer(1));
        //setTree(root);
        try {
            ((Question3)answer).deleteNode(tree, refCounts, root);
            assertTrue(null, "You failed to correctly update the tree data structure", tree != null && tree.size() == 2 && tree.containsKey(subtree) && tree.containsKey(leaf));
            assertTrue(null, "You failed to correctly update the reference count data structure", refCounts != null && refCounts.size() == 2 && refCounts.containsKey(subtree) && refCounts.containsKey(leaf));
            assertTrue(new Error(), "You failed to correctly update the reference count data structure", ((Integer)refCounts.get(subtree)).intValue() == 0 && ((Integer)refCounts.get(leaf)).intValue() == 1);
        } catch(Throwable exn) {
            fail(exn, "No exception's should be generated, but your code threw: " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testNodeRemoval3

} // end of class deleteNodeTests

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

public class deleteSubTreeTests extends FeedbackCase {

    public Question1 setTree(TreeNode tr) {
        ((Question1)answer).data = tr.data;
        ((Question1)answer).branch = tr.branch;
        return ((Question1)answer);
    } // end of method setTree
    
    public void testNull1() {
        setTree(new TreeNode("dummy"));
        try {
            ((Question1)answer).deleteSubTree(null, 0);
            fail(null, "Your code should throw a `TreeException` when one of its parameters is **null**.");
        } catch(TreeException exn) {
            assertTrue(exn, true);
        } catch(NullPointerException exn) {
            fail(exn, "Your code should throw a `TreeException` when one of its parameters is **null**.");
        } catch(Throwable exn) {
            fail(exn, "Expected a `TreeException` but your code generated: " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testNull1
    
    public void testNull2() {
        TreeNode tree = new TreeNode("tree", 1);
        tree.branch = null;
        setTree(tree);
        TreeNode realTree = ((Question1)answer);
        try {
            ((Question1)answer).deleteSubTree(realTree, 0);
            fail(null, "Your code should throw a `TreeException` when the tree's branch property is **null**.");
        } catch(TreeException exn) {
            assertTrue(exn, true);
        } catch(NullPointerException exn) {
            fail(exn, "Your code should throw a `TreeException` when the tree's branch property is **null**.");
        } catch(Throwable exn) {
            fail(exn, "Expected a `TreeException` but your code generated: " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testNull2
       
    public void testContainment1a() {
        try {
            TreeNode tree1 = new TreeNode("tree1", 1);
            tree1.setBranch(new TreeNode[]{ new TreeNode("subtree")});
            TreeNode tree2 = new TreeNode("tree2", 1);
            tree2.setBranch(new TreeNode[]{ new TreeNode("subtree")});
            setTree(tree1);
            ((Question1)answer).deleteSubTree(tree2, 0);            
            fail(null, "Your code has failed to test that the given node is **contained** within `this` tree.");
        } catch(TreeException exn) {
            assertTrue(exn, true);
        } catch(NullPointerException exn) {
            fail(exn, "Your code has failed to test that the given node is **contained** within `this` tree.");
        } catch(ArrayIndexOutOfBoundsException exn) {
            fail(exn, "`ArrayIndexOutOfBoundsException`: \n* Does your code (incorrectly) assume that edge numbering starts at 1?\n* Has your code forgotten to `exclude` the branch array's size as an index value?");
        } catch(Throwable exn) {
            fail(exn, "Expected a `TreeException` but your code generated: " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testContainment1a
       
    public void testContainment1b() {
        try {
            TreeNode tree1 = new TreeNode("tree");
            TreeNode tree2 = new TreeNode("tree");
            setTree(tree1);
            ((Question1)answer).deleteSubTree(tree2, 1);            
            fail(null, "Your code has failed to spot that a leaf node was contained within a leaf node tree.");
        } catch(TreeException exn) {
            assertTrue(exn, true);
        } catch(NullPointerException exn) {
            fail(exn, "Your code has failed to spot that a leaf node was contained within a leaf node tree.");
        } catch(ArrayIndexOutOfBoundsException exn) {
            fail(exn, "`ArrayIndexOutOfBoundsException`: \n* Does your code (incorrectly) assume that edge numbering starts at 1?\n* Has your code forgotten to `exclude` the branch array's size as an index value?");
        } catch(Throwable exn) {
            fail(exn, "Expected a `TreeException` but your code generated: " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testContainment1b
    
    public void testContainment2() {
        try {
            TreeNode tree1 = new TreeNode("tree", 1);
            tree1.setBranch(new TreeNode[]{ new TreeNode("subtree")});
            TreeNode tree2 = new TreeNode("tree", 1);
            tree2.setBranch(new TreeNode[]{ new TreeNode("subtree")});
            setTree(tree1);
            ((Question1)answer).deleteSubTree(tree2, 0);            
            fail(null, "Your code has failed to test that the given node is a **valid reference** within `this` tree.");
        } catch(TreeException exn) {
            assertTrue(exn, true);
        } catch(NullPointerException exn) {
            fail(exn, "Your code has failed to test that the given node is a **valid reference** within `this` tree.");
        } catch(ArrayIndexOutOfBoundsException exn) {
            fail(exn, "`ArrayIndexOutOfBoundsException`: \n* Does your code (incorrectly) assume that edge numbering starts at 1?\n* Has your code forgotten to `exclude` the branch array's size as an index value?");
        } catch(Throwable exn) {
            fail(exn, "Expected a `TreeException` but your code generated: " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testContainment2

    public void testInvalidEdge1() {
        try {
            TreeNode tree = new TreeNode("test");
            setTree(tree);
            TreeNode realTree = ((Question1)answer);
            ((Question1)answer).deleteSubTree(realTree, 0);
            fail(null, "No `TreeException` generated when we try and delete an edge that doesn't exist. Consider the following: \n* What should happen when `edge = 0`?\n* What should happen when `edge = node.branch.length`?");
        } catch(TreeException exn) {
            assertTrue(exn, true);
        } catch(NullPointerException exn) {
            fail(exn, "Your code should throw a `TreeException` when we try and delete edges from a leaf. Consider the following: \n* What should happen when `edge = 0`?\n* What should happen when `edge = node.branch.length`?");
        } catch(ArrayIndexOutOfBoundsException exn) {
            fail(exn, "Your code should throw a `TreeException` when we try and delete edges from a leaf. Consider the following: \n* What should happen when `edge = 0`?\n* What should happen when `edge = node.branch.length`?");
        } catch(Throwable exn) {
            fail(exn, "Expected a `TreeException` but your code generated: " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testInvalidEdge1

    public void testInvalidEdge2() {
        try {
            TreeNode tree = new TreeNode("test", 1);
            setTree(tree);
            TreeNode realTree = ((Question1)answer);
            ((Question1)answer).deleteSubTree(realTree, -1);
            fail(null, "No `TreeException` generated when we try and delete an edge that doesn't exist");
        } catch(TreeException exn) {
            assertTrue(exn, true);
        } catch(NullPointerException exn) {
            fail(exn, "Your code should throw a `TreeException` when we try and delete edges from a leaf. Consider the following: \n* What should happen when `edge = 0`?\n* What should happen when `edge = node.branch.length`?");
        } catch(ArrayIndexOutOfBoundsException exn) {
            fail(exn, "Your code should throw a `TreeException` when we try and delete edges from a negative branch position.");
        } catch(Throwable exn) {
            fail(exn, "Expected a `TreeException` but your code generated: " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testInvalidEdge2

    public void testInvalidEdge3() {
        try {
            TreeNode tree = new TreeNode("test", 1);
            setTree(tree);
            TreeNode realTree = ((Question1)answer);
            ((Question1)answer).deleteSubTree(realTree, 1);
            fail(null, "No `TreeException` generated when we try and delete an edge that doesn't exist. Consider the following: \n* What should happen when `edge = 0`?\n* What should happen when `edge = node.branch.length`?");
        } catch(TreeException exn) {
            assertTrue(exn, true);
        } catch(NullPointerException exn) {
            fail(exn, "Your code should throw a `TreeException` when we try and delete edges from a leaf. Consider the following: \n* What should happen when `edge = 0`?\n* What should happen when `edge = node.branch.length`?");
        } catch(ArrayIndexOutOfBoundsException exn) {
            fail(exn, "Your code should throw a `TreeException` when we try and delete edges from a non-existent branch position. Consider the following: \n* What should happen when `edge = 0`?\n* What should happen when `edge = node.branch.length`?");
        } catch(Throwable exn) {
            fail(exn, "Expected a `TreeException` but your code generated: " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testInvalidEdge3
     
    public void testValidEdge1() {
        try {
            TreeNode tree = new TreeNode("tree", 1);
            tree.setBranch(new TreeNode[]{ new TreeNode("subtree")});
            TreeNode subtree = new TreeNode("subtree");
            setTree(tree);
            TreeNode realTree = ((Question1)answer);
            ((Question1)answer).deleteSubTree(realTree, 0);
            if (realTree.getBranch().length != 1) {
                fail(new Error(), "Your code has altered the branch array size!");
            } // end of if-then
            assertTrue(null, "Your code has incorrectly deleted a leaf node from a 1-branching tree", 
                ((String)(realTree.getData())).equals("tree") 
                && realTree.getBranch()[0] == null);
        } catch(TreeException exn) {
            fail(exn, "Your code threw a `TreeException`. Possible causes: \n* Remember that the `root` node has a `label`.\n* Maybe you are using your parameters incorrectly?\n* Maybe you are using `==` instead of the `equals` method when testing labels?");
        } catch(NullPointerException exn) {
            fail(exn, "Your code threw an `NullPointerException`. Possible causes: \n* Remember that the `root` node has a `label`.\n* Maybe you are using your node parameter incorrectly?\n* Maybe you have assumed that edge numbering starts at 1?\n* Has your code forgotten to `exclude` the branch array's size as an index value?");
        } catch(ArrayIndexOutOfBoundsException exn) {
            fail(exn, "Your code threw an `ArrayIndexOutOfBoundsException`. Possible causes: \n* Remember that the `root` node has a `label`.\n* Maybe you are using your node parameter incorrectly?\n* Maybe you have assumed that edge numbering starts at 1?\n* Has your code forgotten to `exclude` the branch array's size as an index value?");
        } catch(Throwable exn) {
            fail(exn, "No exception's should be generated, but your code threw: " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testValidEdge1
   
    public void testValidEdge2() {
        try {
            TreeNode tree = new TreeNode("tree", 2);
            TreeNode subtree = new TreeNode("spare-subtree");
            tree.setBranch(new TreeNode[]{ new TreeNode("subtree"), subtree });
            setTree(tree);
            TreeNode realTree = ((Question1)answer);
            ((Question1)answer).deleteSubTree(realTree, 0);
            assertTrue(null, "Your code has incorrectly deleted a leaf node from a 2-branching tree", 
                ((String)(realTree.getData())).equals("tree") 
                && realTree.getBranch().length == 2
                && realTree.getBranch()[0] == null
                && realTree.getBranch()[1] == subtree);
        } catch(TreeException exn) {
            fail(exn, "Your code threw a `TreeException`. Possible causes: \n* Remember that the `root` node has a `label`.\n* Maybe you are using your parameters incorrectly?\n* Maybe you are using `==` instead of the `equals` method when testing labels?");
        } catch(NullPointerException exn) {
            fail(exn, "Your code threw an `NullPointerException`. Possible causes: \n* Remember that the `root` node has a `label`.\n* Maybe you are using your node parameter incorrectly?\n* Maybe you have assumed that edge numbering starts at 1?\n* Has your code forgotten to `exclude` the branch array's size as an index value?");
        } catch(ArrayIndexOutOfBoundsException exn) {
            fail(exn, "Your code threw an `ArrayIndexOutOfBoundsException`. Possible causes: \n* Remember that the `root` node has a `label`.\n* Maybe you are using your node parameter incorrectly?\n* Maybe you have assumed that edge numbering starts at 1?\n* Has your code forgotten to `exclude` the branch array's size as an index value?");
        } catch(Throwable exn) {
            fail(exn, "No exception's should be generated, but your code threw: " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testValidEdge2
   
    public void testValidEdge3() {
        try {
            TreeNode tree = new TreeNode("tree", 2);
            TreeNode subtree = new TreeNode("spare-subtree");
            tree.setBranch(new TreeNode[]{ subtree, new TreeNode("subtree")});
            setTree(tree);
            TreeNode realTree = ((Question1)answer);
            ((Question1)answer).deleteSubTree(realTree, 1);
            assertTrue(null, "Your code has incorrectly deleted a leaf node from a 2-branching tree", 
                ((String)(realTree.getData())).equals("tree") 
                && realTree.getBranch().length == 2
                && realTree.getBranch()[0] == subtree
                && realTree.getBranch()[1] == null);
        } catch(TreeException exn) {
            fail(exn, "Your code threw a `TreeException`. Possible causes: \n* Remember that the `root` node has a `label`.\n* Maybe you are using your parameters incorrectly?\n* Maybe you are using `==` instead of the `equals` method when testing labels?");
        } catch(NullPointerException exn) {
            fail(exn, "Your code threw an `NullPointerException`. Possible causes: \n* Remember that the `root` node has a `label`.\n* Maybe you are using your node parameter incorrectly?\n* Maybe you have assumed that edge numbering starts at 1?\n* Has your code forgotten to `exclude` the branch array's size as an index value?");
        } catch(ArrayIndexOutOfBoundsException exn) {
            fail(exn, "Your code threw an `ArrayIndexOutOfBoundsException`. Possible causes: \n* Remember that the `root` node has a `label`.\n* Maybe you are using your node parameter incorrectly?\n* Maybe you have assumed that edge numbering starts at 1?\n* Has your code forgotten to `exclude` the branch array's size as an index value?");
        } catch(Throwable exn) {
            fail(exn, "No exception's should be generated, but your code threw: " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testValidEdge3
   
    public void testValidEdge4() {
        try {
            TreeNode tree = new TreeNode("tree", 3);
            TreeNode subtree1 = new TreeNode("spare-subtree1");
            TreeNode subtree2 = new TreeNode("spare-subtree2");
            tree.setBranch(new TreeNode[]{ new TreeNode("subtree"), subtree1, subtree2});
            setTree(tree);
            TreeNode realTree = ((Question1)answer);
            ((Question1)answer).deleteSubTree(realTree, 0);
            assertTrue(null, "Your code has incorrectly deleted a leaf node from a 3-branching tree", 
                ((String)(realTree.getData())).equals("tree") 
                && realTree.getBranch().length == 3
                && realTree.getBranch()[0] == null
                && realTree.getBranch()[1] == subtree1
                && realTree.getBranch()[2] == subtree2);
        } catch(TreeException exn) {
            fail(exn, "Your code threw a `TreeException`. Possible causes: \n* Remember that the `root` node has a `label`.\n* Maybe you are using your parameters incorrectly?\n* Maybe you are using `==` instead of the `equals` method when testing labels?");
        } catch(NullPointerException exn) {
            fail(exn, "Your code threw an `NullPointerException`. Possible causes: \n* Remember that the `root` node has a `label`.\n* Maybe you are using your node parameter incorrectly?\n* Maybe you have assumed that edge numbering starts at 1?\n* Has your code forgotten to `exclude` the branch array's size as an index value?");
        } catch(ArrayIndexOutOfBoundsException exn) {
            fail(exn, "Your code threw an `ArrayIndexOutOfBoundsException`. Possible causes: \n* Remember that the `root` node has a `label`.\n* Maybe you are using your node parameter incorrectly?\n* Maybe you have assumed that edge numbering starts at 1?\n* Has your code forgotten to `exclude` the branch array's size as an index value?");
        } catch(Throwable exn) {
            fail(exn, "No exception's should be generated, but your code threw: " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testValidEdge4

    public void testValidEdge5() {
        try {
            TreeNode tree = new TreeNode("tree", 3);
            TreeNode subtree1 = new TreeNode("spare-subtree1");
            TreeNode subtree2 = new TreeNode("spare-subtree2");
            tree.setBranch(new TreeNode[]{ subtree1, new TreeNode("subtree"), subtree2 });
            setTree(tree);
            TreeNode realTree = ((Question1)answer);
            ((Question1)answer).deleteSubTree(realTree, 1);
            assertTrue(null, "Your code has incorrectly deleted a leaf node from a 3-branching tree", 
                ((String)(realTree.getData())).equals("tree") 
                && realTree.getBranch().length == 3
                && realTree.getBranch()[0] == subtree1
                && realTree.getBranch()[1] == null
                && realTree.getBranch()[2] == subtree2);
        } catch(TreeException exn) {
            fail(exn, "Your code threw a `TreeException`. Possible causes: \n* Remember that the `root` node has a `label`.\n* Maybe you are using your parameters incorrectly?\n* Maybe you are using `==` instead of the `equals` method when testing labels?");
        } catch(NullPointerException exn) {
            fail(exn, "Your code threw an `NullPointerException`. Possible causes: \n* Remember that the `root` node has a `label`.\n* Maybe you are using your node parameter incorrectly?\n* Maybe you have assumed that edge numbering starts at 1?\n* Has your code forgotten to `exclude` the branch array's size as an index value?");
        } catch(ArrayIndexOutOfBoundsException exn) {
            fail(exn, "Your code threw an `ArrayIndexOutOfBoundsException`. Possible causes: \n* Remember that the `root` node has a `label`.\n* Maybe you are using your node parameter incorrectly?\n* Maybe you have assumed that edge numbering starts at 1?\n* Has your code forgotten to `exclude` the branch array's size as an index value?");
        } catch(Throwable exn) {
            fail(exn, "No exception's should be generated, but your code threw: " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testValidEdge5

    public void testValidEdge6() {
        try {
            TreeNode tree = new TreeNode("tree", 3);
            TreeNode subtree1 = new TreeNode("spare-subtree1");
            TreeNode subtree2 = new TreeNode("spare-subtree2");
            tree.setBranch(new TreeNode[]{ subtree1, subtree2, new TreeNode("subtree")});
            setTree(tree);
            TreeNode realTree = ((Question1)answer);
            ((Question1)answer).deleteSubTree(realTree, 2);
            assertTrue(null, "Your code has incorrectly deleted a leaf node from a 3-branching tree", 
                ((String)(realTree.getData())).equals("tree") 
                && realTree.getBranch().length == 3
                && realTree.getBranch()[0] == subtree1
                && realTree.getBranch()[1] == subtree2
                && realTree.getBranch()[2] == null);
        } catch(TreeException exn) {
            fail(exn, "Your code threw a `TreeException`. Possible causes: \n* Remember that the `root` node has a `label`.\n* Maybe you are using your parameters incorrectly?\n* Maybe you are using `==` instead of the `equals` method when testing labels?");
        } catch(NullPointerException exn) {
            fail(exn, "Your code threw an `NullPointerException`. Possible causes: \n* Remember that the `root` node has a `label`.\n* Maybe you are using your node parameter incorrectly?\n* Maybe you have assumed that edge numbering starts at 1?\n* Has your code forgotten to `exclude` the branch array's size as an index value?");
        } catch(ArrayIndexOutOfBoundsException exn) {
            fail(exn, "Your code threw an `ArrayIndexOutOfBoundsException`. Possible causes: \n* Remember that the `root` node has a `label`.\n* Maybe you are using your node parameter incorrectly?\n* Maybe you have assumed that edge numbering starts at 1?\n* Has your code forgotten to `exclude` the branch array's size as an index value?");
        } catch(Throwable exn) {
            fail(exn, "No exception's should be generated, but your code threw: " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testValidEdge6
   
    public void testValidEdge7() {
        try {
            TreeNode tree = new TreeNode("tree", 2);
            TreeNode subtree = new TreeNode("subtree");
            tree.setBranch(new TreeNode[]{ subtree, null });
            setTree(tree);
            TreeNode realTree = ((Question1)answer);
            ((Question1)answer).deleteSubTree(realTree, 1);
            assertTrue(null, "Your code has incorrectly deleted a `null` node from a 2-branching tree", 
                ((String)(realTree.getData())).equals("tree") 
                && realTree.getBranch().length == 2
                && realTree.getBranch()[0] == subtree
                && realTree.getBranch()[1] == null);
        } catch(TreeException exn) {
            fail(exn, "Your code threw a `TreeException`. Maybe you have disallowed the deletions of **null** subtrees");
        } catch(NullPointerException exn) {
            fail(exn, "Your code threw an `NullPointerException`. Possible causes: \n* Maybe you are using your node parameter incorrectly?\n* Maybe you have assumed that edge numbering starts at 1?\n* Has your code forgotten to `exclude` the branch array's size as an index value?");
        } catch(ArrayIndexOutOfBoundsException exn) {
            fail(exn, "Your code threw an `ArrayIndexOutOfBoundsException`. Possible causes: \n* Maybe you are using your node parameter incorrectly?\n* Maybe you have assumed that edge numbering starts at 1?\n* Has your code forgotten to `exclude` the branch array's size as an index value?");
        } catch(Throwable exn) {
            fail(exn, "No exception's should be generated, but your code threw: " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testValidEdge7

} // end of class deleteSubTreeTests

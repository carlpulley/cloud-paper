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

public class addSubTreeTests extends FeedbackCase {

    public Question1 setTree(TreeNode tr) {
        ((Question1)answer).data = tr.data;
        ((Question1)answer).branch = tr.branch;
        return ((Question1)answer);
    } // end of method setTree

    public void testNull1() {
        try {
            TreeNode tree = new TreeNode("test", 1);
            setTree(tree);
            TreeNode realTree = ((Question1)answer);
            ((Question1)answer).addSubTree(null, realTree, 0);
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
        setTree(new TreeNode("dummy"));
        try {
            ((Question1)answer).addSubTree(new TreeNode("test"), null, 0);
            fail(null, "Your code should throw a `TreeException` when one of its parameters is **null**.");
        } catch(TreeException exn) {
            assertTrue(exn, true);
        } catch(NullPointerException exn) {
            fail(exn, "Your code should throw a `TreeException` when one of its parameters is **null**.");
        } catch(Throwable exn) {
            fail(exn, "Expected a `TreeException` but your code generated: " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testNull2
    
    public void testNull3() {
        TreeNode tree = new TreeNode("test", 1);
        tree.branch = null;
        setTree(tree);
        TreeNode realTree = ((Question1)answer);
        TreeNode subtree = new TreeNode("subtree");
        try {
            ((Question1)answer).addSubTree(subtree, realTree, 0);
            fail(null, "Your code should throw a `TreeException` when the tree's branch property is **null**.");
        } catch(TreeException exn) {
            assertTrue(exn, true);
        } catch(NullPointerException exn) {
            fail(exn, "Your code should throw a `TreeException` when the tree's branch property is **null**.");
        } catch(Throwable exn) {
            fail(exn, "Expected a `TreeException` but your code generated: " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testNull3

    public void testContainment1() {
        try {
            TreeNode subtree = new TreeNode("subtree");
            TreeNode tree1 = new TreeNode("tree1", 1);
            tree1.setBranch(new TreeNode[]{ new TreeNode("leaf1") });
            setTree(tree1);
            TreeNode tree2 = new TreeNode("tree2", 1);
            tree2.setBranch(new TreeNode[]{ new TreeNode("leaf2") });
            ((Question1)answer).addSubTree(subtree, tree2, 0);
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
    } // end of method testContainment1

    public void testContainment2() {
        try {
            TreeNode subtree = new TreeNode("subtree");
            TreeNode tree1 = new TreeNode("tree", 1);
            tree1.setBranch(new TreeNode[]{ new TreeNode("leaf") });
            setTree(tree1);
            TreeNode tree2 = new TreeNode("tree", 1);
            tree2.setBranch(new TreeNode[]{ new TreeNode("leaf") });
            ((Question1)answer).addSubTree(subtree, tree2, 0);
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
            TreeNode tree = new TreeNode("subtree");
            setTree(tree);
            TreeNode realTree = ((Question1)answer);
            ((Question1)answer).addSubTree(new TreeNode("test"), realTree, 0);
            fail(null, "No `TreeException` generated when we try and add an edge that doesn't exist. Consider the following: \n* What should happen when `edge = 0`?\n* What should happen when `edge = node.branch.length`?");
        } catch(TreeException exn) {
            assertTrue(exn, true);
        } catch(NullPointerException exn) {
            fail(exn, "Your code should throw a `TreeException` when we try and add edges to a non-existent branch position. Consider the following: \n* What should happen when `edge = 0`?\n* What should happen when `edge = node.branch.length`?");
        } catch(ArrayIndexOutOfBoundsException exn) {
            fail(exn, "Your code should throw a `TreeException` when we try and add edges to a leaf. Consider the following: \n* What should happen when `edge = 0`?\n* What should happen when `edge = node.branch.length`?");
        } catch(Throwable exn) {
            fail(exn, "Expected a `TreeException` but your code generated: " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testInvalidEdge1

    public void testInvalidEdge2() {
        try {
            TreeNode tree = new TreeNode("subtree");
            setTree(tree);
            TreeNode realTree = ((Question1)answer);
            ((Question1)answer).addSubTree(new TreeNode("test", 1), realTree, -1);
            fail(null, "No `TreeException` generated when we try and add an edge that doesn't exist");
        } catch(TreeException exn) {
            assertTrue(exn, true);
        } catch(NullPointerException exn) {
            fail(exn, "Your code should throw a `TreeException` when we try and add edges to a non-existent branch position. Consider the following: \n* What should happen when `edge = 0`?\n* What should happen when `edge = node.branch.length`?");
        } catch(ArrayIndexOutOfBoundsException exn) {
            fail(exn, "Your code should throw a `TreeException` when we try and add edges to a negative branch position.");
        } catch(Throwable exn) {
            fail(exn, "Expected a `TreeException` but your code generated: " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testInvalidEdge2

    public void testInvalidEdge3() {
        try {
            TreeNode tree = new TreeNode("subtree");
            setTree(tree);
            TreeNode realTree = ((Question1)answer);
            ((Question1)answer).addSubTree(new TreeNode("test", 1), realTree, 1);
            fail(null, "No `TreeException` generated when we try and add an edge that doesn't exist. Consider the following: \n* What should happen when `edge = 0`?\n* What should happen when `edge = node.branch.length`?");
        } catch(TreeException exn) {
            assertTrue(exn, true);
        } catch(NullPointerException exn) {
            fail(exn, "Your code should throw a `TreeException` when we try and add edges to a non-existent branch position. Consider the following: \n* What should happen when `edge = 0`?\n* What should happen when `edge = node.branch.length`?");
        } catch(ArrayIndexOutOfBoundsException exn) {
            fail(exn, "Your code should throw a `TreeException` when we try and add edges to a non-existent branch position. Consider the following: \n* What should happen when `edge = 0`?\n* What should happen when `edge = node.branch.length`?");
        } catch(Throwable exn) {
            fail(exn, "Expected a `TreeException` but your code generated: " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testInvalidEdge3

    public void testInvalidEdge4() {
        try {
            TreeNode tree = new TreeNode("subtree", 1);
            setTree(tree);
            TreeNode realTree = ((Question1)answer);
            ((Question1)answer).addSubTree(new TreeNode("test", 1), realTree, 1);
            fail(null, "No `TreeException` generated when we try and add an edge that doesn't exist. Consider the following: \n* What should happen when `edge = 0`?\n* What should happen when `edge = node.branch.length`?");
        } catch(TreeException exn) {
            assertTrue(exn, true);
        } catch(NullPointerException exn) {
            fail(exn, "Your code should throw a `TreeException` when we try and add edges to a non-existent branch position. Consider the following: \n* What should happen when `edge = 0`?\n* What should happen when `edge = node.branch.length`?");
        } catch(ArrayIndexOutOfBoundsException exn) {
            fail(exn, "Your code should throw a `TreeException` when we try and add edges to a non-existent branch position. Consider the following: \n* What should happen when `edge = 0`?\n* What should happen when `edge = node.branch.length`?");
        } catch(Throwable exn) {
            fail(exn, "Expected a `TreeException` but your code generated: " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testInvalidEdge4
    
    public void testValidEdge1a() {
        try {
            TreeNode tree = new TreeNode("tree", 1);
            tree.setBranch(new TreeNode[]{ new TreeNode("leaf") });
            TreeNode subtree = new TreeNode("subtree");
            setTree(tree);
            TreeNode realTree = ((Question1)answer);
            ((Question1)answer).addSubTree(subtree, realTree, 0);
            assertTrue(null, "Your code has incorrectly added in a leaf node to a 1-branching tree", 
                ((String)(realTree.getData())).equals("tree") 
                && realTree.getBranch().length == 1
                && realTree.getBranch()[0] != null
                && ((String)(realTree.getBranch()[0].getData())).equals("subtree")
                && realTree.getBranch()[0].getBranch().length == 0);
        } catch(TreeException exn) {
            fail(exn, "Your code threw a `TreeException`. Possible causes: \n* Remember that the `root` node has a `label`.\n* Maybe you are using your parameters incorrectly?");
        } catch(NullPointerException exn) {
            fail(exn, "Your code threw an `NullPointerException`. Possible causes: \n* Remember that the `root` node has a `label`.\n* Maybe you are using your subtree and node parameters incorrectly?\n* Maybe you have assumed that edge numbering starts at 1?\n* Has your code forgotten to `exclude` the branch array's size as an index value?");
        } catch(ArrayIndexOutOfBoundsException exn) {
            fail(exn, "Your code threw an `ArrayIndexOutOfBoundsException`. Possible causes: \n* Remember that the `root` node has a `label`.\n* Maybe you are using your subtree and node parameters incorrectly?\n* Maybe you have assumed that edge numbering starts at 1?\n* Has your code forgotten to `exclude` the branch array's size as an index value?");
        } catch(Throwable exn) {
            fail(exn, "No exception's should be generated, but your code threw: " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testValidEdge1a
    
    public void testValidEdge1b() {
        TreeNode tree = new TreeNode("tree", 1);
        tree.setBranch(new TreeNode[]{ new TreeNode("leaf") });
        TreeNode subtree = new TreeNode("subtree");
        setTree(tree);
        TreeNode realTree = ((Question1)answer);
        try {
            ((Question1)answer).addSubTree(subtree, realTree, 0);
            if (!((String)(realTree.getData())).equals("tree")) {
                fail(null, "Your code has incorrectly added in a leaf node to a 1-branching tree");
            } // end of if-then
            if (realTree.getBranch().length != 1) { 
                fail(null, "Your code has incorrectly added in a leaf node to a 1-branching tree");
            } // end of if-then
            if (realTree.getBranch()[0] == null) {
                fail(null, "Your code has incorrectly added in a leaf node to a 1-branching tree");
            } // end of if-then
            assertTrue(new Error(), "Your code does **not** add in a `reference` to the given subtree - it adds in a **duplicate** of it!", 
                realTree.getBranch()[0] == subtree);
        } catch(TreeException exn) {
            fail(exn, "Your code threw a `TreeException`. Possible causes: \n* Remember that the `root` node has a `label`.\n* Maybe you are using your parameters incorrectly?");
        } catch(NullPointerException exn) {
            fail(exn, "Your code threw an `NullPointerException`. Possible causes: \n* Remember that the `root` node has a `label`.\n* Maybe you are using your subtree and node parameters incorrectly?\n* Maybe you have assumed that edge numbering starts at 1?\n* Has your code forgotten to `exclude` the branch array's size as an index value?");
        } catch(ArrayIndexOutOfBoundsException exn) {
            fail(exn, "Your code threw an `ArrayIndexOutOfBoundsException`. Possible causes: \n* Remember that the `root` node has a `label`.\n* Maybe you are using your subtree and node parameters incorrectly?\n* Maybe you have assumed that edge numbering starts at 1?\n* Has your code forgotten to `exclude` the branch array's size as an index value?");
        } catch(Throwable exn) {
            fail(exn, "No exception's should be generated, but your code threw: " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testValidEdge1b
   
    public void testValidEdge2() {
        try {
            TreeNode tree = new TreeNode("tree", 2);
            tree.setBranch(new TreeNode[]{ new TreeNode("leaf1"), new TreeNode("leaf2") });
            TreeNode subtree = new TreeNode("subtree");
            setTree(tree);
            TreeNode realTree = ((Question1)answer);
            ((Question1)answer).addSubTree(subtree, realTree, 0);
            assertTrue(null, "Your code has incorrectly added in a leaf node to a 2-branching tree", 
                ((String)(realTree.getData())).equals("tree") 
                && realTree.getBranch().length == 2
                && realTree.getBranch()[0] != null
                && realTree.getBranch()[1] != null
                && ((String)(realTree.getBranch()[0].getData())).equals("subtree")
                && ((String)(realTree.getBranch()[1].getData())).equals("leaf2")
                && realTree.getBranch()[0].getBranch().length == 0
                && realTree.getBranch()[1].getBranch().length == 0);
        } catch(TreeException exn) {
            fail(exn, "Your code threw a `TreeException`. Possible causes: \n* Remember that the `root` node has a `label`.\n* Maybe you are using your parameters incorrectly?");
        } catch(NullPointerException exn) {
            fail(exn, "Your code threw an `NullPointerException`. Possible causes: \n* Remember that the `root` node has a `label`.\n* Maybe you are using your subtree and node parameters incorrectly?\n* Maybe you have assumed that edge numbering starts at 1?\n* Has your code forgotten to `exclude` the branch array's size as an index value?");
        } catch(ArrayIndexOutOfBoundsException exn) {
            fail(exn, "Your code threw an `ArrayIndexOutOfBoundsException`. Possible causes: \n* Remember that the `root` node has a `label`.\n* Maybe you are using your subtree and node parameters incorrectly?\n* Maybe you have assumed that edge numbering starts at 1?\n* Has your code forgotten to `exclude` the branch array's size as an index value?");
        } catch(Throwable exn) {
            fail(exn, "No exception's should be generated, but your code threw: " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testValidEdge2
   
    public void testValidEdge3() {
        try {
            TreeNode tree = new TreeNode("tree", 2);
            tree.setBranch(new TreeNode[]{ new TreeNode("leaf1"), new TreeNode("leaf2") });
            TreeNode subtree = new TreeNode("subtree");
            setTree(tree);
            TreeNode realTree = ((Question1)answer);
            ((Question1)answer).addSubTree(subtree, realTree, 1);
            assertTrue(null, "Your code has incorrectly added in a leaf node to a 2-branching tree", 
                ((String)(realTree.getData())).equals("tree") 
                && realTree.getBranch().length == 2
                && realTree.getBranch()[0] != null
                && realTree.getBranch()[1] != null
                && ((String)(realTree.getBranch()[0].getData())).equals("leaf1")
                && ((String)(realTree.getBranch()[1].getData())).equals("subtree")
                && realTree.getBranch()[0].getBranch().length == 0
                && realTree.getBranch()[1].getBranch().length == 0);
        } catch(TreeException exn) {
            fail(exn, "Your code threw a `TreeException`. Possible causes: \n* Remember that the `root` node has a `label`.\n* Maybe you are using your parameters incorrectly?");
        } catch(NullPointerException exn) {
            fail(exn, "Your code threw an `NullPointerException`. Possible causes: \n* Remember that the `root` node has a `label`.\n* Maybe you are using your subtree and node parameters incorrectly?\n* Maybe you have assumed that edge numbering starts at 1?\n* Has your code forgotten to `exclude` the branch array's size as an index value?");
        } catch(ArrayIndexOutOfBoundsException exn) {
            fail(exn, "Your code threw an `ArrayIndexOutOfBoundsException`. Possible causes: \n* Remember that the `root` node has a `label`.\n* Maybe you are using your subtree and node parameters incorrectly?\n* Maybe you have assumed that edge numbering starts at 1?\n* Has your code forgotten to `exclude` the branch array's size as an index value?");
        } catch(Throwable exn) {
            fail(exn, "No exception's should be generated, but your code threw: " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testValidEdge3
   
    public void testValidEdge4() {
        try {
            TreeNode tree = new TreeNode("tree", 3);
            tree.setBranch(new TreeNode[]{ new TreeNode("leaf1"), new TreeNode("leaf2"), new TreeNode("leaf3") });
            TreeNode subtree = new TreeNode("subtree");
            setTree(tree);
            TreeNode realTree = ((Question1)answer);
            ((Question1)answer).addSubTree(subtree, realTree, 0);
            assertTrue(null, "Your code has incorrectly added in a leaf node to a 3-branching tree", 
                ((String)(realTree.getData())).equals("tree") 
                && realTree.getBranch().length == 3
                && realTree.getBranch()[0] != null
                && realTree.getBranch()[1] != null
                && realTree.getBranch()[2] != null
                && ((String)(realTree.getBranch()[0].getData())).equals("subtree")
                && ((String)(realTree.getBranch()[1].getData())).equals("leaf2")
                && ((String)(realTree.getBranch()[2].getData())).equals("leaf3")
                && realTree.getBranch()[0].getBranch().length == 0
                && realTree.getBranch()[1].getBranch().length == 0
                && realTree.getBranch()[2].getBranch().length == 0);
       } catch(TreeException exn) {
            fail(exn, "Your code threw a `TreeException`. Possible causes: \n* Remember that the `root` node has a `label`.\n* Maybe you are using your parameters incorrectly?");
        } catch(NullPointerException exn) {
            fail(exn, "Your code threw an `NullPointerException`. Possible causes: \n* Remember that the `root` node has a `label`.\n* Maybe you are using your subtree and node parameters incorrectly?\n* Maybe you have assumed that edge numbering starts at 1?\n* Has your code forgotten to `exclude` the branch array's size as an index value?");
       } catch(ArrayIndexOutOfBoundsException exn) {
            fail(exn, "Your code threw an `ArrayIndexOutOfBoundsException`. Possible causes: \n* Remember that the `root` node has a `label`.\n* Maybe you are using your subtree and node parameters incorrectly?\n* Maybe you have assumed that edge numbering starts at 1?\n* Has your code forgotten to `exclude` the branch array's size as an index value?");
       } catch(Throwable exn) {
            fail(exn, "No exception's should be generated, but your code threw: " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testValidEdge4

    public void testValidEdge5() {
        try {
            TreeNode tree = new TreeNode("tree", 3);
            tree.setBranch(new TreeNode[]{ new TreeNode("leaf1"), new TreeNode("leaf2"), new TreeNode("leaf3") });
            TreeNode subtree = new TreeNode("subtree");
            setTree(tree);
            TreeNode realTree = ((Question1)answer);
            ((Question1)answer).addSubTree(subtree, realTree, 1);
            assertTrue(null, "Your code has incorrectly added in a leaf node to a 3-branching tree", 
                ((String)(realTree.getData())).equals("tree") 
                && realTree.getBranch().length == 3
                && realTree.getBranch()[0] != null
                && realTree.getBranch()[1] != null
                && realTree.getBranch()[2] != null
                && ((String)(realTree.getBranch()[0].getData())).equals("leaf1")
                && ((String)(realTree.getBranch()[1].getData())).equals("subtree")
                && ((String)(realTree.getBranch()[2].getData())).equals("leaf3")
                && realTree.getBranch()[0].getBranch().length == 0
                && realTree.getBranch()[1].getBranch().length == 0
                && realTree.getBranch()[2].getBranch().length == 0);
        } catch(TreeException exn) {
            fail(exn, "Your code threw a `TreeException`. Possible causes: \n* Remember that the `root` node has a `label`.\n* Maybe you are using your parameters incorrectly?");
        } catch(NullPointerException exn) {
            fail(exn, "Your code threw an `NullPointerException`. Possible causes: \n* Remember that the `root` node has a `label`.\n* Maybe you are using your subtree and node parameters incorrectly?\n* Maybe you have assumed that edge numbering starts at 1?\n* Has your code forgotten to `exclude` the branch array's size as an index value?");
        } catch(ArrayIndexOutOfBoundsException exn) {
            fail(exn, "Your code threw an `ArrayIndexOutOfBoundsException`. Possible causes: \n* Remember that the `root` node has a `label`.\n* Maybe you are using your subtree and node parameters incorrectly?\n* Maybe you have assumed that edge numbering starts at 1?\n* Has your code forgotten to `exclude` the branch array's size as an index value?");
        } catch(Throwable exn) {
            fail(exn, "No exception's should be generated, but your code threw: " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testValidEdge5

    public void testValidEdge6() {
        try {
            TreeNode tree = new TreeNode("tree", 3);
            tree.setBranch(new TreeNode[]{ new TreeNode("leaf1"), new TreeNode("leaf2"), new TreeNode("leaf3") });
            TreeNode subtree = new TreeNode("subtree");
            setTree(tree);
            TreeNode realTree = ((Question1)answer);
            ((Question1)answer).addSubTree(subtree, realTree, 2);
            assertTrue(null, "Your code has incorrectly added in a leaf node to a 3-branching tree", 
                ((String)(realTree.getData())).equals("tree") 
                && realTree.getBranch().length == 3
                && realTree.getBranch()[0] != null
                && realTree.getBranch()[1] != null
                && realTree.getBranch()[2] != null
                && ((String)(realTree.getBranch()[0].getData())).equals("leaf1")
                && ((String)(realTree.getBranch()[1].getData())).equals("leaf2")
                && ((String)(realTree.getBranch()[2].getData())).equals("subtree")
                && realTree.getBranch()[0].getBranch().length == 0
                && realTree.getBranch()[1].getBranch().length == 0
                && realTree.getBranch()[2].getBranch().length == 0);
        } catch(TreeException exn) {
            fail(exn, "Your code threw a `TreeException`. Possible causes: \n* Remember that the `root` node has a `label`.\n* Maybe you are using your parameters incorrectly?");
        } catch(NullPointerException exn) {
            fail(exn, "Your code threw an `NullPointerException`. Possible causes: \n* Remember that the `root` node has a `label`.\n* Maybe you are using your subtree and node parameters incorrectly?\n* Maybe you have assumed that edge numbering starts at 1?\n* Has your code forgotten to `exclude` the branch array's size as an index value?");
        } catch(ArrayIndexOutOfBoundsException exn) {
            fail(exn, "Your code threw an `ArrayIndexOutOfBoundsException`. Possible causes: \n* Remember that the `root` node has a `label`.\n* Maybe you are using your subtree and node parameters incorrectly?\n* Maybe you have assumed that edge numbering starts at 1?\n* Has your code forgotten to `exclude` the branch array's size as an index value?");
        } catch(Throwable exn) {
            fail(exn, "No exception's should be generated, but your code threw: " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testValidEdge6

} // end of class addSubTreeTests

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

public class containsTests extends FeedbackCase {

    public Question1 setTree(TreeNode tr) {
        ((Question1)answer).data = tr.data;
        ((Question1)answer).branch = tr.branch;
        return ((Question1)answer);
    } // end of method setTree
    
    private TreeNode contains(TreeNode tree, TreeNode node) throws TreeException, NotFound {
        setTree(tree);
        return ((Question1)answer).contains(node);
    } // end of method contains
    
    private TreeNode mkChain(Object[] label) {
        TreeNode tree = new TreeNode(label[label.length - 1]);
        for(int nos = label.length - 2; nos >= 0; nos--) {
            TreeNode oldtree = tree;
            tree = new TreeNode(label[nos], 1);
            tree.setBranch(new TreeNode[]{ oldtree });
        } // end of for-loop
        return tree;
    } // end of method mkChain
    
    public void testNull() {
        setTree(new TreeNode("dummy"));
        try {
            ((Question1)answer).contains(null);
            fail(null, "No `TreeException` generated when the parameter is **null**");
        } catch(TreeException exn) {
            assertTrue(exn, true);
        } catch(NullPointerException exn) {
            fail(exn, "Your code should throw a `TreeException` when it is passed a **null** paramter.");
        } catch(Throwable exn) {
            fail(exn, "Expected a `TreeException`, but we threw: " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testNull

    public void testPresent1() {
        try {
            TreeNode result = contains(new TreeNode("node"), mkChain(new String[]{ "node" }));
            assertTrue(null, "Failed to locate the only node of a leaf!", 
                result != null
                && result.getData() != null
                && result.getData().equals("node") 
                && result.getBranch() != null
                && result.getBranch().length == 0);
        } catch(NotFound exn) {
            fail(exn, "You can not locate the only node of a leaf!");
        } catch(TreeException exn) {
            fail(exn, "An unexpected `TreeException` was thrown!");
        } catch(Throwable exn) {
            fail(exn, "Unexpected exception thrown: " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testPresent1

    public void testPresent2() {
        try {
            TreeNode result = contains(new TreeNode("node"), mkChain(new String[]{ "node", "anothernode" }));
            assertTrue(null, "Failed to ignore the subtrees branches when locating the only node of a leaf!", 
                result != null
                && result.getData() != null
                && result.getData().equals("node") 
                && result.getBranch() != null
                && result.getBranch().length == 0);
        } catch(NotFound exn) {
            fail(exn, "You can not locate the only node of a leaf! Possible causes: \n* Maybe you have an error in your `recursive` solution:\n* are `all` return statements present?\n* Maybe you are using the `question's` equals method instead of simply comparing node labels?\n* What is the difference between `==` and the `equals(Object)` method?");
        } catch(TreeException exn) {
            fail(exn, "An unexpected `TreeException` was thrown!");
        } catch(Throwable exn) {
            fail(exn, "Unexpected exception thrown: " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testPresent2

    public void testPresent3() {
        try {
            TreeNode result = contains(new TreeNode("node"), mkChain(new String[]{ "node", "node1", "node2" }));
            assertTrue(null, "Failed to ignore the branches of a subtree when locating the only node of a leaf!", 
                result != null
                && result.getData() != null
                && result.getData().equals("node") 
                && result.getBranch() != null
                && result.getBranch().length == 0);
        } catch(NotFound exn) {
            fail(exn, "You can not locate the only node of a leaf! Possible causes: \n* Maybe you have an error in your `recursive` solution:\n* are `all` return statements present?\n* Maybe you are using the `question's` equals method instead of simply comparing node labels?\n* What is the difference between `==` and the `equals(Object)` method?");
        } catch(TreeException exn) {
            fail(exn, "An unexpected `TreeException` was thrown!");
        } catch(Throwable exn) {
            fail(exn, "Unexpected exception thrown: " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testPresent3

    public void testPresent4() {
        try {
            TreeNode tree = mkChain(new String[]{ "label1", "label2" });
            TreeNode result = contains(tree, new TreeNode("label1"));
            if (result == null) {
                fail(null, "You can not locate the root node of a chain!");
            } // end of if-then
            if (result.getData() == null) {
                fail(null, "In locating the root node of a chain, you returned a `null` labelled subtree!");
            } // end of if-then
            if (!result.getData().equals("label1")) {
                fail(null, "In locating the root node of a chain, you returned a subtree with the incorrect label!");
            } // end of if-then
            if (result.getBranch() == null) {
                fail(null, "In locating the root node of a chain, you returned a subtree with a `null` branch property!");
            } // end of if-then
            if (result.getBranch().length != 1) {
                fail(null, "In locating the root node of a chain, you returned a subtree with the wrong number of branches!");
            } // end of if-then
            if (result.getBranch()[0] == null) {
                fail(null, "In locating the root node of a chain, you returned a subtree with a `null` sub-subtree!");
            } // end of if-then
            if (result.getBranch()[0].getBranch() == null) {
                fail(null, "In locating the root node of a chain, you returned a subtree, whose sub-subtree had a `null` branch property!");
            } // end of if-then
            if (result.getBranch()[0].getData() == null) {
                fail(null, "In locating the root node of a chain, you returned a subtree, whose sub-subtree has a `null` label!");
            } // end of if-then
            if (!result.getBranch()[0].getData().equals("label2")) {
                fail(null, "In locating the root node of a chain, you returned a subtree, whose sub-subtree has an invalid label!");
            } // end of if-then
            if (result.getBranch()[0].getBranch().length != 0) {
                fail(null, "In locating the root node of a chain, you returned a subtree, whose sub-subtree `should` have been a leaf node!");
            } // end of if-then
            assertTrue(null, "You are **not** returning a node that is located `within` the current tree! Check that:\n* all `return` statements are present in your recursive solution.\n* you are not `duplicating` nodes/trees.",
                result == ((Question1)answer));
        } catch(NotFound exn) {
            fail(exn, "You can not locate the root node of a chain! Possible causes: \n* Maybe you have an error in your `recursive` solution:\n* are `all` return statements present?\n* Maybe you are using the `question's` equals method instead of simply comparing node labels?\n* What is the difference between `==` and the `equals(Object)` method?");
        } catch(TreeException exn) {
            fail(exn, "An unexpected `TreeException` was thrown!");
        } catch(Throwable exn) {
            fail(exn, "Unexpected exception thrown: " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testPresent4

    public void testPresent5() {
        try {
            TreeNode result = contains(mkChain(new String[]{ "label1", "label2" }), new TreeNode("label2"));
            assertTrue(null, "Failed to locate the leaf node of a chain!", 
                result != null
                && result.getData() != null
                && result.getData().equals("label2") 
                && result.getBranch() != null
                && result.getBranch().length == 0);
        } catch(NotFound exn) {
            fail(exn, "You can not locate the leaf node of a chain! Possible causes: \n* Maybe you have an error in your `recursive` solution:\n* are `all` return statements present?\n* Maybe you are using the `question's` equals method instead of simply comparing node labels?\n* What is the difference between `==` and the `equals(Object)` method?");
        } catch(TreeException exn) {
            fail(exn, "An unexpected `TreeException` was thrown!");
        } catch(Throwable exn) {
            fail(exn, "Unexpected exception thrown: " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testPresent5

    public void testPresent6() {
        try {
            TreeNode tree = new TreeNode("root", 2);
            tree.branch[0] = mkChain(new String[]{ "label1", "label2" });
            tree.branch[1] = mkChain(new String[]{ "label3", "label4" });
            TreeNode result = contains(tree, new TreeNode("label4"));
            if (result == null) {
                fail(null, "Whilst working with a branching tree: your method `unexpectibly` returns **null** when searching for an existing node of a branching tree!");
            } // end of if-then
            if (result.getData() == null) {
                fail(null, "Whilst working with a branching tree: your answer returns a `TreeNode` instance with a **null** label!");
            } // end of if-then
            if (result.getBranch() == null) {
                fail(null, "Whilst working with a branching tree: your answer returns a `TreeNode` instance with a **null** branch!");
            } // end of if-then
            if (result.getBranch().length != 0) {
                fail(null, "Whilst working with a branching tree: your answer should have returned a `leaf` node!");
            } // end of if-then
            assertTrue(null, "Failed to locate the leaf node of a branching tree!", result.getData().equals("label4"));
        } catch(NotFound exn) {
            fail(exn, "You can not locate the `leaf` node of a branching tree! Possible causes: \n* Maybe you have an error in your `recursive` solution:\n* are `all` return statements present?\n* Maybe you are using the `question's` equals method instead of simply comparing node labels?\n* What is the difference between `==` and the `equals(Object)` method?");
        } catch(TreeException exn) {
            fail(exn, "An unexpected `TreeException` was thrown!");
        } catch(Throwable exn) {
            fail(exn, "Unexpected exception thrown: " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testPresent6

    public void testPresent7() {
        try {
            TreeNode tree = new TreeNode("root", 2);
            tree.branch[0] = mkChain(new String[]{ "label1", "label2" });
            tree.branch[1] = mkChain(new String[]{ "label3", "label4" });
            TreeNode result = contains(tree, new TreeNode("label3"));
            if (result == null) {
                fail(null, "Your method `unexpectibly` returns **null** when searching for an existing node of a branching tree!");
            } // end of if-then
            if (result.getData() == null) {
                fail(null, "Your answer returns a `TreeNode` instance with a **null** label!");
            } // end of if-then
            if (result.getBranch() == null) {
                fail(null, "Your answer returns a `TreeNode` instance with a **null** branch!");
            } // end of if-then
            if (result.getBranch().length != 1) {
                fail(null, "Your answer should have returned a `1-branching` node!");
            } // end of if-then
            assertTrue(null, "Failed to locate an internal node of a branching tree!", result.getData().equals("label3"));
        } catch(NotFound exn) {
            fail(exn, "You can not locate the `internal` node of a branching tree! Possible causes: \n* Maybe you have an error in your `recursive` solution:\n* are `all` return statements present?\n* Maybe you are using the `question's` equals method instead of simply comparing node labels?\n* What is the difference between `==` and the `equals(Object)` method?");
        } catch(TreeException exn) {
            fail(exn, "An unexpected `TreeException` was thrown!");
        } catch(Throwable exn) {
            fail(exn, "Unexpected exception thrown: " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testPresent7

    public void testPresent8() {
        try {
            TreeNode result = contains(new TreeNode(new String("node")), mkChain(new String[]{ new String("node") }));
            assertTrue(null, "Failed to locate the only node of a leaf!", 
                result != null
                && result.getData() != null
                && result.getData().equals("node") 
                && result.getBranch() != null
                && result.getBranch().length == 0);
        } catch(NotFound exn) {
            fail(exn, "You can not locate the only node of a leaf!");
        } catch(TreeException exn) {
            fail(exn, "An unexpected `TreeException` was thrown!");
        } catch(Throwable exn) {
            fail(exn, "Unexpected exception thrown: " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testPresent8

    public void testPresent9() {
        try {
            TreeNode result = contains(mkChain(new String[]{ "root", "branch1", "branch2", "branch3", "branch4", "branch5", "branch6", new String("node") }), new TreeNode(new String("node")));
            assertTrue(null, "Failed to locate the last node of a chain!", 
                result != null
                && result.getData() != null
                && result.getData().equals("node") 
                && result.getBranch() != null
                && result.getBranch().length == 0);
        } catch(NotFound exn) {
            fail(exn, "You can not locate the last node of a chain!");
        } catch(TreeException exn) {
            fail(exn, "An unexpected `TreeException` was thrown!");
        } catch(Throwable exn) {
            fail(exn, "Unexpected exception thrown: " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testPresent9

    public void testNotPresent1() {
        try {
            TreeNode result = contains(mkChain(new String[]{ "label1" }), new TreeNode("data"));
            fail(null, "Failed to throw a `NotFound` exception when searching for a non-existent node.");
        } catch(NotFound exn) {
            assertTrue(exn, true);
        } catch(TreeException exn) {
            fail(exn, "An unexpected `TreeException` was thrown!");
        } catch(Throwable exn) {
            fail(exn, "Unexpected exception thrown: " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testNotPresent1

    public void testNotPresent2() {
        try {
            TreeNode result = contains(mkChain(new String[]{ "label1", "label2" }), new TreeNode("data"));
            fail(null, "Failed to throw a `NotFound` exception when searching for a non-existent node.");
        } catch(NotFound exn) {
            assertTrue(exn, true);
        } catch(TreeException exn) {
            fail(exn, "An unexpected `TreeException` was thrown!");
        } catch(Throwable exn) {
            fail(exn, "Unexpected exception thrown: " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testNotPresent2

    public void testNotPresent3() {
        try {
            TreeNode tree = new TreeNode("root", 2);
            tree.branch[0] = mkChain(new String[]{ "label1", "label2" });
            tree.branch[1] = mkChain(new String[]{ "label3", "label4" });
            TreeNode result = contains(tree, new TreeNode("data"));
            fail(null, "Failed to throw a `NotFound` exception when searching for a non-existent node.");
        } catch(NotFound exn) {
            assertTrue(exn, true);
        } catch(TreeException exn) {
            fail(exn, "An unexpected `TreeException` was thrown!");
        } catch(Throwable exn) {
            fail(exn, "Unexpected exception thrown: " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testNotPresent3

    public void testDataDependance1() {
        try {
            int[] label = new int[0];
            TreeNode result = contains(new TreeNode(label), mkChain(new int[][]{ label }));
            assertTrue(null, "Failed to locate the only node of a leaf!", 
                result != null
                && result.getData() != null 
                && ((int[])(result.getData())).length == 0
                && result.getBranch() != null
                && result.getBranch().length == 0);
        } catch(NotFound exn) {
            fail(exn, "You can not locate the only node of a leaf!");
        } catch(TreeException exn) {
            fail(exn, "An unexpected `TreeException` was thrown!");
        } catch(ClassCastException exn) {
            fail(exn, "Your code fails to deal with tree nodes that are **not** labeled with string data structures! Make sure that: \n* You have **not** written your answer assuming that `String`'s will be the only data type used\n* You have used the `correct` type of `equal`ity.");            
        } catch(Throwable exn) {
            fail(exn, "Unexpected exception thrown: " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testDataDependance1

    public void testDataDependance2() {
        try {
            TreeNode label = new TreeNode("label");
            TreeNode result = contains(new TreeNode(label), mkChain(new TreeNode[]{ label }));
            assertTrue(null, "Failed to locate the only node of a leaf!", 
                result != null
                && result.getData() != null 
                && ((result.getData()) instanceof TreeNode)
                && result.getBranch() != null
                && result.getBranch().length == 0);
        } catch(NotFound exn) {
            fail(exn, "You can not locate the only node of a leaf!");
        } catch(TreeException exn) {
            fail(exn, "An unexpected `TreeException` was thrown!");
        } catch(ClassCastException exn) {
            fail(exn, "Your code fails to deal with tree nodes that are **not** labeled with string data structures! Make sure that: \n* You have **not** written your answer assuming that `String`'s will be the only data type used\n* You have used the `correct` type of `equal`ity.");            
        } catch(Throwable exn) {
            fail(exn, "Unexpected exception thrown: " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testDataDependance2

} // end of class containsTests

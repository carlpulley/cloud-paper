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

public class equalsTests extends FeedbackCase {

    public Question1 setTree(TreeNode tr) {
        ((Question1)answer).data = tr.data;
        ((Question1)answer).branch = tr.branch;
        return ((Question1)answer);
    } // end of method setTree
    
    private boolean equals(TreeNode tree1, TreeNode tree2) throws TreeException {
        setTree(tree1);
        return ((Question1)answer).equals(tree2);
    } // end of method equals
    
    public void testNull1() {
        try {
            equals(new TreeNode("tree1"), null);   
            fail(null, "No `TreeException` thrown when comparing against a **null** tree");
        } catch(TreeException exn) {
            assertTrue(exn, true);
        } catch(NullPointerException exn) {
            fail(exn, "Your code should throw a `TreeException` when it encounters a **null** (sub)tree. Remember, such (sub)trees `might` be encountered during recursion.");
        } catch(Throwable exn) {
            fail(exn, "Expected a `TreeException` to be thrown, but instead we threw: " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testNull1
             
    public void testNull2() {
        try {
            TreeNode tree1 = new TreeNode("tree", 1);
            tree1.setBranch(new TreeNode[]{ null });
            TreeNode tree2 = new TreeNode("tree", 1);
            tree2.setBranch(new TreeNode[]{ new TreeNode("leaf") });
            equals(tree1, tree2);
            fail(null, "No `TreeException` generated with a **null** subtree: \n* `null` subtrees are **not** allowed with this method\n* check that your conditional `logic` correctly rejects `null` subtrees");
        } catch(TreeException exn) {
            assertTrue(exn, true);
        } catch(NullPointerException exn) {
            fail(exn, "Your code should throw a `TreeException` when it encounters a **null** (sub)tree. Remember, such (sub)trees `might` be encountered during recursion.");
        } catch(Throwable exn) {
            fail(exn, "Expected a `TreeException`, but we threw: " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testNull2

    public void testNull3() {
        try {
            TreeNode tree1 = new TreeNode("tree", 1);
            tree1.setBranch(new TreeNode[]{ new TreeNode("leaf") });
            TreeNode tree2 = new TreeNode("tree", 1);
            tree2.setBranch(new TreeNode[]{ null });
            equals(tree1, tree2);
            fail(null, "No `TreeException` generated with a **null** subtree: \n* `null` subtrees are **not** allowed with this method\n* check that your conditional `logic` correctly rejects `null` subtrees");
        } catch(TreeException exn) {
            assertTrue(exn, true);
        } catch(NullPointerException exn) {
            fail(exn, "Your code should throw a `TreeException` when it encounters a **null** (sub)tree. Remember, such (sub)trees `might` be encountered during recursion.");
        } catch(Throwable exn) {
            fail(exn, "Expected a `TreeException`, but we threw: " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testNull3
   
    public void testNull4() {
        TreeNode tree1 = new TreeNode("tree1");
        tree1.branch = null;
        TreeNode tree2 = new TreeNode("tree1");
        try {
            equals(tree1, tree2);   
            fail(null, "No `TreeException` thrown when comparing against a (sub)tree with **null** branch");
        } catch(TreeException exn) {
            assertTrue(exn, true);
        } catch(NullPointerException exn) {
            fail(exn, "Your code should throw a `TreeException` when it encounters a (sub)tree with a **null** branch. Remember, such (sub)trees `might` be encountered during recursion.");
        } catch(Throwable exn) {
            fail(exn, "Expected a `TreeException` to be thrown, but instead we threw: " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testNull4
    
    public void testNull5() {
        TreeNode tree1 = new TreeNode("tree1");
        TreeNode tree2 = new TreeNode("tree1");
        tree2.branch = null;
        try {
            equals(tree1, tree2);   
            fail(null, "No `TreeException` thrown when comparing against a (sub)tree with **null** branch");
        } catch(TreeException exn) {
            assertTrue(exn, true);
        } catch(NullPointerException exn) {
            fail(exn, "Your code should throw a `TreeException` when it encounters a (sub)tree with a **null** branch. Remember, such (sub)trees `might` be encountered during recursion.");
        } catch(Throwable exn) {
            fail(exn, "Expected a `TreeException` to be thrown, but instead we threw: " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testNull5

    public void testBranchError1() {
        try {
            assertTrue(null, "Failed to see that a leaf node and 1-branching tree were different. What is the difference between `==` and the `equals(Object)` method?",
                !equals(new TreeNode("tree"), new TreeNode("tree", 1)));
        } catch(Throwable exn) {
            fail(exn, "Expected **no** exception, but we threw: " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testBranchError1
    
    public void testBranchError2() {
        try {
            assertTrue(null, "Failed to see that a leaf node and 1-branching tree were different. What is the difference between `==` and the `equals(Object)` method?",
                !equals(new TreeNode("tree", 1), new TreeNode("tree")));
        } catch(ArrayIndexOutOfBoundsException exn) {
            fail(exn, "`ArrayIndexOutOfBoundsException` throwm. Check the following: \n* Is your recursion terminating correctly when it determines that we have differing trees? ");
        } catch(Throwable exn) {
            fail(exn, "Expected **no** exception, but we threw: " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testBranchError2
        
    public void testBranchError3() {
        try {
            assertTrue(null, "Failed to see a difference between 1-branching and 2-branching trees", 
                !equals(new TreeNode("tree", 2), new TreeNode("tree", 1)));
        } catch(Throwable exn) {
            fail(exn, "Expected **no** exception, but we threw: " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testBranchError3
        
    public void testBranchError4() {
        try {
            assertTrue(null, "Failed to see a difference between 1-branching and 2-branching trees", 
                !equals(new TreeNode("tree", 1), new TreeNode("tree", 2)));
        } catch(Throwable exn) {
            fail(exn, "Expected **no** exception, but we threw: " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testBranchError4
        
    public void testBranchError5() {
        try {
            assertTrue(null, "Failed to see a difference between 1-branching trees with different labels", 
                !equals(new TreeNode("tree1", 1), new TreeNode("tree2", 1)));
        } catch(Throwable exn) {
            fail(exn, "Expected **no** exception, but we threw: " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testBranchError5
        
    public void testBranchError6() {
        try {
            TreeNode tree1 = new TreeNode("tree", 1);
            tree1.setBranch(new TreeNode[]{ new TreeNode("leaf1") });
            TreeNode tree2 = new TreeNode("tree", 1);
            tree2.setBranch(new TreeNode[]{ new TreeNode("leaf2") });
            assertTrue(null, "Failed to see a difference between 1-branching trees with differing subtrees", 
                !equals(tree1, tree2));
        } catch(Throwable exn) {
            fail(exn, "Expected **no** exception, but we threw: " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testBranchError6
  
    public void testBranchError7() {
        try {
            TreeNode tree1 = new TreeNode("tree", 1);
            tree1.setBranch(new TreeNode[]{ new TreeNode("leaf") });
            TreeNode tree2 = new TreeNode("tree", 1);
            tree2.setBranch(new TreeNode[]{ new TreeNode("leaf", 1) });
            assertTrue(null, "Failed to see a difference between 1-branching trees with differing subtrees", 
                !equals(tree1, tree2));
        } catch(Throwable exn) {
            fail(exn, "Expected **no** exception, but we threw: " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testBranchError7
  
    public void testBranchError8() {
        try {
            TreeNode tree1 = new TreeNode("tree", 1);
            tree1.setBranch(new TreeNode[]{ new TreeNode("leaf", 1) });
            TreeNode tree2 = new TreeNode("tree", 1);
            tree2.setBranch(new TreeNode[]{ new TreeNode("leaf") });
            assertTrue(null, "Failed to see a difference between 1-branching trees with differing subtrees", 
                !equals(tree1, tree2));
        } catch(Throwable exn) {
            fail(exn, "Expected **no** exception, but we threw: " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testBranchError8
  
    public void testBranchValid1() {
        try {
            TreeNode tree1 = new TreeNode("tree", 1);
            tree1.setBranch(new TreeNode[]{ new TreeNode("leaf") });
            setTree(tree1);
            TreeNode tree2 = new TreeNode("tree", 1);
            tree2.setBranch(new TreeNode[]{ new TreeNode("leaf") });
            assertTrue(null, "Failed to see that two copies of the `same` 1-branching tree are the same", 
                ((Question1)answer).equals(tree2));
        } catch(Throwable exn) {
            fail(exn, "Expected **no** exception, but we threw: " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testBranchValid1
  
    public void testBranchValid2() {
        try {
            TreeNode tree1 = new TreeNode("tree", 1);
            tree1.setBranch(new TreeNode[]{ new TreeNode("leaf") });
            setTree(tree1);
            assertTrue(null, "Failed to see that a 1-branching tree, when compared with itself, is the same", 
                ((Question1)answer).equals((Question1)answer));
        } catch(Throwable exn) {
            fail(exn, "Expected **no** exception, but we threw: " + exn.getClass().getName() + " when comparing `this` tree with itself");
        } // end of try-catch
    } // end of method testBranchValid2

} // end of class equalsTests

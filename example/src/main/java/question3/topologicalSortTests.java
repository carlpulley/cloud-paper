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

public class topologicalSortTests extends FeedbackCase {

    public Question1 setTree(TreeNode tr) {
        ((Question3)answer).tree = new ModelAnswer2(new ModelAnswer1(tr.data));
        ((Question3)answer).tree.tree.branch = tr.branch;
        return ((Question3)answer).tree.tree;
    } // end of emthod setTree

    public void testNull1() {
        setTree(new TreeNode("dummy"));
        ((Question3)answer).tree = null;
        try {
            ((Question3)answer).topologicalSort();
            fail(null, "Expected a TreeException");
        } catch(TreeException exn) {
            assertTrue(exn, true);
        } catch(Throwable exn) {
            fail(exn, "Unexpected exception " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testNull1

    public void testLeaf1a() {
        TreeNode tree = new TreeNode("leaf");
        Question1 qTree = setTree(tree);
        try {
            TreeNode[] result = ((Question3)answer).topologicalSort();
            assertTrue(null, "Unexpected null value returned", result != null);
            assertTrue(null, "Returned array has the wrong size", result.length == 1);
            assertTrue(null, "Returned array has null values in it", result[0] != null);
            assertTrue(null, "Returned array has invalid tree nodes in it", result[0].data != null && result[0].branch != null);
            assertTrue(null, "Returned array has a node with an invalid branching factor", result[0].branch.length == 0);
            assertTrue(null, "Returned array has an incorrectly labelled node in it", result[0].data.equals("leaf"));
        } catch(Throwable exn) {
            fail(exn, "Unexpected exception " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testLeaf1a

    public void testLeaf1b() {
        TreeNode tree = new TreeNode("leaf");
        Question1 qTree = setTree(tree);
        try {
            TreeNode[] result = ((Question3)answer).topologicalSort();
            assertTrue(null, "Unexpected null value returned", result != null);
            assertTrue(null, "Returned array has the wrong size", result.length == 1);
            assertTrue(null, "Returned array has null values in it", result[0] != null);
            assertTrue(null, "Returned array has invalid tree nodes in it", result[0].data != null && result[0].branch != null);
            assertTrue(null, "Returned array has a node with an invalid branching factor", result[0].branch.length == 0);
            assertTrue(null, "Returned array has an incorrectly labeled node in it", result[0].data.equals("leaf"));
            assertTrue(null, "Returned array has a reference in it that does not belong in the original tree!", result[0] == tree || result[0] == qTree);
        } catch(Throwable exn) {
            fail(exn, "Unexpected exception " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testLeaf1b

    public void testChain1a() {
        TreeNode root = new TreeNode("root", 1);
        TreeNode leaf = new TreeNode("leaf");
        root.branch[0] = leaf;
        Question1 qTree = setTree(root);
        try {
            TreeNode[] result = ((Question3)answer).topologicalSort();
            assertTrue(null, "Unexpected null value returned", result != null);
            assertTrue(null, "Returned array has the wrong size", result.length == 2);
            assertTrue(null, "Returned array has a reference in it that does not belong in the original tree!", result[0] == root || result[0] == qTree);
            assertTrue(null, "Returned array has a reference in it that does not belong in the original tree!", result[1] == leaf);
        } catch(Throwable exn) {
            fail(exn, "Unexpected exception " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testChain1a

    public void testChain1b() {
        TreeNode root = new TreeNode("root", 1);
        TreeNode leaf = new TreeNode("leaf");
        root.branch[0] = leaf;
        Question1 qTree = setTree(root);
        try {
            TreeNode[] result = ((Question3)answer).topologicalSort();
            assertTrue(null, "Unexpected null value returned", result != null);
            assertTrue(null, "Returned array has the wrong size", result.length == 2);
            if ((result[1] == root || result[1] == qTree) && result[0] == leaf) {
                fail(new Error(), "Returned array is correct, **but** it is reversed!");
            } // end of if-then
            assertTrue((Exception)null, true);
        } catch(Throwable exn) {
            fail(exn, "Unexpected exception " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testChain1b

    public void testChain2a() {
        TreeNode root = new TreeNode("root", 1);
        TreeNode subtree = new TreeNode("subtree", 1);
        TreeNode leaf = new TreeNode("leaf");
        root.branch[0] = subtree;
        subtree.branch[0] = leaf;
        Question1 qTree = setTree(root);
        try {
            TreeNode[] result = ((Question3)answer).topologicalSort();
            assertTrue(null, "Unexpected null value returned", result != null);
            assertTrue(null, "Returned array has the wrong size", result.length == 3);
            assertTrue(null, "Returned array has a label in it that does not belong in the original tree!", result[0].data.equals(root.data));
            assertTrue(null, "Returned array has a label in it that does not belong in the original tree!", result[1].data.equals(subtree.data));
            assertTrue(null, "Returned array has a label in it that does not belong in the original tree!", result[2].data.equals(leaf.data));
        } catch(Throwable exn) {
            fail(exn, "Unexpected exception " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testChain2a

    public void testChain2b() {
        TreeNode root = new TreeNode("root", 1);
        TreeNode subtree = new TreeNode("subtree", 1);
        TreeNode leaf = new TreeNode("leaf");
        root.branch[0] = subtree;
        subtree.branch[0] = leaf;
        Question1 qTree = setTree(root);
        try {
            TreeNode[] result = ((Question3)answer).topologicalSort();
            assertTrue(null, "Unexpected null value returned", result != null);
            assertTrue(null, "Returned array has the wrong size", result.length == 3);
            assertTrue(null, "Returned array has a reference in it that does not belong in the original tree!", result[0] == root || result[0] == qTree);
            assertTrue(null, "Returned array has a reference in it that does not belong in the original tree!", result[1] == subtree);
            assertTrue(null, "Returned array has a reference in it that does not belong in the original tree!", result[2] == leaf);
        } catch(Throwable exn) {
            fail(exn, "Unexpected exception " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testChain2b
    
    public void testTree1() {
        TreeNode root = new TreeNode("root", 2);
        TreeNode leaf1 = new TreeNode("leaf1");
        TreeNode leaf2 = new TreeNode("leaf2");
        root.branch = new TreeNode[]{ leaf1, leaf2 };
        Question1 qTree = setTree(root);        
        try {
            TreeNode[] result = ((Question3)answer).topologicalSort();
            assertTrue(null, "Unexpected null value returned", result != null);
            assertTrue(null, "Returned array has the wrong size", result.length == 3);
            assertTrue(null, "Returned array has a reference in it that does not belong in the original tree!", result[0] == root || result[0] == qTree);
            assertTrue(null, "Returned array has a reference in it that does not belong in the original tree!", (result[1] == leaf1 && result[2] == leaf2) || (result[1] == leaf2 && result[2] == leaf1));
         } catch(Throwable exn) {
            fail(exn, "Unexpected exception " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testTree1
  
    public void testTree2() {
        TreeNode root = new TreeNode("root", 2);
        TreeNode subtree1 = new TreeNode("subtree1", 1);
        TreeNode subtree2 = new TreeNode("subtree2", 1);
        TreeNode leaf1 = new TreeNode("leaf1");
        TreeNode leaf2 = new TreeNode("leaf2");
        root.branch = new TreeNode[]{ subtree1, subtree2 };
        subtree1.branch[0] = leaf1;
        subtree2.branch[0] = leaf2;
        Question1 qTree = setTree(root);        
        try {
            TreeNode[] result = ((Question3)answer).topologicalSort();
            assertTrue(null, "Unexpected null value returned", result != null);
            assertTrue(null, "Returned array has the wrong size", result.length == 5);
            assertTrue(null, "Returned array has a reference (" + result[0].toString() + ") in it that does not belong in the original tree!", result[0] == root || result[0] == qTree);
            assertTrue(null, "Returned array has a reference in it that does not belong in the original tree!", 
                (result[1] == subtree1 && result[2] == subtree2 && result[3] == leaf1 && result[4] == leaf2) 
                || (result[1] == subtree1 && result[2] == subtree2 && result[3] == leaf2 && result[4] == leaf1) 
                || (result[1] == subtree1 && result[2] == leaf1 && result[3] == subtree2 && result[4] == leaf2) 
                || (result[1] == subtree2 && result[2] == subtree1 && result[3] == leaf1 && result[4] == leaf2) 
                || (result[1] == subtree2 && result[2] == subtree1 && result[3] == leaf2 && result[4] == leaf1) 
                || (result[1] == subtree2 && result[2] == leaf2 && result[3] == subtree1 && result[4] == leaf1));
         } catch(Throwable exn) {
            fail(exn, "Unexpected exception " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testTree2
    
    public void testTree3() {
        TreeNode root = new TreeNode("root", 2);
        TreeNode subtree1 = new TreeNode("subtree1", 1);
        TreeNode subtree2 = new TreeNode("subtree2", 1);
        TreeNode leaf = new TreeNode("leaf");
        root.branch = new TreeNode[]{ subtree1, subtree2 };
        subtree1.branch[0] = leaf;
        subtree2.branch[0] = leaf;
        Question1 qTree = setTree(root);        
        try {
            TreeNode[] result = ((Question3)answer).topologicalSort();
            assertTrue(null, "Unexpected null value returned", result != null);
            assertTrue(null, "Returned array has the wrong size", result.length == 4);
            assertTrue(null, "Returned array has a reference in it that does not belong in the original tree!", result[0] == root || result[0] == qTree);
            assertTrue(null, "Returned array has a reference in it that does not belong in the original tree!", 
                (result[1] == subtree1 && result[2] == subtree2 && result[3] == leaf) 
                || (result[1] == subtree2 && result[2] == subtree1 && result[3] == leaf));
         } catch(Throwable exn) {
            fail(exn, "Unexpected exception " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testTree3

} // end of class topologicalSortTests

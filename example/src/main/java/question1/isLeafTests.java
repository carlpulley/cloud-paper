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

public class isLeafTests extends FeedbackCase {

    public Question1 setTree(TreeNode tr) {
        ((Question1)answer).data = tr.data;
        ((Question1)answer).branch = tr.branch;
        return ((Question1)answer);
    } // end of method setTree

    public void testNull() {
        setTree(new TreeNode("dummy"));
        try {
            ((Question1)answer).isLeaf(null);
            fail(null, "No exception was generated!");
        } catch(TreeException exn) {
            assertTrue(exn, true);
        } catch(NullPointerException exn) {
            fail(exn, "Your code fails to throw a `TreeException` when testing **null** trees.");
        } catch(Throwable exn) {
            fail(exn, "The incorrect exception (ie. " + exn.getClass().getName() + ") was thrown.");
        } // end of try-catch
    } // end of method testNull

    public void testLeaf1() {
        setTree(new TreeNode("dummy"));
        try {
            assertTrue(null, "Failed to spot a valid leaf node!", ((Question1)answer).isLeaf(new TreeNode("data")));
        } catch(Throwable exn) {
            fail(exn, "An inappropriate exception was thrown: " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testLeaf1

    public void testLeaf2a() {
        setTree(new TreeNode("dummy"));
        try {
            TreeNode node = new TreeNode(null);
            node.setBranch(null);
            ((Question1)answer).isLeaf(node);
            fail(null, "Failed to generate an exception with a malformed leaf node and when the label `null`!");
        } catch(TreeException exn) {
            assertTrue(exn, true);
        } catch(NullPointerException exn) {
            fail(exn, "Your code failed to generate a `TreeException` when presented with a leaf node whose `branch` property is **null**.");
        } catch(Throwable exn) {
            fail(exn, "An inappropriate exception was thrown: " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testLeaf2a

    public void testLeaf2b() {
        setTree(new TreeNode("dummy"));
        try {
            TreeNode node = new TreeNode("label");
            node.setBranch(null);
            ((Question1)answer).isLeaf(node);
            fail(null, "Failed to generate an exception with a malformed leaf node!");
        } catch(TreeException exn) {
            assertTrue(exn, true);
        } catch(NullPointerException exn) {
            fail(exn, "Your code failed to generate a `TreeException` when presented with a leaf node whose `branch` property is **null**.");
        } catch(Throwable exn) {
            fail(exn, "An inappropriate exception was thrown: " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testLeaf2b

    public void testLeaf3() {
        setTree(new TreeNode("dummy"));
        try {
            assertTrue(null, "Failed to spot a valid non-leaf node!", ((Question1)answer).isLeaf(new TreeNode("data", 2)) == false);
        } catch(Throwable exn) {
            fail(exn, "An inappropriate exception was thrown: " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testLeaf3

    public void testLeaf4() {
        setTree(new TreeNode("dummy"));
        try {
            TreeNode tree = new TreeNode("data", 2);
            ((Question1)answer).data = tree.data;
            ((Question1)answer).branch = tree.branch;
            assertTrue(null, "You're testing `this` tree instead of the method parameter", ((Question1)answer).isLeaf(new TreeNode("leaf")) == true);
        } catch(Throwable exn) {
            fail(exn, "An inappropriate exception was thrown: " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testLeaf4

    public void testLeaf5() {
        setTree(new TreeNode("dummy"));
        try {
            TreeNode tree = new TreeNode("data");
            ((Question1)answer).data = tree.data;
            ((Question1)answer).branch = tree.branch;
            assertTrue(null, "You're testing `this` tree instead of the method parameter", ((Question1)answer).isLeaf(new TreeNode("leaf", 2)) == false);
        } catch(Throwable exn) {
            fail(exn, "An inappropriate exception was thrown: " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testLeaf5

} // end of class isLeafTests

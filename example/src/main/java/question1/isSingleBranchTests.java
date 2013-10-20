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

public class isSingleBranchTests extends FeedbackCase {

    public int getWeight() {
        return 2;
    } // end of method getWeight

    public Question1 setTree(TreeNode tr) {
        ((Question1)answer).data = tr.data;
        ((Question1)answer).branch = tr.branch;
        return ((Question1)answer);
    } // end of method setTree
    
    public void testNull() {
        setTree(new TreeNode("dummy"));
        try {
            ((Question1)answer).isSingleBranch(null);
            fail(null, "No exception was generated!");
        } catch(TreeException exn) {
            assertTrue(exn, true);
        } catch(NullPointerException exn) {
            fail(exn, "Your code failed to throw a <i>TreeException</i> when presented with a <b>null</b> tree.");
        } catch(Throwable exn) {
            fail(exn, "The incorrect exception (ie. " + exn.getClass().getName() + ") was thrown.");
        } // endof try-catch
    } // end of method testNull

    public void testBranch1() {
        setTree(new TreeNode("dummy"));
        try {
            assertTrue(null, "Failed to correctly spot a leaf node!", !((Question1)answer).isSingleBranch(new TreeNode("leaf")));
        } catch(Throwable exn) {
            fail(exn, "An inappropriate exception was thrown: " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testBranch1

    public void testBranch2() {
        setTree(new TreeNode("dummy"));
        try {
            TreeNode node = new TreeNode("node");
            node.setBranch(null);
            ((Question1)answer).isSingleBranch(node);
            fail(null, "Failed to generate an exception when testing a malformed leaf node!");
        } catch(TreeException exn) {
            assertTrue(exn, true);
        } catch(NullPointerException exn) {
            fail(exn, "Your code failed to throw a <i>TreeException</i> when presented with a leaf whose <i>branch</i> property was <b>null</b>.");
        } catch(Throwable exn) {
            fail(exn, "An inappropriate exception was thrown: " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testBranch2

    public void testBranch3() {
        setTree(new TreeNode("dummy"));
        try {
            assertTrue(null, "Failed to correctly diagnose a 1-branching node!", ((Question1)answer).isSingleBranch(new TreeNode("root", 1)));
        } catch(Throwable exn) {
            fail(exn, "An inappropriate exception was thrown: " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testBranch3

    public void testBranch4() {
        setTree(new TreeNode("dummy"));
        try {
            assertTrue(null, "Failed to correctly diagnose a multiply-branching node!", !((Question1)answer).isSingleBranch(new TreeNode("root", 2)));
        } catch(Throwable exn) {
            fail(exn, "An inappropriate exception was thrown: " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testBranch4

    public void testBranch5() {
        setTree(new TreeNode("dummy"));
        try {
            TreeNode tree = new TreeNode("root", 2);
            ((Question1)answer).data = tree.data;
            ((Question1)answer).branch = tree.branch;
            assertTrue(null, "You're testing <i>this</i> tree instead of the method parameter", ((Question1)answer).isSingleBranch(new TreeNode("subtree", 1)));
        } catch(Throwable exn) {
            fail(exn, "An inappropriate exception was thrown: " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testBranch5

    public void testBranch6() {
        setTree(new TreeNode("dummy"));
        try {
            TreeNode tree = new TreeNode("root", 1);
            ((Question1)answer).data = tree.data;
            ((Question1)answer).branch = tree.branch;
            assertTrue(null, "You're testing <i>this</i> tree instead of the method parameter", !((Question1)answer).isSingleBranch(new TreeNode("subtree", 2)));
        } catch(Throwable exn) {
            fail(exn, "An inappropriate exception was thrown: " + exn.getClass().getName());
        } // end of try-catch
    } // end of method testBranch6

} // end of class isSingleBranchTests

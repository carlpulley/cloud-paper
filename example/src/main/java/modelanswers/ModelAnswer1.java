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

public class ModelAnswer1 extends Question1 {

    /**
     * Needed to ensure that <i>constructor chaining</i> works correctly!
     */
    public ModelAnswer1(Object label) {
        super(label);
    } // end of constructor function

    /**
     * Needed to ensure that <i>constructor chaining</i> works correctly!
     */
    public ModelAnswer1(Object label, int branches) {
        super(label, branches);
    } // end of constructor function

    public ModelAnswer1(TreeNode tree) {
        super(tree);
    } // end of constructor function

    public boolean isLeaf(TreeNode node) throws TreeException {
        if (node == null || node.branch == null) {
            throw new TreeException();
        } // end of if-then
        return node.branch.length == 0;
    } // end of method isLeaf
    
    public boolean isSingleBranch(TreeNode node) throws TreeException {
        if (node == null || node.branch == null) {
            throw new TreeException();
        } // end of if-then
        return node.branch.length == 1;
    } // end of method isManyBranching
    
    public boolean isManyBranching(TreeNode node) throws TreeException {
        if (node == null || node.branch == null) {
            throw new TreeException();
        } // end of if-then
        return node.branch.length >= 2;
    } // end of method isManyBranching
    
    public TreeNode contains(TreeNode node) throws TreeException, NotFound {
        System.out.println("AspectJ called me!!");
        if (node == null) {
            throw new TreeException();
        } // end of if-then
        return contains(this, node.getData());
    } // end of method contains
    
    private TreeNode contains(TreeNode subtree, Object label) throws TreeException, NotFound {
        if (subtree == null) {
            throw new TreeException();
        } // end of if-then
        if (subtree.branch == null) {
            throw new TreeException();
        } // end of if-then
        if (subtree.getData().equals(label)) {
            return subtree;
        } // end of if-then
        for (int branch = 0; branch < subtree.branch.length; branch++) {
            try {
                return contains(subtree.branch[branch], label);
            } catch(NotFound exn) {
                // try another subtree!
            } // end of try-catch
        } // end of for-loop
        throw new NotFound();
    } // end of method contains
    
    public void addSubTree(TreeNode subtree, TreeNode node, int edge) throws TreeException {
        if (node == null || subtree == null || node.branch == null || edge < 0 || edge >= node.branch.length) {
            throw new TreeException();
        } // end of if-then
        if (! containsReference(this, node)) {
			throw new TreeException();
		} // end of if-then
        node.branch[edge] = subtree;
    } // end of method addSubTree

    public void deleteSubTree(TreeNode node, int edge) throws TreeException {
        if (node == null || node.branch == null || edge < 0 || edge >= node.branch.length) {
            throw new TreeException();
        } // end of if-then
        if (! containsReference(this, node)) {
			throw new TreeException();
		} // end of if-then
        node.branch[edge] = null;
    } // end of method deleteSubTree

    public boolean equals(TreeNode tree) throws TreeException {
        return equals(this, tree);
    } // end of method equals
    
	private boolean containsReference(TreeNode tree, TreeNode node) throws TreeException {
		if (tree == node) {
			return true;
		} // end of if-then
		if (tree == null) {
			return false;
		} // end of if-then
		if (tree.branch == null) {
			throw new TreeException();
		} // end of if-then
		for (int edge = 0; edge < tree.branch.length; edge++) {
			if (containsReference(tree.branch[edge], node)) {
				return true;
			} // end of if-then
		} // end of for-loop
		return false;
	} // end of method containsReference
	
    private boolean equals(TreeNode tree1, TreeNode tree2) throws TreeException {
        if (tree1 == null || tree1.branch == null || tree2 == null || tree2.branch == null) {
            throw new TreeException();
        } // end of if-then
        if (tree1.data == null || tree2.data == null) {
            throw new TreeException();
        } // end of if-then
        if (! tree1.data.equals(tree2.data)) {
            return false;
        } // end of if-then
        if (tree1.branch.length != tree2.branch.length) {
            return false;
        } // end of if-then
        for (int branch = 0; branch < tree1.branch.length; branch++) {
            if (tree1.branch[branch] == null || tree2.branch[branch] == null) {
                throw new TreeException();
            } // end of if-then
            if (! equals(tree1.branch[branch], tree2.branch[branch])) {
                return false;
            } // end of if-then
        } // end of for-loop
        return true;
    }

} // end of class ModelAnswer1

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

/**
 * Implementations of this <i>abstract</i> class allow tree instances to be created and manipulated.
 * 
 * <p><b>WARNING:</b> This code defines an interface and so, <b>SHOULD NOT</b> be modified in any 
 * manner what so ever.
 */

abstract public class Question1 extends TreeNode implements Question {

    /**
     *  Needed for constructor chaining.
     */
    public Question1(Object data) {
        this(data, 0);
    } // end of constructor function
    
    /**
     *  Needed for constructor chaining.
     */
    public Question1(Object data, int branches) {
        super(data, branches);
    } // end of constructor function

    /**
     * <b>2 marks:</b> Tests whether the given tree node is a leaf or not.
     * 
     * @param node The tree node we wish to test.
     * @return <i>true</i> is returned precisely when the given tree node
     * is a leaf node.
     * 
     * @throws TreeException Whenever an error is encountered, this exception is thrown.
     */
    /*@ 
      @ behaviour success:
      @     requires node != null;
      @     assigns \nothing;
      @     ensures \result == (node.branch.length == 0);
      @ behaviour failure:
      @     requires node == null || node.branch == null;
      @     assigns \nothing;
      @     signals_only TreeException;
      @*/
    abstract public boolean isLeaf(TreeNode node) throws TreeException;
    
    /**
     * <b>2 marks:</b> Tests whether the given tree node has a single branch or not.
     * 
     * @param node The tree node we wish to test.
     * @return <i>true</i> is returned precisely when the given tree node
     * has <b>exactly</b> one branch.
     * 
     * @throws TreeException Whenever an error is encountered, this exception is thrown.
     */
    /*@
      @ behaviour success:
      @     requires node != null;
      @     assigns \nothing;
      @     ensures \result == (node.branch.length == 1);
      @ behaviour failure:
      @     requires node == null || node.branch == null;
      @     assigns \nothing;
      @     signals_only TreeException;
      @*/
    abstract public boolean isSingleBranch(TreeNode node) throws TreeException;
    
    /**
     * <b>2 marks:</b> Tests whether the given tree node has multiple branches or not.
     * 
     * @param node The tree node we wish to test.
     * @return <i>true</i> is returned precisely when the given tree node
     * has 2 or more branches.
     * 
     * @throws TreeException Whenever an error is encountered, this exception is thrown.
     */
    /*@
      @ behaviour success:
      @     requires node != null;
      @     assigns \nothing;
      @     ensures \result == (node.branch.length >= 2);
      @ behaviour failure:
      @     requires node == null || node.branch == null;
      @     assigns \nothing;
      @     signals_only TreeException;
      @*/
    abstract public boolean isManyBranching(TreeNode node) throws TreeException;
    
    /**
     * <b>10 marks:</b> Tests whether <i>this</i> tree instance contains the given node. If it does, we return 
     * its position.
     * 
     * @param node The node to search for. The <i>branch</i> property of this parameter 
     *             is <b>ignored</b>.
     * 
     * @return Position within <i>this</i> tree instance of the given node.
     * 
     * @throws TreeException Whenever an error is encountered, this exception is thrown.
     * @throws NotFound      Thrown if <i>this</i> tree instance does not contain the given node.
     */
    /*@
      @ requires node != null;
      @ assigns \nothing;
      @ behaviour node_found:
      @   assumes this.data == node.data;
      @   ensures \result == this;
      @ behaviour node_not_found:
      @   assumes this.data != node.data && this.branch.length == 0;
      @   signals_only NotFound;
      FIXME:
      @ behaviour recursive_search:
      @   assumes this.data != node.data && this.branch.length >= 1;
      @   ensures \exists integer n; 0 <= n && n < this.branch.length && this.branch[n] != null && this.branch[n].contains(node);
      @ signals (TreeException) node == null;
      @*/
    abstract public TreeNode contains(TreeNode node) throws TreeException, NotFound;
    
    /**
     * <b>5 marks:</b> Adds a supplied subtree to an existing branch of a
     * node within <i>this</i> tree instance.
     * 
     * @param subtree The subtree we wish to add to <i>this</i> tree instance.
     * @param node The node, within <i>this</i> tree, to which we need to attach the supplied tree.
     * @param edge The branch of the node at which we wish to add the given subtree.
     * 
     * @throws TreeException Whenever an error is encountered, this exception is thrown.
     */
    /*@
      @ behaviour success:
      @     requires subtree != null && node != null && 0 <= edge && edge < node.branch.length;
      @     assigns node.branch;
      @     ensures node.branch[edge] == subtree 
      @         && \forall integer n; 0 <= n && n < node.branch.length && n != edge ==> node.branch[n] == \old(node).branch[n];
      @ behaviour failure:
      @     requires subtree == null || node == null || 0 > edge || edge >= node.branch.length;
      @     assigns \nothing;
      @     signals_only TreeException;
      @*/
   abstract public void addSubTree(TreeNode subtree, TreeNode node, int edge) throws TreeException;
    
    /**
     * <b>5 marks:</b> Deletes the subtree from <i>this</i> tree instance that is located at a specified
     * branch of the given node.
     * 
     * @param node The node, within <i>this</i> tree instance, which has a branch to the subtree to be 
     *             deleted.
     * @param edge The branch of the given node, in <i>this</i> tree instance, that is to be deleted.
     * 
     * @throws TreeException Whenever an error is encountered, this exception is thrown.
     */
    /*@
      @ behaviour success:
      @     requires node != null && 0 <= edge && edge < node.branch.length;
      @     assigns node.branch;
      @     ensures node.branch[edge] == null 
      @         && \forall integer n; 0 <= n && n < node.branch.length && n != edge ==> node.branch[n] == \old(node).branch[n];
      @ behaviour failure:
      @     requires node == null || 0 > edge || edge >= node.branch.length;
      @     assigns \nothing;
      @     signals_only TreeException;
      @*/
    abstract public void deleteSubTree(TreeNode node, int edge) throws TreeException;
    
    /**
     * <b>10 marks:</b> Tests whether <i>this</i> tree instance is the same as the given tree.
     * 
     * <p>Two trees are equal when their root nodes have the <i>same</i> label and corresponding subtrees are
     * equal.
     * 
     * @param node The node to compare against <i>this</i> tree..
     * 
     * @throws TreeException Whenever an error is encountered, this exception is thrown.
     */
    /*@
      @ behaviour success:
      @     requires tree != null;
      @     assigns \nothing;
      @     ensures \result == (
      @         this.data == tree.data 
      @         && this.branch.length == tree.branch.length 
      @         && \forall integer n; 0 <= n && n < this.branch.length ==> this.branch[n] == tree.branch[n] || this.branch[n].equals(tree.branch[n]));
      @ behaviour failure:
      @     requires tree == null;
      @     assigns \nothing;
      @     signals_only TreeException;
      @*/
    abstract public boolean equals(TreeNode tree) throws TreeException;

} // end of class Question1

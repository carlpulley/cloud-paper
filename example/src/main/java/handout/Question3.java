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
 * Implementations of this <i>abstract</i> class allow topological sorts to be performed upon trees.
 * 
 * <p>Your answers should use the methods declared in questions 1 and 2 as much as possible.
 * 
 * <p><b>WARNING:</b> This code defines an interface and so, <b>SHOULD NOT</b> be modified in any manner 
 * what so ever.
 */

abstract public class Question3 implements Question {

    /**
     * The tree that implementations of this class encapsulate.
     */
    /*@
      @ invariant tree != null;
      @*/
    protected Question2 tree;

    /*@
      @ requires tree != null;
      @ assigns this.tree;
      @ ensures this.tree == tree;
      @*/
    public Question3(Question2 tree) {
        this.tree = tree;
    } // end of constructor function

    /**
     * <b>15 marks:</b> Using <i>this</i> tree, we return a topological sort.
     * 
     * <p><i>This</i> tree is <b>not</b> altered in <b>any</b> way by the topological sort.
     * 
     * @return The returned array is a list of pointers into <i>this</i> tree.
     * The pointers are arranged in such a way that the tree nodes they point at 
     * represent a topological sort.
     * 
     * @throws TreeException Whenever an error is encountered, this exception is thrown.
     */
    /*@
      @ assigns \nothing;
      @ ensures (new HashSet(Arrays.asList(\result))).equals(tree.convertTree().keySet())
      FIXME:
      @     && \forall TreeNode n, m; n.child(m) ==> index(n, \result) < index(m, \result);
      @*/
    abstract public TreeNode[] topologicalSort() throws TreeException;

    /**
     * <b>11 marks:</b> A method used to maintain the tree and reference count data structures 
     * during our topological sort.
     * 
     * @param tree      The tree (with a <i>Map</i> view) that we wish to delete a node from.
     * @param refCounts The reference count data structure for the given tree.
     * @param node      The node of the given tree that is to be deleted.
     */
    /*@
      @ requires tree != null && refCounts != null && node != null && refCounts == this.tree.referenceCounts(tree) && tree.containsKey(node);
      @ assigns \nothing;
      @ ensures tree == \old(tree).remove(node) && refCounts == \old(refCounts).remove(node);
      @*/
    abstract protected void deleteNode(java.util.Map tree, java.util.Map refCounts, TreeNode node);

} // end of abstract class Question3

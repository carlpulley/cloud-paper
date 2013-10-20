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
 * Implementations of this <i>abstract</i> class allow tree instances to be used within
 * reference counting topological sorts.
 * 
 * <p>Your answers should use the methods declared in question 1 as much as possible.
 * 
 * <p><b>WARNING:</b> This code defines an interface and so, <b>SHOULD NOT</b> be modified in any 
 * manner what so ever.
 */

abstract public class Question2 implements Question {

    /**
     * The tree that implementations of this class encapsulate.
     */
    /*@
      @ invariant tree != null;
      @*/
    protected Question1 tree;

    /*@
      @ requires tree != null;
      @ assigns this.tree;
      @ ensures this.tree == tree;
      @*/
    public Question2(Question1 tree) {
        this.tree = tree;
    } // end of constructor function

    /**
     * <b>15 marks:</b> Convenience method: generates a <i>Map</i> version of <i>this</i> tree.
     * 
     * @return The returned <i>map</i> uses <i>this</i> tree's <i>node</i>'s as keys (ie. keys have type <i>TreeNode</i>) 
     *         and <i>node arrays</i> as values (ie. values have type <i>TreeNode[]</i>).
     *         The values model the tree nodes (of <i>this</i> tree) accessible from a given key.
     *         
     * @throws TreeException Whenever an error is encountered, this exception is thrown.
     */
    /*@
      @ assigns \nothing;
      @ ensures \forall TreeNode n; this.tree.contains(n) <==> \result.containsKey(n) 
      @     && \forall TreeNode n; this.tree.contains(n) ==> new HashSet(Arrays.asList(\result.get(n))).equals(new HashSet(Arrays.asList(n.branch)));
      @*/
    abstract public java.util.Map convertTree() throws TreeException;

    /**
     * <b>15 marks:</b> For each <i>node</i> of our tree, we associate the node's reference count.
     * 
     * <p>A node's reference count is the number of tree nodes that have <i>branches</i>
     * leading to the node.
     * 
     * @param tree For <b>every</b> node of the given <i>tree</i>, we need to generate reference counts.
     * 
     * @return The returned <i>map</i> uses tree <i>node</i>'s as keys and <i>Integer</i>'s as values.
     *         The values model a key's reference count.
     */
    /*@
      @ requires tree != null;
      @ assigns \nothing;
      @ ensures tree.keySet().equals(\result.keySet())
      @     && \forall TreeNode n; \result.keySet().contains(n) ==> \result.get(n) == tree.get(n).length;
      @*/
    abstract protected java.util.Map referenceCounts(java.util.Map tree);
    
    /**
     * Method wrapping up <i>java.util.Map referenceCounts(java.util.Map)</i>.
     */
    final public java.util.Map referenceCounts() throws TreeException {
        return referenceCounts(convertTree());
    } // end of method referenceCounts

    /**
     * <b>8 marks:</b> Given the reference counts for a tree, we search for and return a node that has a reference count of 0.
     * 
     * @param refCounts A <i>map</i> recording the node reference counts for our tree. Nodes of the tree are 
     *                  <i>key</i>'s in the given data structure. <i>Integer</i>'s are the values of the given
     *                  data structure.
     * 
     * @return A node of the (implicit) tree which is not referenced by any other tree node (ie. its reference 
     *         count is 0). If the tree contains no such node, then <i>null</i> is returned.
     */
    /*@
      @ requires refCounts != null;
      @ assigns \nothing;
      @ ensures \result == null || (refCounts.containsKey(\result) && refCounts.get(\result) == 0);
      @*/
    abstract public TreeNode rootNode(java.util.Map refCounts);

} // end of class Question2

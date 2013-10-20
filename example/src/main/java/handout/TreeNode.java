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
 *  This class provides <i>basic</i> mechanisms by which we may generate 
 *  tree data structures.
 *  
 *  <p><b>WARNING:</b> This code <b>SHOULD NOT</b> be modified in any manner what so ever.
 */

public class TreeNode {
    
    /** <p>This field is used to label or store data at the current node instance.
     *  <p>This field may be null.
     */
    /*@
      @ invariant data != null;
      @*/
    protected Object data;
    
    /*@ 
      @ requires data != null;
      @ assigns this.data;
      @ ensures this.data == data;
      @*/
    public void setData(Object data) {
        this.data = data;
    } // end of method setData
    
    /** <p>This field is used to store the subtrees <i>hanging</i> from the current node instance.
     *  <p>This field may never be null.
     */
    /*@
      @ invariant branch != null;
      @*/
    protected TreeNode[] branch;
    
    /*@ 
      @ requires branch != null;
      @ assigns this.branch;
      @ ensures this.branch == branch;
      @*/
    public void setBranch(TreeNode[] branch) {
        this.branch = branch;
    } // end of method setBranch
    
    /**
     *  Creates a leaf node with the given label.
     *  
     *  @param data The label or data iobject to be stored at this node.
     */
    public TreeNode(Object data) {
        this(data, 0);
    } // end of constructor function
    
    /**
     *  Creates a branching node with the given label. All the trees subtrees
     *  are null.
     *  
     *  @param data The label or data object to be stored at this node.
     *  @param branches The subtrees to <i>hang</i> off this node.
     */
    /*@ 
      @ requires data != null && branches >= 0;
      @ assigns this.data, this.branch;
      @ ensures this.data == data 
      @     && this.branch.length == branches;
      @*/
    public TreeNode(Object data, int branches) {
        this.data = data;
        this.branch = new TreeNode[branches];
    } // end of constructor function
    
    /**
     *  Returns the label (if any) of <i>this</i> tree node.
     *  
     *  @returns <i>This</i> node's label.
     */
    /*@ 
      @ ensures \result == data;
      @*/
    public Object getData() {
        return this.data;
    } // end of method getData
    
    /**
     *  Returns the subtrees (if any) for <i>this</i> tree node.
     *  
     *  @returns An array of subtrees for <i>this</i> node.
     */
    /*@ 
      @ ensures \result == branch;
      @*/
    public TreeNode[] getBranch() {
        return this.branch;
    } // end of method getBranch
    
} // end of class TreeNode

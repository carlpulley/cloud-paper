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

import java.util.*;

public class ModelAnswer3 extends Question3 {

    public ModelAnswer3(Question2 tree) {
        super(tree);
    } // end of constructor function

    public TreeNode[] topologicalSort() throws TreeException {
		if (this.tree == null) {
			throw new TreeException();
		} // end of if-then
        Map tree = this.tree.convertTree();
        TreeNode[] result = new TreeNode[tree.size()];
        Map refCounts = this.tree.referenceCounts();
		for(int entry = 0; entry < result.length; entry++) {
            TreeNode node = this.tree.rootNode(refCounts);
            result[entry] = node;
            deleteNode(tree, refCounts, node);
        } // end of while-loop
        return result;
    } // end of method topologicalSort

    protected void deleteNode(Map tree, Map refCounts, TreeNode node) {
		if (tree == null || refCounts == null || node == null) {
			return;
		} // end of if-then
        refCounts.remove(node);
		tree.remove(node);
        TreeNode[] successors = node.branch;
        for(int nos = 0; nos < successors.length; nos++) {
            TreeNode succNode = successors[nos];
            if (refCounts.containsKey(succNode)) {
                int refCount = ((Integer)(refCounts.get(succNode))).intValue();
                refCounts.put(succNode, new Integer(refCount - 1));
            } // end of if-then
        } // end of for-loop
    } // end of deleteNode
    
} // end of class Answer3

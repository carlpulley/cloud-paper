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

import java.util.Map;
import java.util.Vector;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Collections;

public class ModelAnswer2 extends Question2 {

	public ModelAnswer2(Question1 tree) {
        super(tree);
    } // end of constructor function
    
    public Map convertTree() throws TreeException {
		if (tree == null || tree.branch == null) {
			throw new TreeException();
		} // end of if-then
        Map result = Collections.synchronizedMap(new HashMap());
        Vector toDo = new Vector();
        toDo.add(tree);
        while(! toDo.isEmpty()) {
            TreeNode item = (TreeNode)(toDo.firstElement());
			toDo.remove(0);
			result.put(item, item.getBranch());
			for(int nos = 0; nos < item.getBranch().length; nos++) {
				if (item.branch[nos] == null || item.branch[nos].branch == null) {
					throw new TreeException();
				} // end of if-then
				toDo.add(item.getBranch()[nos]);
			} // end of for-loop
        } // end of while-loop
        return result;
    } // end of method convertTree

    protected Map referenceCounts(Map tree) {
		if (tree == null) {
			return null;
		} // end of if-then
        Map result = Collections.synchronizedMap(new HashMap());
        for(Iterator keys = Collections.synchronizedSet(tree.keySet()).iterator(); keys.hasNext(); ) {
            TreeNode key = (TreeNode)(keys.next());
			if (key == null || key.branch == null) {
				return null;
			} // end of if-then
			for (int edge = 0; edge < key.branch.length; edge++) {
				if (key.branch[edge] == null || ! tree.containsKey(key.branch[edge])) {
					return null;
				} // end of if-then
			} // end of for-loop
            if (! result.containsKey(key)) {
                result.put(key, new Integer(0));
            } // end of if-then
            TreeNode[] successors = (TreeNode[])(tree.get(key));
			if (successors == null || key.branch.length != successors.length) {
				return null;
			} // end of if-then
            for(int nos = 0; nos < successors.length; nos++) {
                TreeNode ref = successors[nos];
				if (ref == null) {
					return null;
				} // end of if-then
                if (! result.containsKey(ref)) {
                    result.put(ref, new Integer(0));
                } // end of if-then
                int refCount = ((Integer)(result.get(ref))).intValue();
                result.put(ref, new Integer(refCount + 1));
            } // end of for-loop
        } // end of for-loop
        return result;
    } // end of method referenceCounts

    public TreeNode rootNode(Map refCounts) {
		if (refCounts == null) {
			return null;
		} // end of if-then
        for(Iterator keys = Collections.synchronizedSet(refCounts.keySet()).iterator(); keys.hasNext(); ) {
            TreeNode key = (TreeNode)(keys.next());
			if (key == null || refCounts.get(key) == null) {
				return null;
			} // end of if-then
            int refCount = ((Integer)(refCounts.get(key))).intValue();
            if (refCount == 0) {
                return key;
            } // end of if-then
        } // end of for-loop
        return null;
    } // end of method rootNode
    
} // end of class ModelAnswer2

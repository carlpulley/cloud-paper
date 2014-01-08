Use Case: Intermediate Level Coursework
=======================================

**Notes:** 
* student handout code may be found in the src/main/java directory [handout](https://github.com/carlpulley/cloud-paper/tree/master/example/src/main/java/handout)
* a model solution, along with unit tests and a testing harness, may be found in the src/main/java directory [assessment](https://github.com/carlpulley/cloud-paper/tree/master/example/src/main/java/assessment)
* property based tests may be found in the src/main/scala directory [assessment](https://github.com/carlpulley/cloud-paper/tree/master/example/src/main/scala/assessment)
* feedback workflows may be found in the src/main/scala directory [workflow](https://github.com/carlpulley/cloud-paper/tree/master/example/src/main/scala/workflow).

Introduction
------------

Whilst building a custom web browsing client, we need to ensure that the various HTML elements are drawn and rendered in the correct order[^1].

For example, consider the following HTML fragment:
```html
<frameset> 
    <frame name="menu">
    <frame name="display">
 </frameset>
```
It should cause a browser to display a two frame window. To do this, the frames _menu_ and _display_ **must** be rendered and drawn _before_ the window containing these frames has been built.

To solve this problem, we need first to build an appropriate tree data structures for storing the HTML dependency structure. Having done that, we may perform a _topological sorting_ of the tree data structure to determine the linear order in which the HTML elements should be drawn and rendered. Elements occurring latter in the linear order must be rendered and drawn before elements listed earlier.

The Java interfaces are annotated with [JML](http://en.wikipedia.org/wiki/Java_Modeling_Language) assertion specifications. Use these to help work out the code's functional requirements.

Tree Data Structures
--------------------

Tree data structures are built using two _primary_ types of nodes:
* _Leaf_ nodes - these nodes have no branches and are annotated with a label. 
* _Branching_ nodes - these nodes have 1 or more branches, and are annotated with a label. 

Both types of node may be created by invoking the `TreeNode` constructor function with the appropriate arguments.

The `Question1` class provides methods for creating and manipulating nodes of a tree data structure. In addition, methods within the `HTML` class may be used to produce instances of `TreeNode` for testing purposes.

For example, here is the tree data structure that results from the above _frameset_ example[^2]:

![Tree Data Structure](https://github.com/carlpulley/cloud-paper/raw/master/images/tree.gif)

Topological Sorts
-----------------

You are advised to look at your lecture notes and the relevant chapters of the following texts:

-> _Introduction to Algorithms_ <-
-> by T.H.Cormen, C.E.Leiserson, R.L.Rivest and C.Stein. MIT Press, 2009 <-

-> _Algorithms (4th edition)_ <-
-> by R.Sedgewick and K.Wayne. Addison-Wesley Professional, 2011 <-

to learn more about topological sorts.

The `Question2` class provides us with a tree data structure and a series of methods that are useful for implementing a topological ordering of its nodes. `Question3` is used to implement the actual topological sort.

Assignment Questions
--------------------

With all of the following questions, you do not need to completely answer the question in order to get marks awarded. However, it is necessary to ensure that your code answers **compile**.

Each method shall be assessed _in isolation_ with the model solution. Thus, incorrect code in one of your methods **should not** affect the marks awarded for other methods.

Further details as to what is required to implement each of these methods may be found by looking at the sources documentation.

1. Implement the _abstract_ class `Question1`. In doing this, you are required to provide implementations for each of the following methods:
  1. `void addSubTree(TreeNode, TreeNode, int)`  Adds a supplied subtree to an existing branch of a node within this tree instance. **5 marks**
  2. `TreeNode contains(TreeNode)`  Tests whether this tree instance contains the given node. **10 marks**
  3. `void deleteSubTree(TreeNode, int)`  Deletes the subtree from this tree instance that is located at a specified branch of the given node. **5 marks**
  4. `boolean equals(TreeNode)` Tests whether this tree instance is the same as the given tree. **10 marks**
  5. `boolean isLeaf(TreeNode)` Tests whether the given tree node is a leaf or not. **2 marks**
  6. `boolean isManyBranching(TreeNode)`  Tests whether the given tree node has multiple branches or not. **2 marks**
  7. `boolean isSingleBranch(TreeNode)`  Tests whether the given tree node has a single branch or not. **2 marks**

2. Implement the _abstract_ class `Question2`. In doing this, you are required to provide implementations for each of the following methods:
  1. `java.util.Map convertTree()` Convenience method: generates a Map version of this tree. **15 marks**
  2. `java.util.Map referenceCounts(java.util.Map)` For each node of our tree, we associate the node's reference count. **15 marks**
  3. `TreeNode rootNode(java.util.Map)`  Given the reference counts for a tree, we search for and return a node that has a reference count of 0. **8 marks**

3. Implement the _abstract_ class `Question3`. In doing this, you are required to provide implementations for each of the following methods:
  1. `void deleteNode(java.util.Map, java.util.Map, TreeNode)`  A method used to maintain the tree and reference count data structures during our topological sort. **11 marks**
  2. `TreeNode[] topologicalSort()`  Using this tree, we return a topological sort. **15 marks**

---

[^1]: As the browser will run on an experimental platform, a custom GUI library will be supplied by our customer.

[^2]: **Note:** the example tree has been annotated with integers to indicate what the reference count of each tree node will be.

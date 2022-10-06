package avlg;

import avlg.exceptions.UnimplementedMethodException;
// import pqueue.heaps.LinkedMinHeap.MinHeapNode;

//import org.w3c.dom.Node;

import avlg.exceptions.EmptyTreeException;
import avlg.exceptions.InvalidBalanceException;

/** <p>{@link AVLGTree}  is a class representing an <a href="https://en.wikipedia.org/wiki/AVL_tree">AVL Tree</a> with
 * a relaxed balance condition. Its constructor receives a strictly  positive parameter which controls the <b>maximum</b>
 * imbalance allowed on any subtree of the tree which it creates. So, for example:</p>
 *  <ul>
 *      <li>An AVL-1 tree is a classic AVL tree, which only allows for perfectly balanced binary
 *      subtrees (imbalance of 0 everywhere), or subtrees with a maximum imbalance of 1 (somewhere). </li>
 *      <li>An AVL-2 tree relaxes the criteria of AVL-1 trees, by also allowing for subtrees
 *      that have an imbalance of 2.</li>
 *      <li>AVL-3 trees allow an imbalance of 3.</li>
 *      <li>...</li>
 *  </ul>
 *
 *  <p>The idea behind AVL-G trees is that rotations cost time, so maybe we would be willing to
 *  accept bad search performance now and then if it would mean less rotations. On the other hand, increasing
 *  the balance parameter also means that we will be making <b>insertions</b> faster.</p>
 *
 * @author David Dayton
 *
 * @see EmptyTreeException
 * @see InvalidBalanceException
 * @see StudentTests
 */
public class AVLGTree<T extends Comparable<T>> {

    /* ********************************************************* *
     * Write any private data elements or private methods here...*
     * ********************************************************* */
	
	private class Node {
		private T data;
		private Node lChild, rChild;
		
		public Node() {
			data = null;
			lChild = null;
			rChild = null;	
		}
		
		public Node(T nodeData) {
			data = nodeData;
			lChild = null;
			rChild = null;
		}
		
	}

	private int maxImbalance;
	private Node root;
	private int numOfElements;
	
    /* ******************************************************** *
     * ************************ PUBLIC METHODS **************** *
     * ******************************************************** */

	/**
	 * Recursively calculates the height of the AVLG Tree
	 * @param node: The root of the tree/subtree
	 * @return: The height of the AVLG Tree
	 */
	public int calcHeight(Node node) {
		
		if (node == null) {
			return -1;
		} else if (node.data == null) {
			return -1;
		}
		
		int leftHeight = calcHeight(node.lChild);
		int rightHeight = calcHeight(node.rChild);
		return Math.max(leftHeight, rightHeight) + 1;
	}
	
	/**
	 * Recursively calculates the height of the node's left subtree
	 * @param node: The changing root of the tree/subtree
	 * @param root: The original root of the tree/subtree passed in (original node == root)
	 * @return: The height of the node's left subtree
	 */
	public int calcLeftSubtreeHeight(Node node, Node root) {
		
		if (root == null || node == null) {
			return -1;
		} else if (node.data == null) {
			return -1;
		}
		
		int leftHeight = calcLeftSubtreeHeight(node.lChild, root);
		// once you get the left subtree's height return early
		if (root == node) {
			return leftHeight;
		}
		int rightHeight = calcLeftSubtreeHeight(node.rChild, root);
		return Math.max(leftHeight, rightHeight) + 1;
	}
	
	/**
	 * Recursively calculates the height of the node's right subtree
	 * @param node: The changing root of the tree/subtree
	 * @param root: The original root of the tree/subtree passed in (original node == root)
	 * @return: The height of the node's right subtree
	 */
	public int calcRightSubtreeHeight(Node node, Node root) {
		
		if (root == null || node == null) {
			return -1;
		} else if (node.data == null) {
			return -1;
		}
		
		int rightHeight = calcRightSubtreeHeight(node.rChild, root);
		// once you get the left subtree's height return early
		if (root == node) {
			return rightHeight;
		}
		int leftHeight = calcRightSubtreeHeight(node.lChild, root);
		
		return Math.max(leftHeight, rightHeight) + 1;
	}
	
	/**
	 * Calculate a nodes balance (left height - right height)
	 * @param node: The node whose balance we want to calculate
	 * @return: The nodes balance
	 */
	public int calcNodeBalance(Node node) {
		int leftHeight = calcLeftSubtreeHeight(node, node);
		int rightHeight = calcRightSubtreeHeight(node, node);
		return leftHeight - rightHeight;
	}
	
	/**
	 * Finds the lowest node in the tree (most left node)
	 * @param root: The root of the tree or subtree
	 * @return: The lowest (most left) node in the tree/subtree
	 */
	public Node findMinNode(Node root) {
		
		while (root.lChild != null) {
			root = root.lChild;
		}
		
		return root;
	}
	
	/**
	 * Find the correct location to insert the passed in data
	 * Based on the insert function in the notes.
	 * @param root: The current root node of the tree/subtree
	 * @param node: The data whom which we want to insert
	 * @return: The new node inserted
	 */
	public Node insertNode(Node root, T data) {
		
		// if the AVL root is null. place the node there
		if (this.root.data == null) {
			this.root.data = data;
			return this.root;
		}
		
		// if the current root is null return a new node
		if (root == null) {
			return new Node(data);
		}
		
		// returns 0 if equal, a negative if key is less than, and a positive if key greater than
		if (data.compareTo(root.data) < 0) {
			root.lChild = insertNode(root.lChild, data);
			if (Math.abs(calcNodeBalance(root)) > this.maxImbalance ) {
				if (data.compareTo(root.lChild.data) < 0) { // new node was placed on left of child
					root = rotateRight(root);
				} else { // new node was placed on right of child
					root = rotateLeftRight(root);
				}
			}	
		} else { // key is >= root.data
			root.rChild = insertNode(root.rChild, data);
			if (Math.abs(calcNodeBalance(root)) > this.maxImbalance) {
				if (data.compareTo(root.rChild.data) >= 0) { // new node was placed on right of child
					root = rotateLeft(root);
				} else { // new node was placed on left of child
					root = rotateRightLeft(root);
				}
			}
		}	
		return root;
	}
	
	/**
	 * Find and delete the node whom which holds the data passed into this function
	 * @param root: The current root node of the tree/subtree
	 * @param data: The data whom which we want to delete
	 * @return: The node which was deleted, or null if this node was not in the tree
	 */
	public Node deleteNode(Node root, T data) {
		
		// find the node we want to delete.
		// if it has a two children child, find its in order successor (right once then all the way left)
		// if it doesn't then just delete the node
		// if it does replace the node's key with its in order successor then delete the in order successor node
		// then we need to rotate if this breaks the AVL condition
		// Always do the deletion from the parent because we need that node so we can check the AVL condition
				
		if (root == null) {
			return root;
		}
				
		// returns 0 if equal, a negative if key is less than, and a positive if key greater than
		if (data.compareTo(root.data) < 0) {
			root.lChild = deleteNode(root.lChild, data);
			
		} else if (data.compareTo(root.data) > 0) { // key is > root.data
			root.rChild = deleteNode(root.rChild, data);
			
		} else { // data = node.data
			if (root.lChild != null && root.rChild != null) { // has both children
				// find in order successor 
				root.data = findMinNode(root.rChild).data;
				
				// delete the original in order successor
				root.rChild = deleteNode(root.rChild, root.data);
				
			} else { // has <= 1 children
				// replace the node to delete with null or the child
				Node temp = null;
				if (root.lChild != null) {
					temp = root.lChild;
				} else {
					temp = root.rChild;
				}
				root = temp; 
			}
		}
		
		// before balancing check if the new root is null
		if (root == null) {
			return root;
		}
		
		int balance = calcNodeBalance(root);
		 
	    if (Math.abs(balance) > this.maxImbalance) {
	    	// determine which direction to rotate
	    	if (balance > 0) { // left heavy
	    		if (calcNodeBalance(root.lChild) >= 0) { // right left rotate
	    			root = rotateRight(root);
	    		} else { // right rotate
	    			root = rotateLeftRight(root);
	    		}
	    	} else { // right heavy
	    		if (calcNodeBalance(root.rChild) <= 0) { // left rotate
	    			root = rotateLeft(root);
	    		} else { // left right rotate
	    			root = rotateRightLeft(root);
	    		}
	    	}
	    }
	    		
		return root;
	}
	
	/**
	 * Rotate the passed in node right
	 * @param node: root of the subtree
	 * @return: the new root of the subtree
	 */
	public Node rotateRight(Node node) {
		Node temp = node.lChild;
		node.lChild = temp.rChild; 
		temp.rChild = node;
		return temp;
	}
	
	/**
	 * Rotate the passed in node left
	 * @param node: root of the subtree
	 * @return: the new root of the subtree
	 */
	public Node rotateLeft(Node node) {
		Node temp = node.rChild;
		node.rChild = temp.lChild;
		temp.lChild = node;
		return temp;
	}
	
	/** 
	 * Perform a left right rotation on the node
	 * @param node: Root of the subtree
	 * @return: the new root of the subtree
	 */
	public Node rotateLeftRight(Node node) {
		node.lChild = rotateLeft(node.lChild);
		node = rotateRight(node);
		return node;
	}
	
	/** 
	 * Perform a right left rotation on the node
	 * @param node: Root of the subtree
	 * @return: the new root of the subtree
	 */
	public Node rotateRightLeft(Node node) {
		node.rChild = rotateRight(node.rChild);
		node = rotateLeft(node);
		return node;
	}
	
	/** 
	 * Checks if this AVL tree is a BST
	 * @param node: The root node of this tree or subtree
	 * @return: True if the tree passed in is a binary search tree, otherwise false
	 */
	public boolean isBinarySearchTree(Node node) {
		
		if (node == null) {
			return true;
		}
		
		if (node.lChild != null) {
			if (node.lChild.data != null) {
			    // returns 0 if equal, a negative if data is less than, and a positive if data greater than
			    if (node.lChild.data.compareTo(node.data) >= 0) {
			   		 return false;
			   	}   	
			}
		}
	   
	    if (node.rChild != null) {
	    	if (node.rChild.data != null) {
		    	// returns 0 if equal, a negative if data is less than, and a positive if data greater than
		    	if (node.rChild.data.compareTo(node.data) < 0) {
		    		 return false;
		    	}   	
		    }
	    }    
		
	    if (isBinarySearchTree(node.lChild) == false || isBinarySearchTree(node.rChild) == false) {
	    	return false;
	    }       
	        
		return true;
	}
	
	/** 
	 * Checks if this AVL tree is balanced
	 * @param node: The root node of this tree or subtree
	 * @return: True if the tree is balanced, otherwise false
	 */
	public boolean isTreeBalanced(Node node) {
		
		if (node != null) {
			if (calcNodeBalance(node) > this.maxImbalance) {
		    	return false;
		    }
		}  
		
	    if (isBinarySearchTree(node.lChild) == false || isBinarySearchTree(node.rChild) == false) {
	    	return false;
	    }         
	        
		return true;
	}
	
	
	/** 
	 * A binary search which looks for the node with the key passed into it.
	 * @param key: The data you want to get from the tree
	 * @param node: The current root of the tree/subtree
	 * @return: The node that holds the key in it. 
	 */
	public Node binarySeach(T key, Node node) {
		
		if (node == null) {
			return node;
		}
		
		if (node.data == null) {
			return node;
		}
		
		// returns 0 if equal, a negative if key is less than, and a positive if key greater than
		if (key.compareTo(node.data) < 0) { 
			node = binarySeach(key, node.lChild);
			return node;
		}
		
		if (key.compareTo(node.data) > 0) {
			node = binarySeach(key, node.rChild);
			return node;
		}
		
		return node; 
	}
	
	/**************************************
	 ********* Required Functions *********
	 **************************************/
	
    /**
     * The class constructor provides the tree with the maximum imbalance allowed.
     * @param maxImbalance The maximum imbalance allowed by the AVL-G Tree.
     * @throws InvalidBalanceException if maxImbalance is a value smaller than 1.
     */
    public AVLGTree(int maxImbalance) throws InvalidBalanceException {
    	if (maxImbalance < 1) {
    		throw new InvalidBalanceException("Max imbalance must be greater than 1!");
    	}
    	this.maxImbalance = maxImbalance;
    	root = new Node();
    }

    /**
     * Insert key in the tree. You will <b>not</b> be tested on
     * duplicates! This means that in a deletion test, any key that has been
     * inserted and subsequently deleted should <b>not</b> be found in the tree!
     * s
     * @param key The key to insert in the tree.
     */
    public void insert(T key) {
    	root = insertNode(root, key);
    	numOfElements++;
    }

    /**
     * Delete the key from the data structure and return it to the caller.
     * @param key The key to delete from the structure.
     * @return The key that was removed, or {@code null} if the key was not found.
     * @throws EmptyTreeException if the tree is empty.
     */
    public T delete(T key) throws EmptyTreeException {
    	if (isEmpty() == true) {
    		throw new EmptyTreeException("Tree is empty!");
    	}
    	
    	if (this.numOfElements == 1) { // tree is only a root
    		// returns 0 if equal, a negative if data is less than, and a positive if data greater than
    	    if (root.data.compareTo(key) == 0) {
    	    	this.clear();
    	    	return key;
    	    }
    	}
    	
    	
    	root = deleteNode(root, key);
    	if (root == null) {
    		return null;
    	}
    	
    	this.numOfElements--;
    	
        return key;
    }

    /**
     * <p>Search for key in the tree. Return a reference to it if it's in there,
     * or {@code null} otherwise.</p>
     * @param key The key to search for.
     * @return key if key is in the tree, or {@code null} otherwise.
     * @throws EmptyTreeException if the tree is empty.
     */
    public T search(T key) throws EmptyTreeException {
    	if (isEmpty() == true) {
    		throw new EmptyTreeException("Tree is empty!");
    	}
    	
    	Node node = binarySeach(key, root);
    	if (node == null) {
    		return null;
    	}
    	
    	return node.data;
    }

    /**
     * Retrieves the maximum imbalance parameter.
     * @return The maximum imbalance parameter provided as a constructor parameter.
     */
    public int getMaxImbalance(){
    	return maxImbalance;
    }


    /**
     * <p>Return the height of the tree. The height of the tree is defined as the length of the
     * longest path between the root and the leaf level. By definition of path length, a
     * stub tree has a height of 0, and we define an empty tree to have a height of -1.</p>
     * @return The height of the tree. If the tree is empty, returns -1.
     */
    public int getHeight() {     
    	int height = -1;
    	// if the tree is empty return -1
    	if (isEmpty() == true) {
    		height = -1; // technically this is not needed 
    	} else if (root.lChild  == null && root.rChild == null) {
    		height = 0;
    	} else {
    		height = calcHeight(root);
    	}
    	
    	return height;
    }

    /**
     * Query the tree for emptiness. A tree is empty iff it has zero keys stored.
     * @return {@code true} if the tree is empty, {@code false} otherwise.
     */
    public boolean isEmpty() {
    	if (root == null) {
    		return true;
    	} else if (root.data == null) {
    		return true;
    	}
    	return false;
    }

    /**
     * Return the key at the tree's root node.
     * @return The key at the tree's root node.
     * @throws  EmptyTreeException if the tree is empty.
     */
    public T getRoot() throws EmptyTreeException{
    	if (isEmpty() == true) {
    		throw new EmptyTreeException("Tree is empty!");
    	}
    	return root.data;
    }


    /**
     * <p>Establishes whether the AVL-G tree <em>globally</em> satisfies the BST condition. This method is
     * <b>terrificly useful for testing!</b></p>
     * @return {@code true} if the tree satisfies the Binary Search Tree property,
     * {@code false} otherwise.
     */
    public boolean isBST() {
    	// an empty tree is a BST technically
    	if (isEmpty()) {
    		return true; 
    	}
    	return isBinarySearchTree(root);
    }


    /**
     * <p>Establishes whether the AVL-G tree <em>globally</em> satisfies the AVL-G condition. This method is
     * <b>terrificly useful for testing!</b></p>
     * @return {@code true} if the tree satisfies the balance requirements of an AVLG tree, {@code false}
     * otherwise.
     */
    public boolean isAVLGBalanced() {
        // throw new UnimplementedMethodException();       
    	if (isEmpty()) {
    		return true;
    	}
    	
    	return isTreeBalanced(root);
    }

    /**
     * <p>Empties the AVL-G Tree of all its elements. After a call to this method, the
     * tree should have <b>0</b> elements.</p>
     */
    public void clear(){
    	// Remove everything from the root and it will be garbage collected
    	if (root == null) {
    		return;
    	}
    	root.lChild = null;
    	root.rChild = null;
    	root.data = null;
    	numOfElements = 0;
    }


    /**
     * <p>Return the number of elements in the tree.</p>
     * @return  The number of elements in the tree.
     */
    public int getCount(){ 
    	return numOfElements;
    }
    
}




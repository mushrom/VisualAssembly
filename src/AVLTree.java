// *****************************************************************************
// Major Programming Assignment (Part 4)
// 
// *****************************************************************************

// based on the AVLTree class from the book
import java.util.Comparator;
import java.util.ArrayList;

public class AVLTree<E extends Comparable<E>> extends BST<E> {
	// AVL tree constructors are all wrappers around BST constructors
	public AVLTree() {
		super();
	}

	/** Create a BST with a specified comparator */
	public AVLTree(Comparator<E> c) {
		super(c);
	}

	public AVLTree(E[] objects) {
		super(objects);
	}

    /** Overload of createNewNode which creates an AVLTreeNode<E> */
	@Override
	public AVLTreeNode<E> createNewNode(E e) {
		return new AVLTreeNode<E>(e);
	}

	/** Insert an element e into the tree
	 *
	 *  @param e element to insert
	 *  @return true if successfully inserted
	 */
	@Override
	public boolean insert(E e) {
		boolean successful = super.insert(e);

		if (!successful) {
			return false;
		}

		balancePath(e);
		incrementSize(e, +1);
		return true;
	}

	/** Delete the given element from the tree
	 *
	 *  @param  e element to delete
	 *  @return tree if successfully deleted
	 **/
	@Override
	public boolean delete(E e) {
		// Copy-paste from BST.java, with balancePath(),
		// incrementSize() calls added after deletion
		if (root == null) {
			return false;
		}

		TreeNode<E> parent  = null;
		TreeNode<E> current = root;

		while (current != null) {
			if (c.compare(e, current.element) < 0) {
				parent = current;
				current = current.left;

			} else if (c.compare(e, current.element) > 0) {
				parent = current;
				current = current.right;

			} else {
				break;
			}
		}


		if (current == null) {
			// element doesn't exist in the tree, nothing to delete
			return false;
		}

		if (current.left == null) {
			if (parent == null) {
				root = current.right;
				incrementSize(root.element, -1);

			} else {
				if (c.compare(e, parent.element) < 0) {
					parent.left = current.right;
				} else {
					parent.right = current.right;
				}

				incrementSize(parent.element, -1);
				balancePath(parent.element);
			}

		} else {
			TreeNode<E> parentOfRightMost = current;
			TreeNode<E> rightMost = current.left;

			while (rightMost.right != null) {
				parentOfRightMost = rightMost;
				rightMost = rightMost.right;
			}

			current.element = rightMost.element;

			if (parentOfRightMost.right == rightMost) {
				parentOfRightMost.right = rightMost.left;

			} else {
				parentOfRightMost.left = rightMost.left;
			}

			incrementSize(parentOfRightMost.element, -1);
			balancePath(parentOfRightMost.element);
		}

		size--;
		return true;
	}

    /** Retrieve the nth smallest node from the tree, indexing from 1. */
	// (starting from one, for some reason? that's what the exercise says, so...)
	public E find(int index) {
		if (root == null) return null;

		return find((AVLTreeNode<E>)root, index);
	}

    /** Retrieve the nth smallest node from the tree at the given node,
     *  indexing from 1.
     */
	public E find(AVLTreeNode<E> node, int index) {
		if (node == null) return null;

		AVLTreeNode<E> left  = (AVLTreeNode<E>)node.left;
		AVLTreeNode<E> right = (AVLTreeNode<E>)node.right;

		int leftsize  = (left  == null)? 0 : left.size;
		int rightsize = (right == null)? 0 : right.size;

		if (left == null && index == 1)
			return node.element;
		if (left == null && index == 2)
			return (right == null)? null : right.element;

		if (left != null && index <= leftsize)
			return find(left, index);
		if (index == leftsize + 1)
			return node.element;
		if (right != null && index > leftsize + 1)
			return find(right, index - leftsize - 1);

		return null;
	}

	/** Retrieves the instance of an object that compares as equal
	 *  to the given value.
	 *
	 *  (this is useful for using an AVL tree as a map)
	 */
	public E findElem(E value) {
		return findElem((AVLTreeNode<E>)root, value);
	}

	public E findElem(AVLTreeNode<E> root, E value) {
		if (root == null) {
			return null;
		}

		int compare = value.compareTo(root.element);

		if (compare < 0) return findElem((AVLTreeNode<E>)root.left, value);
		if (compare > 0) return findElem((AVLTreeNode<E>)root.right, value);
		else             return root.element;
	}

    /** Prints a representation of the tree to the system output. */
	@Override
	public void dump() {
		dumpAVL((AVLTreeNode<E>)root, 0);
	}

    /** AVL-specific recursive helper for dump(). */
	public void dumpAVL(AVLTreeNode<E> node, int level) {
		if (node == null) {
			return;
		}

		dumpAVL((AVLTreeNode<E>)node.left, level + 1);

		for (int i = 0; i < level; i++) {
			System.out.print("   |");
		}
		//System.out.println("---+ " + node.element + "s" + node.size);

		System.out.printf("---+ %s (h=%d, s=%d)\n",
		                  node.element.toString(),
		                  node.height, node.size);

		dumpAVL((AVLTreeNode<E>)node.right, level + 1);
	}

    /** Updates the height attributes of the given node. */
	private void updateHeight(AVLTreeNode<E> node) {
		if (node.left == null && node.right == null) {
			// leaf node
			node.height = 0;

		} else if (node.left == null) {
			node.height = 1 + ((AVLTreeNode<E>)node.right).height;

		} else if (node.right == null) {
			node.height = 1 + ((AVLTreeNode<E>)node.left).height;

		} else {
			AVLTreeNode<E> left  = (AVLTreeNode<E>)node.left;
			AVLTreeNode<E> right = (AVLTreeNode<E>)node.right;
			node.height = 1 + Math.max(left.height, right.height);
		}
	}

    /** Updates the size attributes of the given node,
     *  similarly to updateHeight().
     */
	// TODO: Hmm, this gets called in the same order as
	//       updateHeight(), should size just be updated as
	//       part of updateHeight()?
	private void updateSize(AVLTreeNode<E> node) {
		AVLTreeNode<E> left  = (AVLTreeNode<E>)node.left;
		AVLTreeNode<E> right = (AVLTreeNode<E>)node.right;

		int leftsize  = (left  == null)? 0 : left.size;
		int rightsize = (right == null)? 0 : right.size;
		
		node.size = leftsize + rightsize + 1;
	}

    /** Increments the size attributes of the nodes leading to and including
     *  the node for the given element e by the specified amount.
     */
	private void incrementSize(E e, int amount) {
		ArrayList<TreeNode<E>> path = path(e);

		for (int i = path.size() - 1; i >= 0; i--) {
			AVLTreeNode<E> A = (AVLTreeNode<E>)path.get(i);
			A.size += amount;
		}
	}

    /** Rebalances the tree, starting from parents to the node
     *  for the given element e.
     */
	private void balancePath(E e) {
		ArrayList<TreeNode<E>> path = path(e);

		for (int i = path.size() - 1; i >= 0; i--) {
			AVLTreeNode<E> A = (AVLTreeNode<E>)path.get(i);
			updateHeight(A);

			AVLTreeNode<E> parent = (A == root)? null : (AVLTreeNode<E>)path.get(i - 1);

			switch (balanceFactor(A)) {
				case -2:
					if (balanceFactor((AVLTreeNode<E>)A.left) <= 0) {
						balanceLL(A, parent);
					} else {
						balanceLR(A, parent);
					}
					break;
				case +2:
					if (balanceFactor((AVLTreeNode<E>)A.right) >= 0) {
						balanceRR(A, parent);
					} else {
						balanceRL(A, parent);
					}
					break;
			}
		}
	}

    /** Returns the balance factor for the given node, calculated from
     *  the heights of the node's children.
     */
	private int balanceFactor(AVLTreeNode<E> node) {
		if (node.right == null) {
			return -node.height;

		} if (node.left  == null) {
			return +node.height;

		} else {
			AVLTreeNode<E> left  = (AVLTreeNode<E>)node.left;
			AVLTreeNode<E> right = (AVLTreeNode<E>)node.right;

			return right.height - left.height;
		}
	}

    /** Rotation when a left-leaning tree has a left-leaning left child. */
	private void balanceLL(TreeNode<E> A, TreeNode<E> parent) {
		TreeNode<E> B = A.left;

		if (A == root) {
			root = B;

		} else {
			if (parent.left == A) {
				parent.left = B;
			} else {
				parent.right = B;
			}
		}

		A.left = B.right;
		B.right = A;

		updateSize((AVLTreeNode<E>)A);
		updateSize((AVLTreeNode<E>)B);

		updateHeight((AVLTreeNode<E>)A);
		updateHeight((AVLTreeNode<E>)B);
	}

    /** Rotation when a left-leaning tree has a right-leaning left child. */
	private void balanceLR(TreeNode<E> A, TreeNode<E> parent) {
		TreeNode<E> B = A.left;
		TreeNode<E> C = B.right;

		if (A == root) {
			root = C;

		} else {
			if (parent.left == A) {
				parent.left = C;
			} else {
				parent.right = C;
			}
		}

		A.left = C.right;
		B.right = C.left;
		C.left = B;
		C.right = A;

		updateSize((AVLTreeNode<E>)A);
		updateSize((AVLTreeNode<E>)B);
		updateSize((AVLTreeNode<E>)C);

		updateHeight((AVLTreeNode<E>)A);
		updateHeight((AVLTreeNode<E>)B);
		updateHeight((AVLTreeNode<E>)C);
	}

    /** Rotation when a right-leaning tree has a right-leaning right child. */
	private void balanceRR(TreeNode<E> A, TreeNode<E> parent) {
		TreeNode<E> B = A.right;

		if (A == root) {
			root = B;

		} else {
			if (parent.left == A) {
				parent.left = B;
			} else {
				parent.right = B;
			}
		}

		A.right = B.left;
		B.left = A;

		updateSize((AVLTreeNode<E>)A);
		updateSize((AVLTreeNode<E>)B);

		updateHeight((AVLTreeNode<E>)A);
		updateHeight((AVLTreeNode<E>)B);
	}

    /** Rotation when a right-leaning tree has a left-leaning right child. */
	private void balanceRL(TreeNode<E> A, TreeNode<E> parent) {
		TreeNode<E> B = A.right;
		TreeNode<E> C = B.left;

		if (A == root) {
			root = C;

		} else {
			if (parent.left == A) {
				parent.left = C;
			} else {
				parent.right = C;
			}
		}

		A.right = C.left;
		B.left = C.right;
		C.left = A;
		C.right = B;

		updateSize((AVLTreeNode<E>)A);
		updateSize((AVLTreeNode<E>)B);
		updateSize((AVLTreeNode<E>)C);

		updateHeight((AVLTreeNode<E>)A);
		updateHeight((AVLTreeNode<E>)B);
		updateHeight((AVLTreeNode<E>)C);
	}

    /** Extension of BST.TreeNode<E> that adds height and size attributes. */
	public static class AVLTreeNode<E> extends BST.TreeNode<E> {
		protected int height = 0;
		protected int size = 0;

		public AVLTreeNode(E e) {
			super(e);
		}
	}
}

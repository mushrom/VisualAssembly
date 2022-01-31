// *****************************************************************************
// Major Programming Assignment (Part 4)
// 
// *****************************************************************************

// based on the BST class from the book
import java.util.Comparator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;
import java.util.ListIterator;

public class BST<E extends Comparable<E>> implements Tree<E> {
	protected TreeNode<E> root;

	protected int size = 0;
	protected Comparator<E> c;

    /** Create a BST with a default ordered comparator. */
	public BST() {
		this.c = (a, b) -> a.compareTo(b);
	}

	/** Create a BST with a specified comparator */
	public BST(Comparator<E> c) {
		this.c = c;
	}

	public BST(E[] objects) {
		this.c = (e1, e2) -> ((Comparable<E>)e1).compareTo(e2);

		for (int i = 0; i < objects.length; i++) {
			add(objects[i]);
		}
	}

	/** Return true if the given element exists in the tree */
	@Override
	public boolean search(E e) {
		TreeNode<E> current = root;

		while (current != null) {
			if (c.compare(e, current.element) < 0) {
				current = current.left;

			} else if (c.compare(e, current.element) > 0) {
				current = current.right;

			} else {
				return true;
			}
		}

		return false;
	}

	/** Insert an element e into the tree
	 *
	 *  @param e element to insert
	 *  @return true if successfully inserted
	 */
	@Override
	public boolean insert(E e) {
		if (root == null) {
			root = createNewNode(e);
			size++;
			return true;

		} else {
			TreeNode<E> parent  = null;
			TreeNode<E> current = root;

			while (current != null) {
				if (c.compare(e, current.element) < 0) {
					parent = current;
					current = current.left;
				}
				
				else if (c.compare(e, current.element) > 0) {
					parent = current;
					current = current.right;
				}

				else {
					// don't overwrite when there's duplicates
					return false;
				}
			}

			if (c.compare(e, parent.element) < 0) {
				parent.left = createNewNode(e);
			} else {
				parent.right = createNewNode(e);
			}

			size++;
			return true;
		}
	}

	public TreeNode<E> createNewNode(E e) {
		return new TreeNode<E>(e);
	}

	/** Print with tree with inorder traversal from the root */
	@Override
	public void inorder() {
		inorder(root);
		System.out.println();
	}

    /** Print a representation of the tree to the system output. */
	public void dump() {
		dump(root, 0);
	}

    /** Recursive helper for dump(). */
	public void dump(TreeNode<E> node, int level) {
		if (node == null) {
			return;
		}

		dump(node.left, level + 1);

		for (int i = 0; i < level; i++) {
			System.out.print("   |");
		}
		System.out.println("---+ " + node.element);

		dump(node.right, level + 1);
	}

	/** Print with tree with postorder traversal from the root */
	@Override
	public void postorder() {
		System.out.println();
		System.out.print("     Recursive: ");
		postorderRecursive(root);
		System.out.println();
		System.out.print("     Iterative: ");
		postorderIterative(root);
		System.out.println();
	}

	/** Print with tree with preorder traversal from the root */
	@Override
	public void preorder() {
		System.out.println();
		System.out.print("     Recursive: ");
		preorderRecursive(root);
		System.out.println();
		System.out.print("     Iterative: ");
		preorderIterative(root);
		System.out.println();
	}

	protected void inorder(TreeNode<E> root) {
		if (root == null) return;

		inorder(root.left);
		System.out.print(root.element + " ");
		inorder(root.right);
	}

	protected void postorderRecursive(TreeNode<E> root) {
		if (root == null) return;

		postorderRecursive(root.left);
		postorderRecursive(root.right);
		System.out.print(root.element + " ");
	}

	// Non-recursive preorder traversal for exercise 25.5
	protected void postorderIterative(TreeNode<E> root) {
		if (root == null) return;

		TreeNode<E> cur = root;
		Stack<TreeNode<E>> traverseStack = new Stack<TreeNode<E>>();

		/*
		// my initial attempt, output stack has a consistent O(n) memory complexity
		// which is less than ideal...
		Stack<TreeNode<E>> outputStack   = new Stack<TreeNode<E>>();

		while (cur != null) {
			if (cur.left != null)  traverseStack.push(cur.left);
			if (cur.right != null) traverseStack.push(cur.right);

			outputStack.push(cur);
			cur = traverseStack.isEmpty()? null : traverseStack.pop();
		}

		while (!outputStack.empty()) {
			System.out.print(outputStack.pop().element + " ");
		}
		*/

		// one-stack algorithm
		// t. https://www.geeksforgeeks.org/iterative-postorder-traversal-using-stack/
		//
		// this should have an average O(log n) to O(n) memory complexity depending on
		// how balanced the tree is, much better
		do {
			// traverse leftwards down the tree adding the nodes right children
			// and the nodes themselves to the traversal stack
			while (cur != null) {
				if (cur.right != null) {
					traverseStack.push(cur.right);
				}

				traverseStack.push(cur);
				cur = cur.left;
			}

			// now at the leftmost lowest unprinted node
			cur = traverseStack.pop();

			// if this node has a right child on top of the stack, swap the
			// current node with it and repeat the above loop
			if (!traverseStack.isEmpty() && traverseStack.peek() == cur.right) {
				TreeNode<E> temp = traverseStack.pop();
				traverseStack.push(cur);
				cur = temp;

			// otherwise print the node
			} else {
				System.out.print(cur.element + " ");
				cur = null;
			}
		} while (!traverseStack.isEmpty());
	}

	// Non-recursive preorder traversal for exercise 25.4
	protected void preorderIterative(TreeNode<E> root) {
		if (root == null) return;

		Stack<TreeNode<E>> traverseStack = new Stack<TreeNode<E>>();
		TreeNode<E> cur = root;

		// this should have O(log n) to O(n) memory complexity
		// depending on how balanced the tree is
		while (cur != null) {
			System.out.print(cur.element + " ");

			if (cur.right != null) traverseStack.push(cur.right);
			if (cur.left != null)  traverseStack.push(cur.left);

			cur = traverseStack.isEmpty()? null : traverseStack.pop();
		}
	}

	// left here for comparison
	protected void preorderRecursive(TreeNode<E> root) {
		if (root == null) return;

		System.out.print(root.element + " ");
		preorderRecursive(root.left);
		preorderRecursive(root.right);
	}

	public static class TreeNode<E> {
		protected E element;
		protected TreeNode<E> left;
		protected TreeNode<E> right;

		public TreeNode(E e) {
			element = e;
		}

		int compare(E a, E b) {
			return 0;
		}
	}

	/** Return the number of elements in the tree
	 *
	 *  @return the number of elements in the tree.
	 **/
	@Override
	public int getSize() {
		return size;
	}

    /** Return the root node of the tree */
	public TreeNode<E> getRoot() {
		return root;
	}

    /** Return a path of parent nodes leading to (and including)
     *  the node for the given element e
     */
	public ArrayList<TreeNode<E>> path(E e) {
		ArrayList<TreeNode<E>> list = new ArrayList<TreeNode<E>>();
		TreeNode<E> current = root;

		while (current != null) {
			list.add(current);

			if (c.compare(e, current.element) < 0) {
				current = current.left;
			} else if (c.compare(e, current.element) > 0) {
				current = current.right;
			} else {
				break;
			}
		}

		return list;
	}

	/** Delete the given element from the tree
	 *
	 *  @param  e element to delete
	 *  @return tree if successfully deleted
	 **/
	@Override
	public boolean delete(E e) {
		TreeNode<E> parent = null;
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

			} else {
				if (c.compare(e, parent.element) < 0) {
					parent.left = current.right;
				} else {
					parent.right = current.right;
				}
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
		}

		size--;
		return true;
	}

    /** Return an iterator that traverses elements in the tree in order. */
	@Override
	public Iterator<E> iterator() {
		return new InorderIterator();
	}

    /** Return an iterator that traverses elements in the tree in order. */
	public ListIterator<E> listIterator() {
		return new InorderIterator();
	}

	private class InorderIterator implements ListIterator<E> {
		private ArrayList<E> list = new ArrayList<>();
		private int current = 0;
		private int lastIndex = 0;

		public InorderIterator() {
			inorder();
		}

		private void inorder() {
			inorder(root);
		}

		private void inorder(TreeNode<E> root) {
			if (root == null) return;
			inorder(root.left);
			list.add(root.element);
			inorder(root.right);
		}

		/** Unsupported operation for InorderIterator.
		 *
		 *  Throws UnsupportedOperationException.
		 */
		@Override
		public void add(E e) {
			throw new UnsupportedOperationException();
		}

		/** Return true if there is an element available for the next
		 *  call to next()
		 */
		@Override
		public boolean hasNext() {
			return current < list.size();
		}

		/** Return true if there is an element available for the next
		 *  call to previous
		 */
		@Override
		public boolean hasPrevious() {
			return current > 0;
		}

		/** Get the next element from iterator and advance the iterator position. */
		@Override
		public E next() {
			lastIndex = current;
			return list.get(current++);
		}

		/** Get the previous element from iterator and decrement
		 *  the iterator position.
		 */
		@Override
		public E previous() {
			lastIndex = current - 1;
			return list.get(--current);
		}

		/** Get the index of the previous element in the list */
		@Override
		public int previousIndex() {
			return current - 1;
		}

		/** Get the index of the next element in the list */
		@Override
		public int nextIndex() {
			return current;
		}

		/** Remove the element from the last call to previous()/next(),
		 *  preserving the iterator position in the list
		 */
		@Override
		public void remove() {
			if (lastIndex < 0) {
				throw new IllegalStateException();
			}

			delete(list.get(lastIndex));
			current--;
			list.clear();
			inorder();
		}

		/** Unsupported operation for InorderIterator.
		 *
		 *  Throws UnsupportedOperationException.
		 */
		@Override
		public void set(E e) {
			throw new UnsupportedOperationException();
		}
	}

	/** Remove all entries in the tree */
	@Override
	public void clear() {
		root = null;
		size = 0;
	}
}

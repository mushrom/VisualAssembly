// *****************************************************************************
// Major Programming Assignment (Part 4)
// 
// *****************************************************************************

// based on the Tree interface from the book
import java.util.Collection;
import java.util.Iterator;
import java.lang.reflect.Array;

public interface Tree<E> extends Collection<E> {
	/** Return true if the given element exists in the tree */
	public boolean search(E e);

	/** Insert an element e into the tree
	 *
	 *  @param e element to insert
	 *  @return true if successfully inserted
	 */
	public boolean insert(E e);

	/** Delete the given element from the tree
	 *
	 *  @param  e element to delete
	 *  @return tree if successfully deleted
	 **/
	public boolean delete(E e);

	/** Return the number of elements in the tree
	 *
	 *  @return the number of elements in the tree.
	 **/
	public int getSize();

	/** Print with tree with inorder traversal from the root */
	public default void inorder() {}

	/** Print with tree with postorder traversal from the root */
	public default void postorder() {}

	/** Print with tree with preorder traversal from the root */
	public default void preorder() {}

	/** Return tree if the tree is empty */
	@Override
	public default boolean isEmpty() {
		return size() == 0;
	}

	/** Return true if the given element exists in the tree. */
	@Override
	public default boolean contains(Object e) {
		return search((E)e);
	}

    /** Add the element e to the tree.
     *
     *  @return true if the element was removed, false if the element
     *          already exists in the tree.
     */
	@Override
	public default boolean add(E e) {
		return insert(e);
	}

    /** Remove the object e from the tree, if it exists in the tree.
     *
     *  @return true if the object was removed, false if no changes were made.
     */
	@Override
	public default boolean remove(Object e) {
		return delete((E)e);
	}

    /** Returns the number of elements in the tree. */
	@Override
	public default int size() {
		// Why does this implement two methods for retrieving the size?
		return getSize();
	}

    /*
	@Override
	public default boolean containsAll(Collection<?> c) {
		// left as an exercise
		return false;
	}

	@Override
	public default boolean addAll(Collection<? extends E> c) {
		// left as an exercise
		return false;
	}

	@Override
	public default boolean removeAll(Collection<?> c) {
		// left as an exercise
		return false;
	}

	@Override
	public default boolean retainAll(Collection<?> c) {
		// left as an exercise
		return false;
	}

	@Override
	public default Object[] toArray() {
		// left as an exercise
		return null;
	}

	@Override
	public default <T> T[] toArray(T[] array) {
		// left as an exercise
		return null;
	}
    */

	/** Returns true if the tree contains all of the elements in the given collection. */
	@Override
	public default boolean containsAll(Collection<?> c) {
		for (Object thing : c) {
			if (!this.contains(thing)) {
				return false;
			}
		}

		return true;
	}

	/** Inserts all of the elements in the given collection into the tree. */
	@Override
	public default boolean addAll(Collection<? extends E> c) {
		for (E thing : c) {
			this.add(thing);
		}

		return true;
	}

	/** Removes all of the elements in the given collection from the tree.
	 *
	 *  @return true if any elements were removed, or false if no elements were removed.
	 */
	@Override
	public default boolean removeAll(Collection<?> c) {
		boolean ret = false;

		for (Object thing : c) {
			if (!this.contains(thing)) {
				this.remove(thing);
				ret = true;
			}
		}

		return ret;
	}

	/** Removes all elements from the tree except those which are also in
	 *  the given collection.
	 *
	 *  @return true if any elements were removed, or false if no elements were removed.
	 */
	@Override
	public default boolean retainAll(Collection<?> c) {
		boolean ret = false;

		for (E thing : this) {
			if (!c.contains(thing)) {
				this.remove(thing);
				ret = true;
			}
		}

		return ret;
	}

	/** Returns an array containing all of the elements of the tree.
	 *
	 *  This version of the function does not retain the generic type,
	 *  see toArray(T[] array).
	 */
	@Override
	public default Object[] toArray() {
		Object[] ret = new Object[this.size()];
        Iterator<E> it = this.iterator();

		for (int i = 0; it.hasNext() && i < this.size(); i++) {
            ret[i] = it.next();
		}

		return ret;
	}

	/** Returns an array containing all of the elements of the tree.
	 *
	 *  This overload casts all elements to the type T given as T[] array.
	 *  This would normally be used as something like: 
	 *
	 *      String[] thing = thetree.toArray(new String[0]);
	 */
	@Override
	public default <T> T[] toArray(T[] array) {
		T[] ret = (T[])Array.newInstance(array.getClass().getComponentType(), this.size());
        Iterator<E> it = this.iterator();

		for (int i = 0; it.hasNext() && i < this.size(); i++) {
            ret[i] = (T)it.next();
		}

		return ret;
	}

}

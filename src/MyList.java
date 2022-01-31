// *****************************************************************************
// Major Programming Assignment (Part 4)
// 
// *****************************************************************************

import java.util.Collection;
import java.lang.reflect.Array;

// partially based on the listing from the book
public interface MyList<E> extends Collection<E> {
	public void add(int index, E e);
	public E get(int index);
	public int indexOf(Object e);
	public int lastIndexOf(Object e);
	public E remove(int index);
	public E set(int index, E e);

	@Override
	public default boolean add(E e) {
		add(size(), e);
		return true;
	}

	@Override
	public default boolean isEmpty() {
		return size() == 0;
	}

	@Override
	public default boolean remove(Object e) {
		if (indexOf(e) >= 0) {
			return true;

		} else {
			return false;
		}
	}

	@Override
	public default boolean containsAll(Collection<?> c) {
		for (Object thing : c) {
			if (this.indexOf(thing) < 0) {
				return false;
			}
		}

		return true;
	}

	@Override
	public default boolean addAll(Collection<? extends E> c) {
		for (E thing : c) {
			this.add(thing);
		}

		return true;
	}

	@Override
	public default boolean removeAll(Collection<?> c) {
		boolean ret = false;

		for (Object thing : c) {
			if (this.indexOf(thing) >= 0) {
				while (this.remove(thing));
				ret = true;
			}
		}

		return ret;
	}

	@Override
	public default boolean retainAll(Collection<?> c) {
		boolean ret = false;

		for (int i = 0; i < this.size(); i++) {
			E thing = this.get(i);

			if (!c.contains(thing)) {
				while (this.remove(thing));
				ret = true;
				// avoid incrementing if the current element was removed

			} else {
				i++;
			}
		}

		return ret;
	}

	@Override
	public default Object[] toArray() {
		Object[] ret = new Object[this.size()];

		for (int i = 0; i < this.size(); i++) {
			ret[i] = this.get(i);
		}

		return ret;
	}

	@Override
	public default <T> T[] toArray(T[] array) {
		T[] ret = (T[])Array.newInstance(array.getClass().getComponentType(), this.size());

		for (int i = 0; i < this.size(); i++) {
			ret[i] = (T)this.get(i);
		}

		return ret;
	}
}

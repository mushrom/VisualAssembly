// *****************************************************************************
// Major Programming Assignment (Part 4)
// 
// *****************************************************************************

// partially based on the listing from the book
public class MyArrayList<E> implements MyList<E> {
	public static final int INITIAL_CAPACITY = 16;
	private E[] data = (E[])new Object[INITIAL_CAPACITY];
	private int size = 0;

	public MyArrayList() {
	}

	public MyArrayList(E[] objects) {
		for (E obj : objects) {
			add(obj);
		}
	}

	@Override
	public void add(int index, E e) {
		if (index < 0 || index > size) {
			throw new IndexOutOfBoundsException(
				"Index: " + index + " is out of bounds "
				+ "for MyArrayList of size " + size);
		}

		ensureCapacity();

		for (int i = size; i > index; i--) {
			data[i] = data[i - 1];
		}

		data[index] = e;
		size++;
	}

	private void ensureCapacity() {
		if (size >= data.length) {
			E[] newData = (E[])(new Object[size*2 + 1]);
			System.arraycopy(data, 0, newData, 0, size);
			data = newData;
		}
	}

	@Override
	public void clear() {
		data = (E[])new Object[INITIAL_CAPACITY];
		size = 0;
	}

	@Override
	public boolean contains(Object e) {
		for (int i = 0; i < size; i++) {
			if (e.equals(data[i])) {
				return true;
			}
		}

		return false;
	}

	@Override
	public E get(int index) {
		checkIndex(index);
		return data[index];
	}

	private void checkIndex(int index) {
		if (index < 0 || index >= size) {
			throw new IndexOutOfBoundsException(
				"Index: " + index + " is out of bounds "
				+ "for MyArrayList of size " + size);
		}
	}

	@Override
	public int indexOf(Object e) {
		for (int i = 0; i < size; i++) {
			if (e.equals(data[i])) {
				return i;
			}
		}

		return -1;
	}

	@Override
	public int lastIndexOf(Object e) {
		for (int i = size - 1; i >= 0; i--) {
			if (e.equals(data[i])) {
				return i;
			}
		}

		return -1;
	}

	@Override
	public E remove(int index) {
		checkIndex(index);

		E ret = data[index];

		for (int k = index; k < size - 1; k++) {
			data[k] = data[k + 1];
		}

		data[size - 1] = null;
		size--;

		return ret;
	}

	@Override
	public E set(int index, E e) {
		checkIndex(index);
		E old = data[index];
		data[index] = e;

		return old;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder("[");

		for (int i = 0; i < size; i++) {
			result.append(data[i]);

			if (i < size - 1) {
				result.append(", ");
			}
		}

		result.append("]");
		return result.toString();
	}

	public void trimToSize() {
		if (size != data.length) {
			E[] newData = (E[])(new Object[size]);
			System.arraycopy(data, 0, newData, 0, size);
			data = newData;
		}
	}

	@Override
	public java.util.Iterator<E> iterator() {
		return new ArrayListIterator();
	}

	private class ArrayListIterator implements java.util.Iterator<E> {
		private int current = 0;

		@Override
		public boolean hasNext() {
			// just now realizing you can easily access the parent class of
			// subclasses in java, the equivalent of this in C++ is a big pain, nice
			return current < size;
		}

		@Override
		public E next() {
			// TODO: this should throw an exception no?
			return data[current++];
		}

		@Override
		public void remove() {
			if (current == 0) {
				throw new IllegalStateException();
			}

			MyArrayList.this.remove(--current);
		}
	}

	@Override
	public int size() {
		return size;
	}
}

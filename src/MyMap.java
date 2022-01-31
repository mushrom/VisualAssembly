// *****************************************************************************
// Major Programming Assignment (Part 4)
// 
// *****************************************************************************

import java.util.Set;

// based on MyMap.java from the book
public interface MyMap<K, V> {
	/** Remove all entries from this map. */
	public void clear();

	/** Return true if this map contains the given key. */
	public boolean containsKey(K key);

	/** return true if this map contains the given value. */
	public boolean containsValue(V value);

	/** Return a set containing all of the entries in the map. */
	public Set<Entry<K, V>> entrySet();

	/** Return the value corresponding to the given key, or null if the
	 *  key doesn't exist in the map.
	 */
	public V get(K key);

	/** Insert a value into the map, either overwriting an existing entry
	 *  if the key is in the map, or extending the map if not.
	 */
	public V put(K key, V value);

	/** Return true if size is equal to zero. */
	public boolean isEmpty();

	/** Return a set containing all keys in the map. */
	public Set<K> keySet();

	/** Remove a key from the map, if it exists in the map. */
	public void remove(K key);

	/** Returns the number of entries in the map. */
	public int size();

	/** Returns a set containing all of the values in the map. */
	public Set<V> values();

	public static class Entry<K, V> {
		K key;
		V value;
		// used in open addressing, marks that this
		// entry was deleted and can be overwritten, and
		// shouldn't be copied when rehashing
		boolean marked;

		public Entry(K key, V value) {
			this.key   = key;
			this.value = value;
			this.marked = false;
		}

		public K getKey() {
			return key;
		}

		public V getValue() {
			return value;
		}

		@Override
		public String toString() {
			return "(" + key.toString() + ": " + value.toString() + ")";
		}
	}
}

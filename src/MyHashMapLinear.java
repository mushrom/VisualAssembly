// *****************************************************************************
// Major Programming Assignment (Part 4)
// 
// *****************************************************************************

// based on MyHashMap.java from the book,
//
// implements a hash map using open addressing with linear probing,
// as part of exercise 27.1
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

public class MyHashMapLinear<K, V> implements MyMap<K, V> {
	private static int DEFAULT_INITIAL_CAPACITY = 4;
	private static int MAXIMUM_CAPACITY = 1 << 30;

	private int capacity;
	// default load factor for linear probing is 0.5
	private static float DEFAULT_MAX_LOAD_FACTOR = 0.5f;
	private float loadFactorThreshold;
	private int size = 0;

	ArrayList<MyMap.Entry<K, V>> entries;

	public MyHashMapLinear() {
		this(DEFAULT_INITIAL_CAPACITY, DEFAULT_MAX_LOAD_FACTOR);
	}

	public MyHashMapLinear(int initialCapacity) {
		this(initialCapacity, DEFAULT_MAX_LOAD_FACTOR);
	}

	public MyHashMapLinear(int initialCapacity, float loadFactorThreshold) {
		this.capacity = Math.min(MAXIMUM_CAPACITY, trimToPowerOf2(initialCapacity));
		this.loadFactorThreshold = loadFactorThreshold;
		this.entries = new ArrayList<>(capacity);

		for (int i = 0; i < capacity; i++) {
			this.entries.add(null);
		}
	}

	/** Returns true if the entry is filled and not marked for deletion. */
	private boolean validEntry(Entry<K, V> entry) {
		return entry != null && entry.marked == false;
	}

	/** Remove all entries from this map. */
	@Override
	public void clear() {
		size = 0;
		removeEntries();
	}

	/** Return true if this map contains the given key. */
	@Override
	public boolean containsKey(K key) {
		return get(key) != null;
	}

	/** return true if this map contains the given value. */
	@Override
	public boolean containsValue(V value) {
		for (Entry<K, V> entry : entries) {
			if (validEntry(entry)) {
				if (entry.getValue().equals(value)) {
					return true;
				}
			}
		}

		return false;
	}

	/** Return a set containing all of the entries in the map. */
	@Override
	public Set<Entry<K,V>> entrySet() {
		Set<Entry<K,V>> ret = new HashSet<>();

		for (Entry<K, V> entry : entries) {
			if (validEntry(entry)) {
				ret.add(entry);
			}
		}

		return ret;
	}
	
	/** Return the value corresponding to the given key, or null if the
	 *  key doesn't exist in the map.
	 */
	@Override
	public V get(K key) {
		int bucketIdx = hash(key.hashCode());

		for (int i = bucketIdx; i != modCapacity(bucketIdx - 1); i = modCapacity(i + 1)) {
			Entry<K,V> entry = entries.get(i);

			if (entry == null) {
				return null;

			} else if (!entry.marked && entry.getKey().equals(key)) {
				return entry.getValue();
			}
		}

		return null;
	}

	/** Return true if size is equal to zero. */
	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	/** Insert a value into the map, either overwriting an existing entry
	 *  if the key is in the map, or extending the map if not.
	 */
	@Override
	public V put(K key, V value) {
		// the original code from the book is broken for larger load factor
		// thresholds, needs to be size+1 otherwise it won't reallocate in time
		// for factors like 0.75, 1.0, and will simply run out of map space
		if (size + 1 >= capacity * loadFactorThreshold) {
			if (capacity >= MAXIMUM_CAPACITY) {
				throw new RuntimeException("Exceeded maximum capacity");
			}

			rehash();
		}

		int bucketIdx = hash(key.hashCode());

		for (int i = bucketIdx; i != modCapacity(bucketIdx - 1); i = modCapacity(i + 1)) {
			Entry<K,V> entry = entries.get(i);

			if (!validEntry(entry)) {
				entries.set(i, new Entry(key, value));
				size++;
				return value;

			} else if (entry.getKey().equals(key)) {
				V oldValue = entry.value;
				entry.value = value;
				return oldValue;
			}
		}

		return null;
	}

	/** Remove a key from the map, if it exists in the map. */
	@Override
	public void remove(K key) {
		int bucketIdx = hash(key.hashCode());

		for (int i = bucketIdx; i != modCapacity(bucketIdx - 1); i = modCapacity(i + 1)) {
			Entry<K, V> entry = entries.get(i);

			if (entry == null) {
				break;

			} else if (!entry.marked && entry.getKey().equals(key)) {
				entry.marked = true;
				size--;
				break;
			}
		}
	}

	/** Returns the number of entries in the map. */
	@Override
	public int size() {
		return size;
	}

	/** Return a set containing all keys in the map. */
	@Override
	public Set<K> keySet() {
		Set<K> ret = new HashSet<>();

		for (Entry<K,V> entry : entries) {
			if (validEntry(entry)) {
				ret.add(entry.getKey());
			}
		}

		return ret;
	}

	/** Returns a set containing all of the values in the map. */
	@Override
	public Set<V> values() {
		Set<V> ret = new HashSet<>();

		for (Entry<K,V> entry : entries) {
			if (validEntry(entry)) {
				ret.add(entry.getValue());
			}
		}

		return ret;
	}

	/** Returns n % capacity, convenience function for modular arithmetic */
	private int modCapacity(int n) {
		return n & (capacity - 1);
	}

	/** Returns an index into the bucket list for the given hash code. */
	private int hash(int hashCode) {
		return supplementalHash(hashCode) & (capacity - 1);
	}

	/** Mixes the hash (more) to ensure even distribution */
	private static int supplementalHash(int h) {
		h ^= (h >>> 20) ^ (h >>> 12);

		return h ^ (h >>> 7) ^ (h >>> 4);
	}

	/** Rounds the requested capacity up to a power of two. */
	private static int trimToPowerOf2(int initialCapacity) {
		int capacity = 1;

		while (capacity < initialCapacity) {
			capacity <<= 1;
		}

		return capacity;
	}

	/** Clears all entries from the map. */
	private void removeEntries() {
		entries.clear();
		entries.ensureCapacity(capacity);
	}

	/** Resizes the bucket list and recalculates bucket indices for entries. */
	private void rehash() {
		Set<Entry<K, V>> set = entrySet();
		capacity <<= 1;
		size = 0;

		entries.clear();
		entries.ensureCapacity(capacity);
		for (int i = 0; i < capacity; i++) {
			this.entries.add(null);
		}

		for (Entry<K, V> entry : set) {
			put(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public String toString() {
		Set<Entry<K, V>> set = entrySet();
		String ret = "[";
		
		for (Entry<K,V> entry : set) {
			ret += entry.toString() + ", ";
		}

		ret += "]";

		return ret;
	}
}


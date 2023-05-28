package hashmap;

import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author om
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    private int numOfBuckets = 16;
    private int numOfElements;
    private double loadFactor = 0.75;
    private int resizeFactor = 2;


    /** Constructors */
    public MyHashMap() {
        buckets = new Collection[numOfBuckets];
        numOfElements = 0;
    }

    public MyHashMap(int initialSize) {
        buckets = new Collection[initialSize];
        numOfElements = 0;
    }


    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        buckets = new Collection[initialSize];
        loadFactor = maxLoad;
        numOfElements = 0;
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     *
     * NOTE: This method is not used as the node is created
     * via the new operator.
     */
    private Node createNode(K key, V value) {
        return null;
    }


    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new LinkedList<>();
    }


    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     *
     * NOTE: This method is not used as the backing array of the
     * hash table is done via the new operator.
     */
    private Collection<Node>[] createTable(int tableSize) {
        return null;
    }

    /** Uses geometric resizing to resize the hash table. */
    private void resize() {
        Collection<Node>[] temp = new Collection[numOfBuckets * resizeFactor];
        System.arraycopy(buckets, 0, temp, 0, numOfBuckets);

        buckets = temp;

        numOfBuckets = numOfBuckets * resizeFactor;
    }


    /** Returns the respective index in the hash table for the given key.*/
    private int getIndex(K key) {
        int hash = key.hashCode();
        return Math.floorMod(hash, numOfBuckets);
    }


    /** Clears all the elements in the hash table. */
    public void clear() {
        buckets = new Collection[numOfBuckets];
        numOfElements = 0;
    }


    /** Returns true if this map contains a mapping for the specified key. */
    public boolean containsKey(K key) {
        int index = getIndex(key);

        if (buckets[index] == null) {
            return false;
        }

        for (Node k : buckets[index]) {
            if (k.key.equals(key)) {
                return true;
            }
        }
        return false;
    }


    /**
     * Returns the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key.
     */
    public V get(K key) {
        int index = getIndex(key);

        if (!containsKey(key)) {
            return null;
        }

        for (Node k : buckets[index]) {
            if (k.key.equals(key)) {
                return k.value;
            }
        }
        return null;
    }


    /** Returns the number of key-value mappings in this map. */
    public int size() {
        return numOfElements;
    }


    /**
     * Associates the specified value with the specified key in this map.
     * If the map previously contained a mapping for the key,
     * the old value is replaced.
     */
    public void put(K key, V value) {
        float currentLoad = (float) numOfElements / numOfBuckets;
        if (currentLoad >= loadFactor) {
            resize();
        }

        int index = getIndex(key);

        if (buckets[index] == null) {
            buckets[index] = createBucket();

            Node n = new Node(key, value);

            buckets[index].add(n);

            numOfElements++;

        } else if (containsKey(key)) {
            for (Node k : buckets[index]) {
                if (k.key.equals(key)) {
                    k.value = value;
                }
            }

        } else {
            Node n = new Node(key, value);

            buckets[index].add(n);

            numOfElements++;
        }
    }


    /** Returns a Set view of the keys contained in this map. */
    public Set<K> keySet() {
        Set<K> setView = new HashSet<>();
        for (int i = 0; i < numOfBuckets; i++) {
            if (buckets[i] == null) {
                continue;
            }

            for (Node n : buckets[i]) {
                setView.add(n.key);
            }
        }
        return setView;
    }


    /**
     * Removes the mapping for the specified key from this map if present.
     * Not required for Lab 8. If you don't implement this, throw an
     * UnsupportedOperationException.
     */
    public V remove(K key) {
        throw new UnsupportedOperationException("Not required to be implemented for Lab 8.");
    }


    /**
     * Removes the entry for the specified key only if it is currently mapped to
     * the specified value. Not required for Lab 8. If you don't implement this,
     * throw an UnsupportedOperationException.
     */
    public V remove(K key, V value) {
        throw new UnsupportedOperationException("Not required to be implemented for Lab 8.");
    }

    public Iterator<K> iterator() {
        throw new UnsupportedOperationException("Not required to be implemented for Lab 8.");
    }
}

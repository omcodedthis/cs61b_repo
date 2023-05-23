package bstmap;


import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable, V> implements Map61B<K, V>, Iterable<K> {

    private class BSTNode<K, V> {
        K key;
        V value;
        BSTNode left;
        BSTNode right;

        public BSTNode(K k, V val) {
            key = k;
            value = val;
        }

        public void setLowerChild(BSTNode node) {
            left = node;
        }

        public void setGreaterChild(BSTNode node) {
            right = node;
        }
    }

    private int size;

    BSTNode root;

    public BSTMap() {
        size = 0;
    }

    /** Removes all of the mappings from this map. */
    @Override
    public void clear() {
        root = null;
        size = 0;
    }


    /* Returns true if this map contains a mapping for the specified key. */
    @Override
    public boolean containsKey(K key) {
        BSTNode ptr = root;

        return containsKeyActual(ptr, key);
    }


    public boolean containsKeyActual(BSTNode ptr, K key) {
        if (ptr == null) {
            return false;
        }

        if (key.equals(ptr.key)) {
            return true;
        }

        int compareResult = key.compareTo(ptr.key);

        if (compareResult < 0) {
            return containsKeyActual(ptr.left, key);
        } else if (compareResult > 0) {
            return containsKeyActual(ptr.right, key);
        }

        return false;
    }


    /* Returns the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key.
     */
    @Override
    public V get(K key) {
        BSTNode ptr = root;

        return getActual(ptr, key);
    }


    private V getActual(BSTNode ptr, K key) {
        if (ptr == null) {
            return null;
        }

        if (key.equals(ptr.key)) {
            return (V) ptr.value;
        }

        int compareResult = key.compareTo(ptr.key);

        if (compareResult < 0) {
            return (V) getActual(ptr.left, key);
        } else if (compareResult > 0) {
            return (V) getActual(ptr.right, key);
        }

        return null;
    }

    /* Returns the number of key-value mappings in this map. */
    @Override
    public int size() {
        return size;
    }


    /* Associates the specified value with the specified key in this map. */
    @Override
    public void put(K key, V value) {
        if (size == 0) {
            root = new BSTNode(key, value);
            size = 1;
        }

        BSTNode ptr = root;

        putActual(ptr, key, value);
    }


    private BSTNode putActual(BSTNode ptr, K key, V value) {
        if (ptr == null) {
            size++;
            return new BSTNode(key, value);
        }

        int compareResult = key.compareTo(ptr.key);

        if (compareResult < 0) {
            ptr.left = putActual(ptr.left, key, value);
        } else if (compareResult > 0) {
            ptr.right = putActual(ptr.right, key, value);
        }

        return ptr;
    }


    /* Returns a Set view of the keys contained in this map. Not required for Lab 7.
     * If you don't implement this, throw an UnsupportedOperationException. */
    @Override
    public Set<K> keySet() {
        throw new UnsupportedOperationException("Not required to be implemented for Lab 7.");
    }


    /* Removes the mapping for the specified key from this map if present.
     * Not required for Lab 7. If you don't implement this, throw an
     * UnsupportedOperationException. */
    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException("Not required to be implemented for Lab 7.");
    }


    /* Removes the entry for the specified key only if it is currently mapped to
     * the specified value. Not required for Lab 7. If you don't implement this,
     * throw an UnsupportedOperationException.*/
    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException("Not required to be implemented for Lab 7.");
    }

    @Override
    public Iterator<K> iterator() {
        throw new UnsupportedOperationException("Not required to be implemented for Lab 7.");
    }
}

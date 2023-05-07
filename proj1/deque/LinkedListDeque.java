package deque;

import java.util.Iterator;

public class LinkedListDeque<T> implements Iterable<T>, Deque<T> {
    /** nested class for a single node. */
    private static class Node<T> {
        T item;
        Node prev;
        Node next;

        Node(T i, Node p, Node n) {
            item = i;
            prev = p;
            next = n;
        }
    }

    /** variables that keep track of the sentinel node & current size of the LinkedListDeque. */
    private Node<T> sentinel;
    private int size;

    /** constructor to either just instantiate the LinkedListDeque or instantiate the LinkedListDeque with an item. */
    public LinkedListDeque() {
        sentinel = new Node<>(null, null, null);
        sentinel.prev = sentinel;
        sentinel.next = sentinel;
        size = 0;
    }

    public LinkedListDeque(T x) {
        sentinel = new Node<>(null, sentinel, sentinel);
        sentinel.next = new Node<>(x, sentinel, sentinel);
        sentinel.prev = sentinel.next;
        size = 1;
    }

    /** Adds an item of type T to the front of the deque. */
    public void addFirst(T item) {
        Node<T> tmp = new Node(item, sentinel, sentinel.next);
        sentinel.next.prev = tmp;
        sentinel.next = tmp;

        size += 1;
    }

    /** Adds an item of type T to the back of the deque. */
    public void addLast(T item) {
        Node<T> tmp = new Node(item, sentinel.prev, sentinel);
        sentinel.prev.next = tmp;
        sentinel.prev = tmp;

        size += 1;
    }

    /** Returns the number of items in the deque. */
    public int size() {
        return size;
    }

    /**
     * Prints the items in the deque from first to last,
     * separated by a space. Once all the items have been
     * printed, print out a new line. */
    public void printDeque() {
        Node<T> tmp = sentinel.next;

        while (tmp != sentinel) {
            System.out.print(tmp.item + " ");
            tmp = tmp.next;
        }
        System.out.println();
    }

    /** Removes and returns the item at the front of the deque. If no such item exists, returns null. */
    public T removeFirst() {
        if (size > 0) {
            Node<T> first = sentinel.next;

            sentinel.next = sentinel.next.next;
            sentinel.next.prev = sentinel;

            size -= 1;

            return first.item;
        } else {
            return null;
        }
    }

    /** Removes and returns the item at the back of the deque. If no such item exists, returns null. */
    public T removeLast() {
        if (size > 0) {
            Node<T> last = sentinel.prev;

            sentinel.prev = sentinel.prev.prev;
            sentinel.prev.next = sentinel;

            size -= 1;

            return last.item;
        } else {
            return null;
        }
    }

    /** Gets the item at the given index, where 0 is the
     * front, 1 is the next item, and so forth. If no such
     * item exists, returns null.  */
    public T get(int index) {
        Node<T> tmp = sentinel.next;

        for (int i = 0; i < index; i++) {
            tmp = tmp.next;
        }
        return tmp.item;
    }

    /** Same as get, but uses recursion. This method adds
     * another parameter, the first node,to getRecursiveActual()
     * which uses recursion. */
    public T getRecursive(int index) {
        return getRecursiveActual(index, sentinel.next);
    }

    private T getRecursiveActual(int index, Node p) {
        if (index == 0) {
            return (T) p.item;
        }

        return getRecursiveActual((index - 1), p.next);


    }

    /** Returns whether the parameter o is equal to the Deque.
     *  o is considered equal if it is a Deque and if it contains
     *  the same contents (as goverened by the generic T’s equals
     *  method) in the same order. */
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof LinkedListDeque otherList) {
            if (this.size != otherList.size) {
                return false;
            }

            for (int i = 0; i < this.size; i++) {
                T expected = this.get(i);
                T actual = (T) otherList.get(i);
                boolean equal = expected.equals(actual);

                if (!equal) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    /** Returns an iterator by instantiating the LinkedListIterator class. */
    @Override
    public Iterator<T> iterator() {
        return new LinkedListDequeIterator();
    }

    private class LinkedListDequeIterator implements Iterator<T> {
        private int wizPos;
        LinkedListDequeIterator() {
            wizPos = 0;
        }

        @Override
        public boolean hasNext() {
            return wizPos < size;
        }

        @Override
        public T next() {
            T returnItem = get(wizPos);
            wizPos++;
            return returnItem;
        }
    }
}

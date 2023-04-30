package deque;

public class ArrayDeque<T> {
    private T[] items;
    private int size;

    private int nextFirst = 4;

    private int nextLast = 5;

    /** constructor to either just instantiate the LinkedListDeque or instantiate the LinkedListDeque with an item. */
    public ArrayDeque() {
        items = (T[])new Object[8];
        size = 0;
    }

    public ArrayDeque(T x) {
        items = (T[])new Object[8];
        items[nextFirst] = x;
        nextFirst -= 1;
        size = 1;
    }

    /** Finds the appropriate index for nextFirst & nextLast. */
    public void moveNext(boolean isFirst) {
        if (isFirst) {
            moveFirst();
        }
        else {
            moveLast();
        }
    }

    /** Finds the appropriate location for nextFirst. */
    public void moveFirst() {
        int tmp = nextFirst - 1;
        for (int i = tmp; i >= 0; i--) {
            if (items[tmp] == null) {
                nextFirst = tmp;
                return;
            }
        }

        for (int j = items.length - 1; j > nextLast; j--) {
            tmp = j;
            if (items[tmp] == null) {
                nextFirst = tmp;
                return;
            }
        }
    }

    /** Finds the appropriate location for nextLast. */
    public void moveLast() {
        int tmp = nextLast + 1;
        for (int i = tmp; i < items.length; i++) {
            if (items[tmp] == null) {
                nextLast = tmp;
                return;
            }
        }

        for (int j = 0; j < nextFirst; j++) {
            tmp = j;
            if (items[tmp] == null) {
                nextLast = tmp;
                return;
            }
        }
    }

    /** Adds an item of type T to the front of the deque. */
    public void addFirst(T item) {
        items[nextFirst] = item;
        moveNext(true);
        size += 1;
    }

    /** Adds an item of type T to the back of the deque. */
    public void addLast(T item) {
        items[nextLast] = item;
        moveNext(false);
        size += 1;
    }

    /** Returns true if deque is empty, false otherwise. */
    public boolean isEmpty() {
        if (size == 0) {
            return true;
        }

        else {
            return false;
        }
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
        for (int i = 0; i < size; i++) {
            System.out.print(items[i] + " ");
        }
        System.out.println();
    }

    /** Removes and returns the item at the front of the deque. If no such item exists, returns null. */
    public T removeFirst() {
        if (size > 0) {
            T first = items[nextFirst + 1];
            items[nextFirst + 1] = null;

            moveNext(true);
            size -= 1;
            return first;
        }

        else {
            return null;
        }
    }

    /** Removes and returns the item at the back of the deque. If no such item exists, returns null. */
    public T removeLast() {
        if (size > 0) {
            T last = items[nextLast - 1];
            items[nextLast - 1] = null;

            moveNext(false);
            size -= 1;
            return last;
        }

        else {
            return null;
        }
    }

    /** Gets the item at the given index, where 0 is the
     * front, 1 is the next item, and so forth. If no such
     * item exists, returns null.  */
    public T get(int index) {
        return items[index];
    }

}

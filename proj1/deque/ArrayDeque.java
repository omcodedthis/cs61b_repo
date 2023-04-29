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

    /** Resizes the array to the desired capacity. */
    private void resize(int capacity) {
       
    }


    /** Adds an item of type T to the front of the deque. */
    public void addFirst(T item) {
        if (size == (items.length - 2)) {
            resize(size * 2);
        }
        items[nextFirst] = item;
        nextFirst -= 1;
        size += 1;
    }

    /** Adds an item of type T to the back of the deque. */
    public void addLast(T item) {
        if (size == (items.length - 2)) {
            resize(size * 2);
        }
        items[nextLast] = item;
        nextLast += 1;
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
        if (size == 0) {
            return null;
        }

        else {
            float R = ((float) (size) / (float) (items.length));
            if (R < 0.25) {
                resize(items.length / 2);
            }
            T first = items[nextFirst + 1];
            items[nextFirst + 1] = null;

            nextFirst += 1;
            size -= 1;
            return first;
        }
    }

    /** Removes and returns the item at the back of the deque. If no such item exists, returns null. */
    public T removeLast() {
        if (size == 0) {
            return null;
        }

        else {
            float R = ((float) (size) / (float) (items.length));
            if (R < 0.25) {
                resize(items.length / 2);
            }
            T last = items[nextLast - 1];
            items[nextLast - 1] = null;

            nextLast -= 1;
            size -= 1;
            return last;
        }
    }

    /** Gets the item at the given index, where 0 is the
     * front, 1 is the next item, and so forth. If no such
     * item exists, returns null.  */
    public T get(int index) {
        return items[index];
    }

}

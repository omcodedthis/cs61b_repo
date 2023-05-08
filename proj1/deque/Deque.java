package deque;

public interface Deque<T> {
    void addFirst(T item);
    void addLast(T item);
    int size();
    void printDeque();
    T removeFirst();
    T removeLast();
    T get(int index);

    /** Default method which returns true if the deque is empty, false otherwise. */
    default boolean isEmpty() {
        if (size() == 0) {
            return true;
        }
        return false;
    }
}

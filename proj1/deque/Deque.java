package deque;

public interface Deque<T> {
    public void addFirst(T item);
    public void addLast(T item);
    public int size();
    public void printDeque();
    public T removeFirst();
    public T removeLast();
    public T get(int index);

    /** Default method which returns true if the deque is empty, false otherwise. */
    default boolean isEmpty() {
        if (size() == 0) {
            return true;
        }
        else {
            return false;
        }
    }
}

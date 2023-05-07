package deque;

import java.util.Comparator;
public class MaxArrayDeque<T> extends ArrayDeque<T> {

    private Comparator<T> nc;

    public MaxArrayDeque(Comparator<T> c) {
        super();
        nc = c;
    }

    /** Returns the maximum element in the deque as governed by
     * the previously given Comparator. If the MaxArrayDeque is
     * empty, simply return null. */
    public T max() {
        if (this.size() == 0) {
            return null;
        }

        T currentMax = super.get(0);
        for (int i = 1; i < super.size(); i++) {
            T compared =  super.get(i);
            int outcome = nc.compare(compared, currentMax);

            if (outcome > 0) {
                currentMax = compared;
            }
        }
        return currentMax;
    }

    /** Returns the maximum element in the deque as governed by
     * the parameter Comparator c. If the MaxArrayDeque is empty,
     * simply return null. */
    public T max(Comparator<T> c) {
        if (this.size() == 0) {
            return null;
        }

        T currentMax = super.get(0);
        for (int i = 1; i < super.size(); i++) {
            T compared =  super.get(i);
            int outcome = c.compare(compared, currentMax);

            if (outcome > 0) {
                currentMax = compared;
            }
        }
        return currentMax;
    }
}

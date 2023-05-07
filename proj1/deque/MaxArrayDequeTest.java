package deque;

import org.junit.Test;
import java.util.Comparator;
import static org.junit.Assert.assertEquals;



public class MaxArrayDequeTest {
        public class IntegerComparator<T> implements Comparator<T> {
            public int compare(T a, T b) {
                Integer aValue =  (Integer) a;
                Integer bValue =  (Integer) b;

                return aValue.compareTo(bValue);
            }
        }

        public class StringComparator<T> implements Comparator<T> {
            public int compare(T a, T b) {
                String aString =  (String) a;
                String bString =  (String) b;

                return aString.compareTo(bString);
            }
        }

        @Test
        public void testMaxIntegerinArray() {
            Comparator<Integer> comparator = new IntegerComparator<>();
            MaxArrayDeque<Integer> array =  new MaxArrayDeque<>(comparator);
            array.addLast(44);
            array.addLast(33);
            array.addLast(16);
            array.addFirst(22);
            array.addFirst(23);

            float maxValue = array.max();
            assertEquals(maxValue,44.0, 0.0);
        }

        @Test
        public void testMaxStringinArray() {
            Comparator<String> comparator = new StringComparator<>();
            MaxArrayDeque<String> array =  new MaxArrayDeque<>(comparator);
            array.addLast("hamilton");
            array.addLast("verstappen");
            array.addLast("leclerc");
            array.addFirst("russell");
            array.addFirst("om");

            String maxString = array.max();

            System.out.println(maxString);
        }
    }


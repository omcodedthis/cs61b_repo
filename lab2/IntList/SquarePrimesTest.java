package IntList;

import static org.junit.Assert.*;
import org.junit.Test;

public class SquarePrimesTest {

    /**
     * Here is a test for isPrime method. Try running it.
     * It passes, but the starter code implementation of isPrime
     * is broken. Write your own JUnit Test to try to uncover the bug!
     */
    @Test
    public void testSquarePrimesSimple() {
        IntList lst = IntList.of(14, 15, 16, 17, 18);
        boolean changed = IntListExercises.squarePrimes(lst, false);
        assertEquals("14 -> 15 -> 16 -> 289 -> 18", lst.toString());
        assertTrue(changed);
    }

    @Test
    public void testAllPrimes() {
        IntList lst = IntList.of(2, 3, 5, 7, 11);
        boolean changed = IntListExercises.squarePrimes(lst, false);
        assertEquals("4 -> 9 -> 25 -> 49 -> 121", lst.toString());
        assertTrue(changed);
    }

    @Test
    public void testSomePrimes() {
        IntList lst = IntList.of(8, 19, 23, 30, 32);
        boolean changed = IntListExercises.squarePrimes(lst, false);
        assertEquals("8 -> 361 -> 529 -> 30 -> 32", lst.toString());
        assertTrue(changed);
    }

    @Test
    public void testNoPrimes() {
        IntList lst = IntList.of(2, 4, 6, 8, 44);
        boolean changed = IntListExercises.squarePrimes(lst, false);
        assertEquals("4 -> 4 -> 6 -> 8 -> 44", lst.toString());
        assertTrue(changed);
    }
}

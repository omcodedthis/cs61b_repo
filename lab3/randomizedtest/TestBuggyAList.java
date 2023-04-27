package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
  // YOUR TESTS HERE

    @Test
    public void testThreeAddthreeRemove() {
        AListNoResizing<Integer> lstNoResize = new AListNoResizing<>();
        BuggyAList<Integer> lstBuggy = new BuggyAList<>();

        lstNoResize.addLast(44);
        lstBuggy.addLast(44);

        lstNoResize.addLast(16);
        lstBuggy.addLast(16);

        lstNoResize.addLast(33);
        lstBuggy.addLast(33);

        int lastNoResize;
        int lastBuggy;

        lastNoResize = lstNoResize.removeLast();
        lastBuggy = lstBuggy.removeLast();
        assertEquals(lastNoResize, lastBuggy);

        lastNoResize = lstNoResize.removeLast();
        lastBuggy = lstBuggy.removeLast();
        assertEquals(lastNoResize, lastBuggy);

        lastNoResize = lstNoResize.removeLast();
        lastBuggy = lstBuggy.removeLast();
        assertEquals(lastNoResize, lastBuggy);
    }

    @Test
    public void randomizedTest() {
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> LBuggy = new BuggyAList<>();

        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                LBuggy.addLast(randVal);
                System.out.println("addLast(" + randVal + ")");
            } else if (operationNumber == 1) {
                // size
                int size = L.size();
                int sizeBuggy = LBuggy.size();
                System.out.println("size: " + size);
                System.out.println("sizeBuggy: " + sizeBuggy);
                assertEquals(size, sizeBuggy);
            } else if (operationNumber == 2) {
                // getLast
                if (L.size() <= 0) {
                    continue;
                }
                int lastItem = L.getLast();
                int lastItemBuggy = LBuggy.getLast();
                System.out.println("last: " + lastItem);
                System.out.println("lastBuggy: " + lastItemBuggy);
                assertEquals(lastItem, lastItemBuggy);
            } else if (operationNumber == 3) {
                // removeLast
                if (L.size() <= 0) {
                    continue;
                }
                int removedItem = L.removeLast();
                int removedItemBuggy = LBuggy.removeLast();
                System.out.println("removed: " + removedItem);
                System.out.println("removedBuggy: " + removedItemBuggy);
                assertEquals(removedItem, removedItemBuggy);
            }
        }
    }
}

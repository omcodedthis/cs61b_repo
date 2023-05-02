package tester;
import static org.junit.Assert.*;

import edu.princeton.cs.introcs.StdRandom;
import org.junit.Test;
import student.StudentArrayDeque;

public class TestArrayDequeEC {
    @Test
    public void compareStudenttoSolution() {
            ArrayDequeSolution<Integer> solArray = new ArrayDequeSolution<>();
            StudentArrayDeque<Integer> testArray = new StudentArrayDeque<>();

            for (int i = 0; i < 100; i++) {
                solArray.addLast(i);
                testArray.addLast(i);
            }

            int test = StdRandom.uniform(4);
            int cnt = 0;

            while (true) {

                if (cnt > 50) {
                    test = StdRandom.uniform(4);
                }

                if (test == 0) {
                    cnt++;

                    solArray.addFirst(44);
                    testArray.addFirst(44);

                    solArray.addFirst(33);
                    testArray.addFirst(33);

                    solArray.addFirst(16);
                    testArray.addFirst(16);

                    Integer expected = solArray.get(cnt);
                    Integer actual = testArray.get(cnt);

                    System.out.println("addFirst(44)");

                    assertEquals("addFirst(44)", expected, actual);


                }

                else if (test == 1) {
                    cnt++;

                    solArray.addFirst(44);
                    testArray.addFirst(44);

                    solArray.addFirst(33);
                    testArray.addFirst(33);

                    solArray.addFirst(16);
                    testArray.addFirst(16);

                    Integer expected = solArray.get(cnt);
                    Integer actual = testArray.get(cnt);

                    System.out.println("addLast(44)");

                    assertEquals("addLast(44)", expected, actual);

                }
                else if (test == 2) {
                    cnt++;

                    solArray.addFirst(44);
                    testArray.addFirst(44);

                    solArray.addFirst(33);
                    testArray.addFirst(33);

                    solArray.addFirst(16);
                    testArray.addFirst(16);

                    Integer expected = solArray.removeFirst();
                    Integer actual = testArray.removeFirst();

                    System.out.println("removeFirst()");

                    assertEquals("removeFirst()", expected, actual);
                }

                else if (test == 3) {
                    cnt++;

                    solArray.addFirst(44);
                    testArray.addFirst(44);

                    solArray.addFirst(33);
                    testArray.addFirst(33);

                    solArray.addFirst(16);
                    testArray.addFirst(16);

                    Integer expected = solArray.removeLast();
                    Integer actual = testArray.removeLast();

                    System.out.println("removeLast()");

                    assertEquals("removeLast()", expected, actual);
                }
            }
    }
}

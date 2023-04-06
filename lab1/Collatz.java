/** Class that prints the Collatz sequence starting from a given number.
 *  @author om
 */
public class Collatz {

    /** nextNumber() first determines whether n is positive or negative. If n is even, n/2 is returned. If n is
     odd, 3n + 1 is returned. */
    public static int nextNumber(int n) {
        int remainder = n % 2;
        if (remainder == 0) {
            return (n / 2);
        }

        else {
            return (3 * n) + 1;
        }
    }

    public static void main(String[] args) {
        int n = 5;
        System.out.print(n + " ");
        while (n != 1) {
            n = nextNumber(n);
            System.out.print(n + " ");
        }
        System.out.println();
    }
}


package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeAList {
    private static void printTimingTable(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    public static void main(String[] args) {
        timeAListConstruction();
    }

    public static void timeAListConstruction() {
        // TODO: YOUR CODE HERE
        Integer N = 1000;
        Stopwatch sw = new Stopwatch();
        AList<Integer> Ns = new AList<>();
        AList<Double> timings = new AList<>();
        AList<Integer> opCounts = new AList<>();

        for (int i = 0; i < 8; i++) {
            AList<Integer> timedList = new AList<>();
            Integer opCount = 0;

            for (int j = 0; j < N; j++) {
                timedList.addLast(44);
                opCount++;
            }

            double timeInSeconds = sw.elapsedTime();

            timings.addLast(timeInSeconds);
            opCounts.addLast(opCount);

            Ns.addLast(N);

            N *= 2;
        }
        printTimingTable(Ns, timings, opCounts);
    }
}

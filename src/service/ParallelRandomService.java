package service;

import model.TransitEvent;

import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * Implements Module 6 (CO6) Randomized and Parallel Algorithms:
 * 1. Randomized QuickSort - O(N log N) expected sorting for transit event ranking.
 * 2. Reservoir Sampling (Algorithm R) - Streaming uniform random sample of size K.
 * 3. Miller-Rabin Primality Test - Secure service token generation.
 * 4. Blelloch Parallel Prefix Scan - Work-efficient cumulative ridership calculation.
 * 5. Parallel Reduce & Brent's Theorem - Multi-threaded statistical reduction.
 */
public class ParallelRandomService {

    private static final Random RNG = new Random();

    // ==========================================
    // 1. Randomized QuickSort
    // ==========================================
    public static void randomizedQuickSort(List<TransitEvent> list, int low, int high) {
        if (low < high) {
            int pivotIndex = randomizedPartition(list, low, high);
            randomizedQuickSort(list, low, pivotIndex - 1);
            randomizedQuickSort(list, pivotIndex + 1, high);
        }
    }

    private static int randomizedPartition(List<TransitEvent> list, int low, int high) {
        int randomPivot = low + RNG.nextInt(high - low + 1);
        Collections.swap(list, randomPivot, high);
        return partition(list, low, high);
    }

    private static int partition(List<TransitEvent> list, int low, int high) {
        TransitEvent pivot = list.get(high);
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (list.get(j).compareTo(pivot) <= 0) {
                i++;
                Collections.swap(list, i, j);
            }
        }
        Collections.swap(list, i + 1, high);
        return i + 1;
    }

    // ==========================================
    // 2. Reservoir Sampling (Algorithm R)
    // ==========================================
    /**
     * Samples K items uniformly at random from a continuous stream of N events.
     * Each item has equal probability (K / N) of being included in the final reservoir.
     * Space Complexity: O(K)
     */
    public static List<TransitEvent> reservoirSample(Iterator<TransitEvent> stream, int k) {
        List<TransitEvent> reservoir = new ArrayList<>(k);
        int count = 0;

        while (stream.hasNext()) {
            TransitEvent item = stream.next();
            count++;
            if (reservoir.size() < k) {
                reservoir.add(item);
            } else {
                int r = RNG.nextInt(count);
                if (r < k) {
                    reservoir.set(r, item);
                }
            }
        }
        return reservoir;
    }

    // ==========================================
    // 3. Miller-Rabin Primality Test
    // ==========================================
    /**
     * Probabilistic Miller-Rabin Primality Test for large numbers.
     * Error probability bounded by (1/4)^k rounds.
     */
    public static boolean millerRabinTest(BigInteger n, int rounds) {
        if (n.compareTo(BigInteger.ONE) <= 0) return false;
        if (n.equals(BigInteger.TWO) || n.equals(BigInteger.valueOf(3))) return true;
        if (!n.testBit(0)) return false; // Even number > 2

        BigInteger d = n.subtract(BigInteger.ONE);
        int s = 0;
        while (!d.testBit(0)) {
            d = d.shiftRight(1);
            s++;
        }

        for (int i = 0; i < rounds; i++) {
            BigInteger a = uniformRandom(BigInteger.TWO, n.subtract(BigInteger.TWO));
            BigInteger x = a.modPow(d, n);

            if (x.equals(BigInteger.ONE) || x.equals(n.subtract(BigInteger.ONE))) {
                continue;
            }

            boolean composite = true;
            for (int r = 1; r < s; r++) {
                x = x.modPow(BigInteger.TWO, n);
                if (x.equals(n.subtract(BigInteger.ONE))) {
                    composite = false;
                    break;
                }
            }
            if (composite) return false;
        }
        return true;
    }

    private static BigInteger uniformRandom(BigInteger min, BigInteger max) {
        BigInteger range = max.subtract(min).add(BigInteger.ONE);
        BigInteger res;
        do {
            res = new BigInteger(range.bitLength(), RNG);
        } while (res.compareTo(range) >= 0);
        return res.add(min);
    }

    // ==========================================
    // 4. Blelloch Work-Efficient Parallel Prefix Scan
    // ==========================================
    /**
     * Computes the in-place prefix sum (cumulative passenger counts) using the Blelloch scan algorithm.
     * Work Complexity: O(N)
     * Span / Step Complexity: O(log N)
     * Requires array size to be a power of 2.
     */
    public static long[] blellochExclusiveScan(long[] input) {
        int n = input.length;
        long[] a = Arrays.copyOf(input, n);

        // Phase 1: Up-Sweep (Reduce tree)
        for (int step = 1; step < n; step *= 2) {
            for (int i = 0; i < n; i += 2 * step) {
                a[i + 2 * step - 1] += a[i + step - 1];
            }
        }

        // Clear the root of the tree
        a[n - 1] = 0;

        // Phase 2: Down-Sweep
        for (int step = n / 2; step >= 1; step /= 2) {
            for (int i = 0; i < n; i += 2 * step) {
                long temp = a[i + step - 1];
                a[i + step - 1] = a[i + 2 * step - 1];
                a[i + 2 * step - 1] += temp;
            }
        }
        return a;
    }

    // ==========================================
    // 5. Parallel Reduce & Brent's Theorem
    // ==========================================
    public static class ParallelReduceSumTask extends RecursiveTask<Long> {
        private final long[] array;
        private final int start, end;
        private static final int THRESHOLD = 1000;

        public ParallelReduceSumTask(long[] array, int start, int end) {
            this.array = array;
            this.start = start;
            this.end = end;
        }

        @Override
        protected Long compute() {
            if (end - start <= THRESHOLD) {
                long sum = 0;
                for (int i = start; i < end; i++) sum += array[i];
                return sum;
            } else {
                int mid = start + (end - start) / 2;
                ParallelReduceSumTask left = new ParallelReduceSumTask(array, start, mid);
                ParallelReduceSumTask right = new ParallelReduceSumTask(array, mid, end);
                left.fork();
                long rightResult = right.compute();
                long leftResult = left.join();
                return leftResult + rightResult;
            }
        }
    }

    public static long executeParallelReduce(long[] data) {
        ForkJoinPool pool = ForkJoinPool.commonPool();
        return pool.invoke(new ParallelReduceSumTask(data, 0, data.length));
    }

    public static String explainBrentsTheorem(long work, long span, int processors) {
        double theoreticalTime = ((double) work / processors) + span;
        return String.format("""
        ========================================================================
        BRENT'S SCHEDULING THEOREM DEMONSTRATION:
        ------------------------------------------------------------------------
        Formula: T_p <= (T_1 / p) + T_inf
        - Work (T_1, sequential work)   : %d operations
        - Span (T_inf, critical path)  : %d steps
        - Processors available (p)      : %d cores
        - Upper bound execution time Tp : <= %.2f time units
        - Maximum theoretical speedup   : %.2fx
        ========================================================================
        """, work, span, processors, theoreticalTime, (double) work / theoreticalTime);
    }
}

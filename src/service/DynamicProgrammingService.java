package service;

import model.Station;

import java.util.*;

/**
 * Implements Module 3 (CO3) Advanced Dynamic Programming for public transport:
 * 1. Levenshtein / Damerau-Levenshtein - Typo correction and fuzzy station search.
 * 2. Bitmask DP - Shortest multi-stop tour covering a subset of destination stations.
 * 3. Matrix-Chain Multiplication (MCM) - Optimal evaluation order for transit transition matrices.
 * 4. Optimal Binary Search Tree (OBST) - Minimizing expected search cost for station frequency lookups.
 */
public class DynamicProgrammingService {

    // ==========================================
    // 1. Damerau-Levenshtein Distance (DP)
    // ==========================================
    /**
     * Computes Damerau-Levenshtein distance (supports insertion, deletion, substitution, and adjacent transposition).
     * Time Complexity: O(|s1| * |s2|)
     * Space Complexity: O(|s1| * |s2|)
     */
    public static int damerauLevenshteinDistance(String s1, String s2) {
        if (s1 == null) s1 = "";
        if (s2 == null) s2 = "";
        s1 = s1.toLowerCase().trim();
        s2 = s2.toLowerCase().trim();

        int n = s1.length();
        int m = s2.length();
        int[][] dp = new int[n + 1][m + 1];

        for (int i = 0; i <= n; i++) dp[i][0] = i;
        for (int j = 0; j <= m; j++) dp[0][j] = j;

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                int cost = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;
                // Standard operations: deletion, insertion, substitution
                dp[i][j] = Math.min(dp[i - 1][j] + 1, Math.min(dp[i][j - 1] + 1, dp[i - 1][j - 1] + cost));

                // Damerau Transposition
                if (i > 1 && j > 1 && s1.charAt(i - 1) == s2.charAt(j - 2) && s1.charAt(i - 2) == s2.charAt(j - 1)) {
                    dp[i][j] = Math.min(dp[i][j], dp[i - 2][j - 2] + 1);
                }
            }
        }
        return dp[n][m];
    }

    /**
     * Suggests closest station names given a misspelled station query.
     */
    public static List<Map.Entry<Station, Integer>> findFuzzyMatches(Collection<Station> stations, String query, int maxDistance) {
        List<Map.Entry<Station, Integer>> suggestions = new ArrayList<>();
        for (Station s : stations) {
            int dist = damerauLevenshteinDistance(query, s.getName());
            if (dist <= maxDistance) {
                suggestions.add(new AbstractMap.SimpleEntry<>(s, dist));
            }
        }
        suggestions.sort(Comparator.comparingInt(Map.Entry::getValue));
        return suggestions;
    }

    // ==========================================
    // 2. Bitmask DP (Multi-Destination Tour)
    // ==========================================
    /**
     * Computes the optimal tour visiting a subset of K stations (e.g. maintenance inspection tour).
     * State: dp[mask][u] = minimum distance to visit the set of stations in mask, ending at station u.
     * Time Complexity: O(K^2 * 2^K)
     * Space Complexity: O(K * 2^K)
     */
    public static class BitmaskTourResult {
        public final double minTotalDistance;
        public final List<Station> orderedTour;

        public BitmaskTourResult(double minTotalDistance, List<Station> orderedTour) {
            this.minTotalDistance = minTotalDistance;
            this.orderedTour = orderedTour;
        }
    }

    public static BitmaskTourResult computeOptimalMultiStopTour(List<Station> selectedStations, double[][] distMatrix) {
        int k = selectedStations.size();
        if (k == 0) return new BitmaskTourResult(0.0, Collections.emptyList());
        if (k == 1) return new BitmaskTourResult(0.0, Collections.singletonList(selectedStations.get(0)));

        int totalMasks = 1 << k;
        double[][] dp = new double[totalMasks][k];
        int[][] parent = new int[totalMasks][k];

        for (int mask = 0; mask < totalMasks; mask++) {
            Arrays.fill(dp[mask], Double.POSITIVE_INFINITY);
            Arrays.fill(parent[mask], -1);
        }

        // Start at station 0
        dp[1][0] = 0.0;

        for (int mask = 1; mask < totalMasks; mask++) {
            for (int u = 0; u < k; u++) {
                if ((mask & (1 << u)) == 0 || dp[mask][u] == Double.POSITIVE_INFINITY) continue;

                for (int v = 0; v < k; v++) {
                    if ((mask & (1 << v)) != 0) continue; // Already visited
                    int nextMask = mask | (1 << v);
                    double cost = dp[mask][u] + distMatrix[u][v];
                    if (cost < dp[nextMask][v]) {
                        dp[nextMask][v] = cost;
                        parent[nextMask][v] = u;
                    }
                }
            }
        }

        // Find best end station that returns back to start (station 0)
        int allVisited = totalMasks - 1;
        double bestCost = Double.POSITIVE_INFINITY;
        int bestLast = -1;

        for (int u = 1; u < k; u++) {
            double cost = dp[allVisited][u] + distMatrix[u][0];
            if (cost < bestCost) {
                bestCost = cost;
                bestLast = u;
            }
        }

        // Reconstruct path
        LinkedList<Station> tour = new LinkedList<>();
        if (bestLast != -1) {
            int currMask = allVisited;
            int currNode = bestLast;
            while (currNode != -1) {
                tour.addFirst(selectedStations.get(currNode));
                int prev = parent[currMask][currNode];
                currMask = currMask ^ (1 << currNode);
                currNode = prev;
            }
            tour.addLast(selectedStations.get(0)); // Return to start
        }

        return new BitmaskTourResult(Math.round(bestCost * 100.0) / 100.0, tour);
    }

    // ==========================================
    // 3. Matrix-Chain Multiplication (MCM)
    // ==========================================
    /**
     * Determines optimal parenthesization order for multiplying a chain of transit transition matrices.
     * Time Complexity: O(N^3)
     * Space Complexity: O(N^2)
     */
    public static class McmResult {
        public final int minOperations;
        public final String parenthesization;

        public McmResult(int minOperations, String parenthesization) {
            this.minOperations = minOperations;
            this.parenthesization = parenthesization;
        }
    }

    public static McmResult computeMatrixChainMultiplication(int[] dimensions) {
        int n = dimensions.length - 1; // number of matrices
        int[][] m = new int[n + 1][n + 1];
        int[][] s = new int[n + 1][n + 1];

        for (int len = 2; len <= n; len++) {
            for (int i = 1; i <= n - len + 1; i++) {
                int j = i + len - 1;
                m[i][j] = Integer.MAX_VALUE;
                for (int k = i; k <= j - 1; k++) {
                    int cost = m[i][k] + m[k + 1][j] + dimensions[i - 1] * dimensions[k] * dimensions[j];
                    if (cost < m[i][j]) {
                        m[i][j] = cost;
                        s[i][j] = k;
                    }
                }
            }
        }

        String paren = buildMcmString(s, 1, n);
        return new McmResult(m[1][n], paren);
    }

    private static String buildMcmString(int[][] s, int i, int j) {
        if (i == j) {
            return "M" + i;
        }
        return "(" + buildMcmString(s, i, s[i][j]) + " x " + buildMcmString(s, s[i][j] + 1, j) + ")";
    }

    // ==========================================
    // 4. Optimal Binary Search Tree (OBST)
    // ==========================================
    /**
     * Computes the Optimal Binary Search Tree for station frequencies to minimize expected search cost.
     * Time Complexity: O(N^3)
     * Space Complexity: O(N^2)
     */
    public static class ObstResult {
        public final double minExpectedCost;
        public final int[][] root;
        public final List<String> stationNames;

        public ObstResult(double minExpectedCost, int[][] root, List<String> stationNames) {
            this.minExpectedCost = minExpectedCost;
            this.root = root;
            this.stationNames = stationNames;
        }
    }

    public static ObstResult computeOptimalBst(List<String> stations, double[] probabilities) {
        int n = stations.size();
        double[][] e = new double[n + 2][n + 2];
        double[][] w = new double[n + 2][n + 2];
        int[][] root = new int[n + 1][n + 1];

        for (int i = 1; i <= n + 1; i++) {
            e[i][i - 1] = 0;
            w[i][i - 1] = 0;
        }

        for (int l = 1; l <= n; l++) {
            for (int i = 1; i <= n - l + 1; i++) {
                int j = i + l - 1;
                e[i][j] = Double.POSITIVE_INFINITY;
                w[i][j] = w[i][j - 1] + probabilities[j - 1];

                for (int r = i; r <= j; r++) {
                    double t = e[i][r - 1] + e[r + 1][j] + w[i][j];
                    if (t < e[i][j]) {
                        e[i][j] = t;
                        root[i][j] = r;
                    }
                }
            }
        }

        return new ObstResult(Math.round(e[1][n] * 1000.0) / 1000.0, root, stations);
    }
}

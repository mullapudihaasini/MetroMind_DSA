package service;

import java.util.*;

/**
 * Implements Module 1 (CO1) String Algorithms for public transport:
 * 1. KMP (Knuth-Morris-Pratt) - O(N + M) exact pattern matching in station names & alerts.
 * 2. Z-Function - O(N) periodic pattern and repeat route description detector.
 * 3. Rabin-Karp - O(N + M) rolling hash pattern matcher for ticket/service codes.
 * 4. Aho-Corasick - Multi-pattern string matching automaton for transit service alerts.
 */
public class StringAlgorithmService {

    // ==========================================
    // 1. KMP (Knuth-Morris-Pratt) Algorithm
    // ==========================================
    /**
     * Computes the KMP Longest Proper Prefix which is also Suffix (LPS) array.
     * Time Complexity: O(M)
     */
    public static int[] computeKmpLps(String pattern) {
        int m = pattern.length();
        int[] lps = new int[m];
        int len = 0;
        int i = 1;

        while (i < m) {
            if (pattern.charAt(i) == pattern.charAt(len)) {
                len++;
                lps[i] = len;
                i++;
            } else {
                if (len != 0) {
                    len = lps[len - 1];
                } else {
                    lps[i] = 0;
                    i++;
                }
            }
        }
        return lps;
    }

    /**
     * Executes KMP search on text for a single pattern.
     * Time Complexity: O(N + M)
     * Space Complexity: O(M)
     */
    public static List<Integer> kmpSearch(String text, String pattern) {
        List<Integer> matches = new ArrayList<>();
        if (text == null || pattern == null || pattern.isEmpty() || text.length() < pattern.length()) {
            return matches;
        }

        int[] lps = computeKmpLps(pattern);
        int i = 0; // index for text
        int j = 0; // index for pattern

        while (i < text.length()) {
            if (Character.toLowerCase(text.charAt(i)) == Character.toLowerCase(pattern.charAt(j))) {
                i++;
                j++;
            }

            if (j == pattern.length()) {
                matches.add(i - j); // Found match at index (i - j)
                j = lps[j - 1];
            } else if (i < text.length() && Character.toLowerCase(text.charAt(i)) != Character.toLowerCase(pattern.charAt(j))) {
                if (j != 0) {
                    j = lps[j - 1];
                } else {
                    i++;
                }
            }
        }
        return matches;
    }

    // ==========================================
    // 2. Z-Algorithm
    // ==========================================
    /**
     * Computes the Z-array for string S.
     * Z[i] is the length of the longest substring starting from S[i] that is also a prefix of S.
     * Time Complexity: O(N) linear time using the Z-box technique.
     */
    public static int[] computeZArray(String s) {
        int n = s.length();
        int[] z = new int[n];
        int l = 0, r = 0;

        for (int i = 1; i < n; i++) {
            if (i <= r) {
                z[i] = Math.min(r - i + 1, z[i - l]);
            }
            while (i + z[i] < n && s.charAt(z[i]) == s.charAt(i + z[i])) {
                z[i]++;
            }
            if (i + z[i] - 1 > r) {
                l = i;
                r = i + z[i] - 1;
            }
        }
        return z;
    }

    /**
     * Detects repeated patterns or periodic cycles in route descriptions using Z-Function.
     */
    public static List<Integer> findRepeatedPatternsZ(String text, String pattern) {
        String combined = pattern + "$" + text;
        int[] z = computeZArray(combined);
        List<Integer> matchIndices = new ArrayList<>();
        int pLen = pattern.length();

        for (int i = pLen + 1; i < combined.length(); i++) {
            if (z[i] == pLen) {
                matchIndices.add(i - (pLen + 1));
            }
        }
        return matchIndices;
    }

    // ==========================================
    // 3. Rabin-Karp Algorithm
    // ==========================================
    private static final long RK_PRIME = 1000000007L;
    private static final int RK_RADIX = 256;

    /**
     * Rabin-Karp pattern search using polynomial rolling hash.
     * Average Time Complexity: O(N + M)
     * Worst Case: O(N * M)
     */
    public static List<Integer> rabinKarpSearch(String text, String pattern) {
        List<Integer> matches = new ArrayList<>();
        if (text == null || pattern == null || pattern.length() > text.length()) {
            return matches;
        }

        int n = text.length();
        int m = pattern.length();
        long pHash = 0;
        long tHash = 0;
        long h = 1;

        for (int i = 0; i < m - 1; i++) {
            h = (h * RK_RADIX) % RK_PRIME;
        }

        for (int i = 0; i < m; i++) {
            pHash = (RK_RADIX * pHash + pattern.charAt(i)) % RK_PRIME;
            tHash = (RK_RADIX * tHash + text.charAt(i)) % RK_PRIME;
        }

        for (int i = 0; i <= n - m; i++) {
            if (pHash == tHash) {
                // Verify characters on hash collision
                boolean match = true;
                for (int j = 0; j < m; j++) {
                    if (text.charAt(i + j) != pattern.charAt(j)) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    matches.add(i);
                }
            }

            if (i < n - m) {
                tHash = (RK_RADIX * (tHash - text.charAt(i) * h) + text.charAt(i + m)) % RK_PRIME;
                if (tHash < 0) {
                    tHash = (tHash + RK_PRIME);
                }
            }
        }
        return matches;
    }

    // ==========================================
    // 4. Aho-Corasick Multi-Pattern Automaton
    // ==========================================
    public static class AhoCorasickNode {
        Map<Character, AhoCorasickNode> children = new HashMap<>();
        AhoCorasickNode failLink = null;
        List<String> outputPatterns = new ArrayList<>();
    }

    public static class AhoCorasickMatcher {
        private final AhoCorasickNode root = new AhoCorasickNode();

        public void addPattern(String pattern) {
            AhoCorasickNode curr = root;
            for (char ch : pattern.toLowerCase().toCharArray()) {
                curr = curr.children.computeIfAbsent(ch, c -> new AhoCorasickNode());
            }
            curr.outputPatterns.add(pattern);
        }

        public void buildFailureLinks() {
            Queue<AhoCorasickNode> queue = new ArrayDeque<>();
            for (AhoCorasickNode child : root.children.values()) {
                child.failLink = root;
                queue.add(child);
            }

            while (!queue.isEmpty()) {
                AhoCorasickNode curr = queue.poll();

                for (Map.Entry<Character, AhoCorasickNode> entry : curr.children.entrySet()) {
                    char ch = entry.getKey();
                    AhoCorasickNode child = entry.getValue();

                    AhoCorasickNode fallback = curr.failLink;
                    while (fallback != null && !fallback.children.containsKey(ch)) {
                        fallback = fallback.failLink;
                    }
                    child.failLink = (fallback != null) ? fallback.children.get(ch) : root;
                    // Inherit output matches from failure link
                    if (child.failLink != null) {
                        child.outputPatterns.addAll(child.failLink.outputPatterns);
                    }
                    queue.add(child);
                }
            }
        }

        public Map<String, List<Integer>> searchAll(String text) {
            Map<String, List<Integer>> results = new HashMap<>();
            AhoCorasickNode curr = root;
            String lowerText = text.toLowerCase();

            for (int i = 0; i < lowerText.length(); i++) {
                char ch = lowerText.charAt(i);
                while (curr != null && !curr.children.containsKey(ch)) {
                    curr = curr.failLink;
                }
                if (curr == null) {
                    curr = root;
                    continue;
                }
                curr = curr.children.get(ch);
                for (String matchedPattern : curr.outputPatterns) {
                    results.computeIfAbsent(matchedPattern, k -> new ArrayList<>())
                           .add(i - matchedPattern.length() + 1);
                }
            }
            return results;
        }
    }
}

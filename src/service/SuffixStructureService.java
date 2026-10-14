package service;

import java.util.*;

/**
 * Implements Module 2 (CO2) Suffix Structures for public transport document analysis:
 * 1. Suffix Array - Indexes large route bulletins and service manuals.
 * 2. Kasai's Algorithm for LCP (Longest Common Prefix) - Identifies common route descriptions in O(N).
 * 3. Suffix Automaton (DAWG) - Compact linear-size automaton for O(|P|) substring retrieval.
 */
public class SuffixStructureService {

    // ==========================================
    // 1. Suffix Array Construction
    // ==========================================
    /**
     * Builds Suffix Array for a given transport document string.
     * Returns an array of starting indices of sorted suffixes.
     */
    public static Integer[] buildSuffixArray(String s) {
        int n = s.length();
        Integer[] sa = new Integer[n];
        for (int i = 0; i < n; i++) sa[i] = i;

        Arrays.sort(sa, (a, b) -> s.substring(a).compareTo(s.substring(b)));
        return sa;
    }

    // ==========================================
    // 2. Kasai's LCP (Longest Common Prefix) Algorithm
    // ==========================================
    /**
     * Computes the LCP array using Kasai's Algorithm in linear O(N) time.
     * lcp[i] = length of the longest common prefix between suffix sa[i] and suffix sa[i-1].
     */
    public static int[] buildLcpKasai(String s, Integer[] sa) {
        int n = s.length();
        int[] lcp = new int[n];
        int[] rank = new int[n];

        for (int i = 0; i < n; i++) {
            rank[sa[i]] = i;
        }

        int h = 0;
        for (int i = 0; i < n; i++) {
            if (rank[i] > 0) {
                int k = sa[rank[i] - 1];
                while (i + h < n && k + h < n && s.charAt(i + h) == s.charAt(k + h)) {
                    h++;
                }
                lcp[rank[i]] = h;
                if (h > 0) {
                    h--;
                }
            }
        }
        return lcp;
    }

    /**
     * Identifies the longest repeated phrase/description across route documents.
     */
    public static String findLongestRepeatedSubstring(String s) {
        if (s == null || s.length() < 2) return "";
        Integer[] sa = buildSuffixArray(s);
        int[] lcp = buildLcpKasai(s, sa);

        int maxLen = 0;
        int startIndex = 0;
        for (int i = 1; i < s.length(); i++) {
            if (lcp[i] > maxLen) {
                maxLen = lcp[i];
                startIndex = sa[i];
            }
        }
        return maxLen > 0 ? s.substring(startIndex, startIndex + maxLen) : "";
    }

    // ==========================================
    // 3. Suffix Automaton (DAWG)
    // ==========================================
    public static class SuffixAutomaton {
        public static class State {
            int len, link;
            Map<Character, Integer> next = new HashMap<>();

            State(int len, int link) {
                this.len = len;
                this.link = link;
            }
        }

        private final List<State> states = new ArrayList<>();
        private int last;

        public SuffixAutomaton(String s) {
            states.add(new State(0, -1));
            last = 0;
            for (char c : s.toCharArray()) {
                extend(c);
            }
        }

        private void extend(char c) {
            int cur = states.size();
            states.add(new State(states.get(last).len + 1, -1));
            int p = last;

            while (p != -1 && !states.get(p).next.containsKey(c)) {
                states.get(p).next.put(c, cur);
                p = states.get(p).link;
            }

            if (p == -1) {
                states.get(cur).link = 0;
            } else {
                int q = states.get(p).next.get(c);
                if (states.get(p).len + 1 == states.get(q).len) {
                    states.get(cur).link = q;
                } else {
                    int clone = states.size();
                    State cloneState = new State(states.get(p).len + 1, states.get(q).link);
                    cloneState.next.putAll(states.get(q).next);
                    states.add(cloneState);

                    while (p != -1 && states.get(p).next.get(c) == q) {
                        states.get(p).next.put(c, clone);
                        p = states.get(p).link;
                    }
                    states.get(q).link = clone;
                    states.get(cur).link = clone;
                }
            }
            last = cur;
        }

        /**
         * Checks if a pattern exists in the document in O(|pattern|) time.
         */
        public boolean containsSubstring(String pattern) {
            int curr = 0;
            for (char c : pattern.toCharArray()) {
                if (!states.get(curr).next.containsKey(c)) {
                    return false;
                }
                curr = states.get(curr).next.get(c);
            }
            return true;
        }

        public int getStateCount() {
            return states.size();
        }
    }
}

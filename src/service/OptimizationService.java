package service;

import model.Connection;
import model.Station;

import java.util.*;

/**
 * Implements Module 5 (CO5) NP-Completeness and Approximation Algorithms:
 * 1. SAT / 3-SAT Solver (DPLL) for dispatch slot scheduling constraints.
 * 2. 3-SAT -> CLIQUE -> INDEPENDENT SET -> VERTEX COVER Reduction Pipeline.
 * 3. Vertex-Cover 2-Approximation Algorithm for optimal station surveillance placement.
 */
public class OptimizationService {

    // ==========================================
    // 1. SAT / 3-SAT Solver (DPLL Algorithm)
    // ==========================================
    public static class Clause {
        public final List<Integer> literals; // Positive = x_i, Negative = -x_i

        public Clause(Integer... lits) {
            this.literals = Arrays.asList(lits);
        }

        public Clause(List<Integer> lits) {
            this.literals = new ArrayList<>(lits);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("(");
            for (int i = 0; i < literals.size(); i++) {
                int lit = literals.get(i);
                sb.append(lit > 0 ? "x" + lit : "~x" + (-lit));
                if (i < literals.size() - 1) sb.append(" v ");
            }
            sb.append(")");
            return sb.toString();
        }
    }

    /**
     * DPLL (Davis-Putnam-Logemann-Loveland) recursive backtracking SAT solver.
     */
    public static Map<Integer, Boolean> solveDpllSat(List<Clause> cnfClauses, Set<Integer> variables) {
        Map<Integer, Boolean> assignment = new HashMap<>();
        if (dpll(cnfClauses, assignment, new ArrayList<>(variables), 0)) {
            return assignment;
        }
        return null; // Unsatisfiable
    }

    private static boolean dpll(List<Clause> clauses, Map<Integer, Boolean> assignment, List<Integer> varList, int index) {
        // Check current clause evaluations
        boolean allSatisfied = true;
        for (Clause c : clauses) {
            Boolean clauseValue = evaluateClause(c, assignment);
            if (Boolean.FALSE.equals(clauseValue)) {
                return false; // Conflict found
            }
            if (clauseValue == null) {
                allSatisfied = false;
            }
        }

        if (allSatisfied) {
            return true;
        }

        if (index >= varList.size()) {
            return false;
        }

        int var = varList.get(index);

        // Branch True
        assignment.put(var, true);
        if (dpll(clauses, assignment, varList, index + 1)) return true;

        // Branch False
        assignment.put(var, false);
        if (dpll(clauses, assignment, varList, index + 1)) return true;

        // Backtrack
        assignment.remove(var);
        return false;
    }

    private static Boolean evaluateClause(Clause clause, Map<Integer, Boolean> assignment) {
        boolean hasUnassigned = false;
        for (int lit : clause.literals) {
            int var = Math.abs(lit);
            if (assignment.containsKey(var)) {
                boolean val = assignment.get(var);
                boolean literalTrue = (lit > 0) ? val : !val;
                if (literalTrue) return true;
            } else {
                hasUnassigned = true;
            }
        }
        return hasUnassigned ? null : false;
    }

    // ==========================================
    // 2. Reduction Pipeline Explanation
    // ==========================================
    public static String explainNpHardReductionPipeline() {
        return """
        ========================================================================
        NP-COMPLETENESS REDUCTION PIPELINE FOR SERVICE CONFLICTS:
        ------------------------------------------------------------------------
        1. 3-SAT -> CLIQUE:
           - Each 3-literal clause creates a 3-vertex cluster.
           - Edges connect literals across different clauses that do NOT contradict.
           - A satisfying assignment of K clauses corresponds to a Clique of size K.
        
        2. CLIQUE -> INDEPENDENT SET:
           - Graph G has a Clique of size K <=> Complement Graph G' has an 
             Independent Set of size K (mutually non-conflicting service departures).
        
        3. INDEPENDENT SET -> VERTEX COVER:
           - A set S is an Independent Set in G <=> (V - S) is a Vertex Cover.
           - Maximizing conflict-free train schedules is polynomial-time equivalent 
             to finding the Minimum Vertex Cover of the conflict graph.
        ========================================================================
        """;
    }

    // ==========================================
    // 3. Vertex-Cover 2-Approximation Algorithm
    // ==========================================
    /**
     * Finds a set of stations to install security checkpoints such that every track connection is monitored.
     * Uses the Maximal Matching heuristic to achieve a 2-Approximation guarantee:
     * |C| <= 2 * |C*| (at most twice the optimal minimum vertex cover).
     */
    public static Set<String> computeVertexCover2Approx(MetroGraph graph) {
        Set<String> vertexCover = new HashSet<>();
        Set<String> coveredEdges = new HashSet<>();

        // Iterate over unique connections
        for (Station s : graph.getAllStations()) {
            for (Connection conn : graph.getConnections(s.getId())) {
                String u = conn.getSource().getId();
                String v = conn.getDestination().getId();
                String edgeKey = u.compareTo(v) < 0 ? u + "-" + v : v + "-" + u;

                if (!coveredEdges.contains(edgeKey)) {
                    // Pick both endpoints of the uncovered edge
                    vertexCover.add(u);
                    vertexCover.add(v);

                    // Mark all incident edges as covered
                    for (Connection c1 : graph.getConnections(u)) {
                        String w = c1.getDestination().getId();
                        coveredEdges.add(u.compareTo(w) < 0 ? u + "-" + w : w + "-" + u);
                    }
                    for (Connection c2 : graph.getConnections(v)) {
                        String w = c2.getDestination().getId();
                        coveredEdges.add(v.compareTo(w) < 0 ? v + "-" + w : w + "-" + v);
                    }
                }
            }
        }
        return vertexCover;
    }
}

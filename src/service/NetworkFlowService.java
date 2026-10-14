package service;

import java.util.*;

/**
 * Implements Module 4 (CO4) Network Flow Algorithms for transport-capacity networks:
 * 1. Edmonds-Karp Algorithm - O(V * E^2) maximum passenger throughput.
 * 2. Dinic's Algorithm - O(V^2 * E) level-graph blocking flow.
 * 3. Min-Cut Analysis - Pinpoints bottleneck track connections separating metro sectors.
 * 4. Maximum Bipartite Matching - Vehicle-to-service schedule assignment.
 */
public class NetworkFlowService {

    public static class FlowEdge {
        public final int u, v;
        public final int capacity;
        public int flow;
        public FlowEdge residual;

        public FlowEdge(int u, int v, int capacity) {
            this.u = u;
            this.v = v;
            this.capacity = capacity;
            this.flow = 0;
        }

        public int remainingCapacity() {
            return capacity - flow;
        }
    }

    public static class FlowNetwork {
        public final int nodeCount;
        public final List<List<FlowEdge>> adj;
        public final Map<String, Integer> nodeToIndex = new HashMap<>();
        public final Map<Integer, String> indexToNode = new HashMap<>();

        public FlowNetwork(int nodeCount) {
            this.nodeCount = nodeCount;
            this.adj = new ArrayList<>(nodeCount);
            for (int i = 0; i < nodeCount; i++) {
                adj.add(new ArrayList<>());
            }
        }

        public void addEdge(int u, int v, int capacity) {
            FlowEdge forward = new FlowEdge(u, v, capacity);
            FlowEdge backward = new FlowEdge(v, u, 0); // Residual reverse edge
            forward.residual = backward;
            backward.residual = forward;
            adj.get(u).add(forward);
            adj.get(v).add(backward);
        }
    }

    // ==========================================
    // 1. Edmonds-Karp Max-Flow Algorithm
    // ==========================================
    public static int edmondsKarpMaxFlow(FlowNetwork net, int source, int sink) {
        int maxFlow = 0;

        while (true) {
            FlowEdge[] parentEdge = new FlowEdge[net.nodeCount];
            Queue<Integer> queue = new ArrayDeque<>();
            queue.add(source);

            while (!queue.isEmpty() && parentEdge[sink] == null) {
                int curr = queue.poll();
                for (FlowEdge edge : net.adj.get(curr)) {
                    if (edge.remainingCapacity() > 0 && parentEdge[edge.v] == null && edge.v != source) {
                        parentEdge[edge.v] = edge;
                        queue.add(edge.v);
                    }
                }
            }

            if (parentEdge[sink] == null) {
                break; // No augmenting path found
            }

            // Find bottleneck capacity along path
            int push = Integer.MAX_VALUE;
            for (FlowEdge edge = parentEdge[sink]; edge != null; edge = parentEdge[edge.u]) {
                push = Math.min(push, edge.remainingCapacity());
            }

            // Augment flow
            for (FlowEdge edge = parentEdge[sink]; edge != null; edge = parentEdge[edge.u]) {
                edge.flow += push;
                edge.residual.flow -= push;
            }

            maxFlow += push;
        }

        return maxFlow;
    }

    // ==========================================
    // 2. Dinic's Algorithm (Level Graph + Blocking Flow)
    // ==========================================
    public static int dinicsMaxFlow(FlowNetwork net, int source, int sink) {
        int maxFlow = 0;
        int[] level = new int[net.nodeCount];

        while (bfsLevelGraph(net, source, sink, level)) {
            int[] ptr = new int[net.nodeCount];
            while (true) {
                int pushed = dfsBlockingFlow(net, source, sink, Integer.MAX_VALUE, level, ptr);
                if (pushed == 0) break;
                maxFlow += pushed;
            }
        }
        return maxFlow;
    }

    private static boolean bfsLevelGraph(FlowNetwork net, int s, int t, int[] level) {
        Arrays.fill(level, -1);
        level[s] = 0;
        Queue<Integer> q = new ArrayDeque<>();
        q.add(s);

        while (!q.isEmpty()) {
            int u = q.poll();
            for (FlowEdge edge : net.adj.get(u)) {
                if (edge.remainingCapacity() > 0 && level[edge.v] < 0) {
                    level[edge.v] = level[u] + 1;
                    q.add(edge.v);
                }
            }
        }
        return level[t] >= 0;
    }

    private static int dfsBlockingFlow(FlowNetwork net, int u, int t, int pushed, int[] level, int[] ptr) {
        if (pushed == 0 || u == t) return pushed;

        for (int cid = ptr[u]; cid < net.adj.get(u).size(); cid++, ptr[u]++) {
            FlowEdge edge = net.adj.get(u).get(cid);
            if (level[edge.v] != level[u] + 1 || edge.remainingCapacity() == 0) continue;

            int tr = dfsBlockingFlow(net, edge.v, t, Math.min(pushed, edge.remainingCapacity()), level, ptr);
            if (tr == 0) continue;

            edge.flow += tr;
            edge.residual.flow -= tr;
            return tr;
        }
        return 0;
    }

    // ==========================================
    // 3. Min-Cut Bottleneck Analysis
    // ==========================================
    public static List<String> findMinCutBottlenecks(FlowNetwork net, int source) {
        boolean[] reachable = new boolean[net.nodeCount];
        Queue<Integer> q = new ArrayDeque<>();
        q.add(source);
        reachable[source] = true;

        while (!q.isEmpty()) {
            int u = q.poll();
            for (FlowEdge edge : net.adj.get(u)) {
                if (edge.remainingCapacity() > 0 && !reachable[edge.v]) {
                    reachable[edge.v] = true;
                    q.add(edge.v);
                }
            }
        }

        List<String> bottleneckCuts = new ArrayList<>();
        for (int u = 0; u < net.nodeCount; u++) {
            if (!reachable[u]) continue;
            for (FlowEdge edge : net.adj.get(u)) {
                if (!reachable[edge.v] && edge.capacity > 0) {
                    String uName = net.indexToNode.getOrDefault(u, "Node_" + u);
                    String vName = net.indexToNode.getOrDefault(edge.v, "Node_" + edge.v);
                    bottleneckCuts.add(String.format("Critical Bottleneck: %s -> %s (Saturated Capacity: %d pax/h)",
                            uName, vName, edge.capacity));
                }
            }
        }
        return bottleneckCuts;
    }

    // ==========================================
    // 4. Maximum Bipartite Matching (Vehicle Assignment)
    // ==========================================
    /**
     * Matches available train rakes (Left) to scheduled trips (Right).
     * Time Complexity: O(V * E)
     */
    public static Map<String, String> computeBipartiteVehicleMatching(
            List<String> trains, List<String> services, Map<String, List<String>> compatibility) {

        Map<String, String> serviceToTrain = new HashMap<>();

        for (String train : trains) {
            Set<String> visited = new HashSet<>();
            dfsBipartiteMatch(train, compatibility, serviceToTrain, visited);
        }

        // Invert to train -> service
        Map<String, String> trainToService = new HashMap<>();
        for (Map.Entry<String, String> entry : serviceToTrain.entrySet()) {
            trainToService.put(entry.getValue(), entry.getKey());
        }
        return trainToService;
    }

    private static boolean dfsBipartiteMatch(String train, Map<String, List<String>> compatibility,
                                             Map<String, String> serviceToTrain, Set<String> visited) {
        List<String> compatibleServices = compatibility.getOrDefault(train, Collections.emptyList());
        for (String service : compatibleServices) {
            if (!visited.contains(service)) {
                visited.add(service);

                if (!serviceToTrain.containsKey(service) || 
                    dfsBipartiteMatch(serviceToTrain.get(service), compatibility, serviceToTrain, visited)) {
                    serviceToTrain.put(service, train);
                    return true;
                }
            }
        }
        return false;
    }
}

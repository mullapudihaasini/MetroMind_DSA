package service;

import model.Connection;
import model.Station;

import java.util.*;

/**
 * Service for computing the optimal route between stations using Dijkstra's Algorithm.
 * Implements a custom PriorityQueue-based shortest path search on the MetroGraph.
 * 
 * Algorithm Complexity:
 * - Time Complexity: O((V + E) log V) where V = number of stations, E = number of connections.
 * - Space Complexity: O(V + E) for distance table, predecessor map, and PriorityQueue heap.
 */
public class RouteService {
    private final MetroGraph graph;

    // Configurable travel time constants
    public static final double AVERAGE_SPEED_KMH = 40.0;    // Average commercial metro cruising speed
    public static final double DWELL_TIME_MINS = 1.5;       // Dwell time per intermediate station
    public static final double INTERCHANGE_PENALTY_MINS = 4.0; // Transfer time walking between lines

    public RouteService(MetroGraph graph) {
        if (graph == null) {
            throw new IllegalArgumentException("MetroGraph cannot be null.");
        }
        this.graph = graph;
    }

    /**
     * Inner helper class for the Dijkstra min-heap.
     */
    private static class NodeRecord implements Comparable<NodeRecord> {
        final String stationId;
        final double distance;

        NodeRecord(String stationId, double distance) {
            this.stationId = stationId;
            this.distance = distance;
        }

        @Override
        public int compareTo(NodeRecord o) {
            return Double.compare(this.distance, o.distance);
        }
    }

    /**
     * Data transfer object encapsulating the computed route result.
     */
    public static class RouteResult {
        private final boolean success;
        private final String message;
        private final Station source;
        private final Station destination;
        private final List<Station> pathStations;
        private final List<Connection> pathSegments;
        private final double totalDistanceKm;
        private final double estimatedMinutes;
        private final int interchangeCount;
        private final List<String> interchangeDetails;

        public RouteResult(boolean success, String message, Station source, Station destination,
                           List<Station> pathStations, List<Connection> pathSegments,
                           double totalDistanceKm, double estimatedMinutes,
                           int interchangeCount, List<String> interchangeDetails) {
            this.success = success;
            this.message = message;
            this.source = source;
            this.destination = destination;
            this.pathStations = pathStations != null ? pathStations : Collections.emptyList();
            this.pathSegments = pathSegments != null ? pathSegments : Collections.emptyList();
            this.totalDistanceKm = Math.round(totalDistanceKm * 100.0) / 100.0;
            this.estimatedMinutes = Math.round(estimatedMinutes * 10.0) / 10.0;
            this.interchangeCount = interchangeCount;
            this.interchangeDetails = interchangeDetails != null ? interchangeDetails : Collections.emptyList();
        }

        public static RouteResult failure(String message) {
            return new RouteResult(false, message, null, null, null, null, 0, 0, 0, null);
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public Station getSource() {
            return source;
        }

        public Station getDestination() {
            return destination;
        }

        public List<Station> getPathStations() {
            return pathStations;
        }

        public List<Connection> getPathSegments() {
            return pathSegments;
        }

        public double getTotalDistanceKm() {
            return totalDistanceKm;
        }

        public double getEstimatedMinutes() {
            return estimatedMinutes;
        }

        public int getStopCount() {
            return pathStations.isEmpty() ? 0 : pathStations.size() - 1;
        }

        public int getInterchangeCount() {
            return interchangeCount;
        }

        public List<String> getInterchangeDetails() {
            return interchangeDetails;
        }
    }

    /**
     * Executes Dijkstra's Shortest Path algorithm from source to destination.
     */
    public RouteResult findShortestRoute(String sourceId, String destId) {
        if (sourceId == null || destId == null) {
            return RouteResult.failure("Source and Destination station IDs cannot be null.");
        }

        String src = sourceId.trim().toUpperCase();
        String dst = destId.trim().toUpperCase();

        Station srcStation = graph.getStation(src);
        Station dstStation = graph.getStation(dst);

        if (srcStation == null) {
            return RouteResult.failure("Source station ID [" + src + "] does not exist in the network.");
        }
        if (dstStation == null) {
            return RouteResult.failure("Destination station ID [" + dst + "] does not exist in the network.");
        }

        // Scenario: Identical source and destination
        if (src.equals(dst)) {
            List<Station> singleStation = Collections.singletonList(srcStation);
            return new RouteResult(true, "Source and destination are identical.",
                    srcStation, dstStation, singleStation, Collections.emptyList(), 0.0, 0.0, 0, Collections.emptyList());
        }

        // Dijkstra's Algorithm Data Structures
        Map<String, Double> distances = new HashMap<>();
        Map<String, Connection> predecessorEdge = new HashMap<>();
        PriorityQueue<NodeRecord> pq = new PriorityQueue<>();
        Set<String> settled = new HashSet<>();

        for (Station s : graph.getAllStations()) {
            distances.put(s.getId(), Double.POSITIVE_INFINITY);
        }

        distances.put(src, 0.0);
        pq.add(new NodeRecord(src, 0.0));

        while (!pq.isEmpty()) {
            NodeRecord current = pq.poll();
            String u = current.stationId;

            if (settled.contains(u)) continue;
            settled.add(u);

            // Early exit once destination is extracted from PriorityQueue
            if (u.equals(dst)) {
                break;
            }

            for (Connection conn : graph.getConnections(u)) {
                String v = conn.getDestination().getId();
                if (settled.contains(v)) continue;

                double newDist = distances.get(u) + conn.getDistance();
                if (newDist < distances.get(v)) {
                    distances.put(v, newDist);
                    predecessorEdge.put(v, conn);
                    pq.add(new NodeRecord(v, newDist));
                }
            }
        }

        // Check if destination is unreachable
        if (!predecessorEdge.containsKey(dst)) {
            return RouteResult.failure("No available metro route found between [" + srcStation.getName() + 
                    "] and [" + dstStation.getName() + "]. Stations belong to disconnected network components.");
        }

        // Reconstruct path in forward order
        LinkedList<Connection> pathSegments = new LinkedList<>();
        LinkedList<Station> pathStations = new LinkedList<>();
        pathStations.addFirst(dstStation);

        String curr = dst;
        while (predecessorEdge.containsKey(curr)) {
            Connection conn = predecessorEdge.get(curr);
            pathSegments.addFirst(conn);
            curr = conn.getSource().getId();
            pathStations.addFirst(conn.getSource());
        }

        double totalDistance = distances.get(dst);

        // Detect line interchanges
        int interchanges = 0;
        List<String> interchangeDetails = new ArrayList<>();
        String currentLine = null;

        for (int i = 0; i < pathSegments.size(); i++) {
            Connection edge = pathSegments.get(i);
            if (currentLine == null) {
                currentLine = edge.getLine();
            } else if (!edge.getLine().equalsIgnoreCase(currentLine)) {
                interchanges++;
                interchangeDetails.add("Transfer at " + edge.getSource().getName() + 
                        " from [" + currentLine + "] to [" + edge.getLine() + "]");
                currentLine = edge.getLine();
            }
        }

        // Travel time calculation:
        // Running time (min) = (distance / speed) * 60
        // Intermediate stops = pathStations.size() - 2 (dwell time each)
        // Interchange penalty = 4 min per line switch
        int intermediateStops = Math.max(0, pathStations.size() - 2);
        double runningMinutes = (totalDistance / AVERAGE_SPEED_KMH) * 60.0;
        double dwellMinutes = intermediateStops * DWELL_TIME_MINS;
        double interchangeMinutes = interchanges * INTERCHANGE_PENALTY_MINS;
        double totalEstimatedMinutes = runningMinutes + dwellMinutes + interchangeMinutes;

        return new RouteResult(true, "Shortest route successfully discovered.",
                srcStation, dstStation, pathStations, pathSegments,
                totalDistance, totalEstimatedMinutes, interchanges, interchangeDetails);
    }
}

package service;

import model.Connection;
import model.Station;

import java.util.*;

/**
 * Graph data structure representing the Metro Station Network.
 * Uses an Adjacency List for space-efficient edge storage: O(V + E).
 * Provides station management, connection validation, BFS/DFS traversals, and network inspection.
 */
public class MetroGraph {
    // Lookup tables for O(1) station retrieval
    private final Map<String, Station> stationsById;
    private final Map<String, Station> stationsByNameLower;
    // Adjacency list representation: Station ID -> List of Outgoing Connections
    private final Map<String, List<Connection>> adjacencyList;
    // Cache of unique metro lines
    private final Set<String> allLines;

    public MetroGraph() {
        this.stationsById = new LinkedHashMap<>();
        this.stationsByNameLower = new HashMap<>();
        this.adjacencyList = new LinkedHashMap<>();
        this.allLines = new HashSet<>();
    }

    /**
     * Adds a new station to the network.
     * Prevents duplicate station IDs or conflicting station entries.
     */
    public synchronized boolean addStation(Station station) {
        if (station == null) {
            throw new IllegalArgumentException("Cannot add a null station.");
        }
        String id = station.getId().toUpperCase();
        if (stationsById.containsKey(id)) {
            return false; // Station with this ID already exists
        }

        stationsById.put(id, station);
        stationsByNameLower.put(station.getName().toLowerCase(), station);
        adjacencyList.putIfAbsent(id, new ArrayList<>());
        allLines.addAll(station.getLines());
        return true;
    }

    /**
     * Adds a connection between two existing stations.
     * Validates distance > 0 and prevents duplicate parallel connections on the same line.
     */
    public synchronized boolean addConnection(String srcId, String destId, double distance, 
                                             String line, int capacity, boolean bidirectional) {
        if (srcId == null || destId == null) {
            throw new IllegalArgumentException("Station IDs cannot be null.");
        }
        String u = srcId.trim().toUpperCase();
        String v = destId.trim().toUpperCase();

        if (u.equals(v)) {
            throw new IllegalArgumentException("Source and destination stations cannot be identical for a connection.");
        }
        if (distance <= 0.0) {
            throw new IllegalArgumentException("Distance must be strictly positive (> 0.0 km).");
        }

        Station sourceStation = stationsById.get(u);
        Station destStation = stationsById.get(v);

        if (sourceStation == null || destStation == null) {
            return false; // One or both stations do not exist
        }

        // Check if connection already exists in u -> v
        if (hasDirectConnection(u, v, line)) {
            return false; // Connection already exists
        }

        Connection forwardConn = new Connection(sourceStation, destStation, distance, line, capacity);
        adjacencyList.get(u).add(forwardConn);
        sourceStation.addLine(line);
        destStation.addLine(line);
        allLines.add(line);

        if (bidirectional) {
            Connection backwardConn = new Connection(destStation, sourceStation, distance, line, capacity);
            adjacencyList.get(v).add(backwardConn);
        }

        return true;
    }

    public boolean addConnection(String srcId, String destId, double distance, String line, boolean bidirectional) {
        return addConnection(srcId, destId, distance, line, 12000, bidirectional);
    }

    /**
     * Checks if a direct connection from src to dest on a specific line exists.
     */
    public boolean hasDirectConnection(String srcId, String destId, String line) {
        List<Connection> edges = adjacencyList.get(srcId.toUpperCase());
        if (edges == null) return false;
        for (Connection c : edges) {
            if (c.getDestination().getId().equalsIgnoreCase(destId) &&
                (line == null || c.getLine().equalsIgnoreCase(line))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if any direct connection exists between two stations regardless of line.
     */
    public boolean hasAnyDirectConnection(String srcId, String destId) {
        return hasDirectConnection(srcId, destId, null);
    }

    public Station getStation(String id) {
        if (id == null) return null;
        return stationsById.get(id.trim().toUpperCase());
    }

    public Station getStationByName(String name) {
        if (name == null) return null;
        return stationsByNameLower.get(name.trim().toLowerCase());
    }

    public boolean containsStation(String id) {
        if (id == null) return false;
        return stationsById.containsKey(id.trim().toUpperCase());
    }

    public Collection<Station> getAllStations() {
        return Collections.unmodifiableCollection(stationsById.values());
    }

    public List<Connection> getConnections(String stationId) {
        if (stationId == null) return Collections.emptyList();
        List<Connection> list = adjacencyList.get(stationId.trim().toUpperCase());
        return list != null ? Collections.unmodifiableList(list) : Collections.emptyList();
    }

    public Map<String, List<Connection>> getAdjacencyList() {
        return Collections.unmodifiableMap(adjacencyList);
    }

    public Set<String> getAllLines() {
        return Collections.unmodifiableSet(allLines);
    }

    public int getStationCount() {
        return stationsById.size();
    }

    /**
     * Calculates the total unique undirected metro connections in the network.
     */
    public int getTotalConnectionsCount() {
        Set<String> uniqueUndirectedEdges = new HashSet<>();
        for (Map.Entry<String, List<Connection>> entry : adjacencyList.entrySet()) {
            for (Connection conn : entry.getValue()) {
                String u = conn.getSource().getId();
                String v = conn.getDestination().getId();
                String edgeKey = u.compareTo(v) < 0 ? u + "<->" + v + "@" + conn.getLine() : v + "<->" + u + "@" + conn.getLine();
                uniqueUndirectedEdges.add(edgeKey);
            }
        }
        return uniqueUndirectedEdges.size();
    }

    /**
     * Breadth-First Search (BFS) graph traversal starting from startId.
     * Time Complexity: O(V + E)
     */
    public List<String> bfsTraversal(String startId) {
        List<String> visitedOrder = new ArrayList<>();
        if (!containsStation(startId)) return visitedOrder;

        Set<String> visited = new HashSet<>();
        Queue<String> queue = new ArrayDeque<>();

        String start = startId.toUpperCase();
        visited.add(start);
        queue.add(start);

        while (!queue.isEmpty()) {
            String curr = queue.poll();
            visitedOrder.add(curr);

            for (Connection conn : getConnections(curr)) {
                String next = conn.getDestination().getId();
                if (!visited.contains(next)) {
                    visited.add(next);
                    queue.add(next);
                }
            }
        }
        return visitedOrder;
    }

    /**
     * Depth-First Search (DFS) graph traversal starting from startId.
     * Time Complexity: O(V + E)
     */
    public List<String> dfsTraversal(String startId) {
        List<String> visitedOrder = new ArrayList<>();
        if (!containsStation(startId)) return visitedOrder;

        Set<String> visited = new HashSet<>();
        dfsHelper(startId.toUpperCase(), visited, visitedOrder);
        return visitedOrder;
    }

    private void dfsHelper(String curr, Set<String> visited, List<String> visitedOrder) {
        visited.add(curr);
        visitedOrder.add(curr);

        for (Connection conn : getConnections(curr)) {
            String next = conn.getDestination().getId();
            if (!visited.contains(next)) {
                dfsHelper(next, visited, visitedOrder);
            }
        }
    }

    /**
     * Checks if destination is reachable from source using BFS.
     */
    public boolean isConnected(String srcId, String destId) {
        if (!containsStation(srcId) || !containsStation(destId)) return false;
        String s = srcId.toUpperCase();
        String d = destId.toUpperCase();
        if (s.equals(d)) return true;

        Set<String> visited = new HashSet<>();
        Queue<String> queue = new ArrayDeque<>();
        visited.add(s);
        queue.add(s);

        while (!queue.isEmpty()) {
            String curr = queue.poll();
            if (curr.equals(d)) return true;
            for (Connection conn : getConnections(curr)) {
                String next = conn.getDestination().getId();
                if (!visited.contains(next)) {
                    visited.add(next);
                    queue.add(next);
                }
            }
        }
        return false;
    }
}

package service;

import model.Connection;
import model.Station;

import java.util.*;

/**
 * Service for fast station discovery and inspection.
 * Utilizes HashMaps for average O(1) direct station lookup by ID and exact name,
 * and linear O(V) scanning for partial-name matching and connected neighbor inspection.
 * 
 * Algorithm Complexity:
 * - Direct ID Lookup: O(1) average via HashMap.
 * - Exact Name Lookup: O(1) average via HashMap.
 * - Partial Name Search: O(V) where V = total stations.
 */
public class StationSearchService {
    private final MetroGraph graph;

    public StationSearchService(MetroGraph graph) {
        if (graph == null) {
            throw new IllegalArgumentException("MetroGraph cannot be null.");
        }
        this.graph = graph;
    }

    /**
     * Finds a station directly by unique station ID in O(1) average time.
     */
    public Station searchById(String stationId) {
        if (stationId == null) return null;
        return graph.getStation(stationId.trim().toUpperCase());
    }

    /**
     * Finds a station directly by exact name in O(1) average time (case-insensitive).
     */
    public Station searchByExactName(String name) {
        if (name == null) return null;
        return graph.getStationByName(name.trim().toLowerCase());
    }

    /**
     * Searches for stations matching a query string by ID or partial name.
     * Time Complexity: O(V)
     */
    public List<Station> searchByPartialQuery(String query) {
        if (query == null || query.trim().isEmpty()) {
            return Collections.emptyList();
        }
        String q = query.trim().toLowerCase();
        List<Station> results = new ArrayList<>();

        for (Station station : graph.getAllStations()) {
            if (station.getId().toLowerCase().contains(q) ||
                station.getName().toLowerCase().contains(q) ||
                station.getPrimaryLine().toLowerCase().contains(q)) {
                results.add(station);
            }
        }
        return results;
    }

    /**
     * Formats detailed station profile including all direct neighbors and distances.
     */
    public String getStationDetailsFormatted(Station station) {
        if (station == null) return "(!) Station not found.";

        StringBuilder sb = new StringBuilder();
        sb.append("====================================================\n");
        sb.append(String.format("STATION PROFILE: %s (ID: %s)\n", station.getName(), station.getId()));
        sb.append("====================================================\n");
        sb.append(String.format("Metro Line(s)      : %s\n", station.getLinesFormatted()));
        sb.append(String.format("Interchange Hub    : %s\n", station.isInterchange() ? "YES" : "NO"));
        
        List<Connection> connections = graph.getConnections(station.getId());
        sb.append(String.format("Connected Neighbors: %d direct connection(s)\n", connections.size()));
        sb.append("----------------------------------------------------\n");
        
        if (connections.isEmpty()) {
            sb.append("  (No outgoing connections registered - isolated station)\n");
        } else {
            for (Connection c : connections) {
                sb.append(String.format("  -> %-16s | Distance: %5.2f km | Line: %-12s | Cap: %d pax/h\n",
                        c.getDestination().getName(), c.getDistance(), c.getLine(), c.getCapacity()));
            }
        }
        sb.append("====================================================");
        return sb.toString();
    }
}

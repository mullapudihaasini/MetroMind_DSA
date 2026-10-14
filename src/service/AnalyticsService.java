package service;

import model.Connection;
import model.JourneyRecord;
import model.Station;

import java.util.*;

/**
 * Service for computing real-time transport analytics and topological network metrics.
 * 
 * Algorithm Complexity:
 * - Station Degree Calculation: O(V + E)
 * - Frequency Map Sorting: O(K log K) where K = unique searched routes
 * - Metric Averages: O(S) where S = journey history records
 */
public class AnalyticsService {
    private final MetroGraph graph;
    private final TrainService trainService;
    private final HistoryService historyService;

    public AnalyticsService(MetroGraph graph, TrainService trainService, HistoryService historyService) {
        if (graph == null || trainService == null || historyService == null) {
            throw new IllegalArgumentException("Dependencies cannot be null.");
        }
        this.graph = graph;
        this.trainService = trainService;
        this.historyService = historyService;
    }

    /**
     * DTO containing all computed analytics metrics.
     */
    public static class AnalyticsReport {
        public final int totalStations;
        public final int totalConnections;
        public final int totalLines;
        public final Set<String> linesList;
        public final List<Map.Entry<Station, Integer>> mostConnectedStations;
        public final int totalTrainSchedules;
        public final int totalSearchesPerformed;
        public final List<Map.Entry<String, Integer>> mostSearchedRoutes;
        public final double averageRouteDistanceKm;
        public final double averageTravelDurationMins;

        public AnalyticsReport(int totalStations, int totalConnections, int totalLines, Set<String> linesList,
                               List<Map.Entry<Station, Integer>> mostConnectedStations,
                               int totalTrainSchedules, int totalSearchesPerformed,
                               List<Map.Entry<String, Integer>> mostSearchedRoutes,
                               double averageRouteDistanceKm, double averageTravelDurationMins) {
            this.totalStations = totalStations;
            this.totalConnections = totalConnections;
            this.totalLines = totalLines;
            this.linesList = linesList;
            this.mostConnectedStations = mostConnectedStations;
            this.totalTrainSchedules = totalTrainSchedules;
            this.totalSearchesPerformed = totalSearchesPerformed;
            this.mostSearchedRoutes = mostSearchedRoutes;
            this.averageRouteDistanceKm = Math.round(averageRouteDistanceKm * 100.0) / 100.0;
            this.averageTravelDurationMins = Math.round(averageTravelDurationMins * 10.0) / 10.0;
        }
    }

    /**
     * Computes the analytics report directly from live in-memory data structures.
     */
    public AnalyticsReport generateReport() {
        int totalStations = graph.getStationCount();
        int totalConnections = graph.getTotalConnectionsCount();
        Set<String> lines = graph.getAllLines();
        int totalLines = lines.size();

        // 1. Calculate station connectivity (degree in graph)
        Map<Station, Integer> degreeMap = new HashMap<>();
        for (Station s : graph.getAllStations()) {
            degreeMap.put(s, graph.getConnections(s.getId()).size());
        }

        List<Map.Entry<Station, Integer>> sortedDegrees = new ArrayList<>(degreeMap.entrySet());
        // Sort descending by degree
        sortedDegrees.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));
        List<Map.Entry<Station, Integer>> topConnected = sortedDegrees.subList(0, Math.min(5, sortedDegrees.size()));

        // 2. Train schedule count
        int totalTrainSchedules = trainService.getTotalSchedulesCount();

        // 3. Journey history statistics
        List<JourneyRecord> history = historyService.getAllRecordsSnapshot();
        int totalSearches = history.size();

        double totalDist = 0.0;
        double totalTime = 0.0;
        Map<String, Integer> routeFrequency = new HashMap<>();

        for (JourneyRecord rec : history) {
            totalDist += rec.getTotalDistanceKm();
            totalTime += rec.getEstimatedMinutes();
            String routeKey = rec.getSourceName() + " -> " + rec.getDestinationName();
            routeFrequency.put(routeKey, routeFrequency.getOrDefault(routeKey, 0) + 1);
        }

        double avgDist = totalSearches > 0 ? (totalDist / totalSearches) : 0.0;
        double avgTime = totalSearches > 0 ? (totalTime / totalSearches) : 0.0;

        List<Map.Entry<String, Integer>> sortedRoutes = new ArrayList<>(routeFrequency.entrySet());
        sortedRoutes.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));
        List<Map.Entry<String, Integer>> topRoutes = sortedRoutes.subList(0, Math.min(5, sortedRoutes.size()));

        return new AnalyticsReport(totalStations, totalConnections, totalLines, lines,
                topConnected, totalTrainSchedules, totalSearches, topRoutes, avgDist, avgTime);
    }

    /**
     * Formats the analytics report for console display.
     */
    public String formatReport(AnalyticsReport r) {
        StringBuilder sb = new StringBuilder();
        sb.append("===============================================================\n");
        sb.append("             METRO MIND - TRANSPORT ANALYTICS REPORT           \n");
        sb.append("===============================================================\n");
        sb.append(String.format(" Total Metro Stations        : %d\n", r.totalStations));
        sb.append(String.format(" Total Metro Connections     : %d (undirected edges)\n", r.totalConnections));
        sb.append(String.format(" Active Metro Lines          : %d %s\n", r.totalLines, r.linesList));
        sb.append(String.format(" Total Train Schedules       : %d\n", r.totalTrainSchedules));
        sb.append("---------------------------------------------------------------\n");
        sb.append(" MOST CONNECTED STATIONS (Transit Hubs / Highest Degree):\n");
        if (r.mostConnectedStations.isEmpty()) {
            sb.append("   (No station connectivity data available)\n");
        } else {
            int rank = 1;
            for (Map.Entry<Station, Integer> entry : r.mostConnectedStations) {
                sb.append(String.format("   %d. %-18s [%s] -> %d direct connection(s)%s\n",
                        rank++, entry.getKey().getName(), entry.getKey().getId(), entry.getValue(),
                        entry.getKey().isInterchange() ? " (Interchange)" : ""));
            }
        }
        sb.append("---------------------------------------------------------------\n");
        sb.append(String.format(" Route Searches in Session   : %d\n", r.totalSearchesPerformed));
        if (r.totalSearchesPerformed == 0) {
            sb.append("   Average Route Distance    : 0.00 km (No searches performed yet)\n");
            sb.append("   Average Travel Duration   : 0.0 mins (No searches performed yet)\n");
            sb.append("   Most Popular Route        : None\n");
        } else {
            sb.append(String.format("   Average Route Distance    : %.2f km\n", r.averageRouteDistanceKm));
            sb.append(String.format("   Average Travel Duration   : %.1f minutes\n", r.averageTravelDurationMins));
            sb.append(" MOST FREQUENTLY SEARCHED ROUTES:\n");
            int rank = 1;
            for (Map.Entry<String, Integer> entry : r.mostSearchedRoutes) {
                sb.append(String.format("   %d. %-32s : %d time(s)\n", rank++, entry.getKey(), entry.getValue()));
            }
        }
        sb.append("===============================================================\n");
        return sb.toString();
    }
}

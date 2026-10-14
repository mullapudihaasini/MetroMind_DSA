import model.*;
import service.*;
import test.TestRunner;
import util.InputValidator;
import util.SampleData;

import java.math.BigInteger;
import java.util.*;

/**
 * Metro Mind – Intelligent Public Transport Platform
 * Main Console Application Entry Point.
 * 
 * Demonstrates:
 * - Graphs & Adjacency Lists
 * - Dijkstra's Shortest Path Algorithm
 * - Min-Heap PriorityQueues for Train Scheduling
 * - Stacks for Journey History (LIFO)
 * - HashMaps for O(1) Station Lookups
 * - String Algorithms (KMP, Z-Function, Rabin-Karp, Aho-Corasick)
 * - Suffix Structures (Suffix Array, Kasai LCP, Suffix Automaton)
 * - Advanced Dynamic Programming (Levenshtein, Bitmask DP, MCM, OBST)
 * - Network Flow (Edmonds-Karp, Dinic's, Min-Cut, Bipartite Matching)
 * - NP-Completeness & Approximation (DPLL SAT Solver, 2-Approx Vertex Cover)
 * - Randomized & Parallel Algorithms (QuickSort, Reservoir Sampling, Blelloch Scan, Parallel Reduce)
 */
public class MetroMind {

    private final MetroGraph graph;
    private final RouteService routeService;
    private final TrainService trainService;
    private final HistoryService historyService;
    private final StationSearchService searchService;
    private final AnalyticsService analyticsService;
    private final Scanner scanner;

    public MetroMind() {
        this.graph = new MetroGraph();
        this.routeService = new RouteService(graph);
        this.trainService = new TrainService();
        this.historyService = new HistoryService();
        this.searchService = new StationSearchService(graph);
        this.analyticsService = new AnalyticsService(graph, trainService, historyService);
        this.scanner = new Scanner(System.in);

        // Preload fictional sample data
        SampleData.populateMetroNetwork(graph);
        SampleData.populateTrainSchedules(trainService);
    }

    public static void main(String[] args) {
        MetroMind app = new MetroMind();
        app.run();
    }

    public void run() {
        printBanner();

        boolean running = true;
        while (running) {
            displayMainMenu();
            int choice = InputValidator.readIntChoice(scanner, 1, 14);
            System.out.println();

            switch (choice) {
                case 1 -> handleViewAllStations();
                case 2 -> handleSearchStation();
                case 3 -> handleViewConnections();
                case 4 -> handleFindShortestRoute();
                case 5 -> handleAddStation();
                case 6 -> handleAddConnection();
                case 7 -> handleAddTrainSchedule();
                case 8 -> handleViewUpcomingTrains();
                case 9 -> handleViewJourneyHistory();
                case 10 -> handleClearJourneyHistory();
                case 11 -> handleViewAnalytics();
                case 12 -> handleAdvancedDsaLab();
                case 13 -> handleRunAutomatedTests();
                case 14 -> {
                    System.out.println("Thank you for using Metro Mind - Intelligent Public Transport Platform.");
                    System.out.println("Exiting safely... Goodbye!");
                    running = false;
                }
            }
            if (running) {
                System.out.println("\nPress [Enter] to return to the Main Menu...");
                scanner.nextLine();
            }
        }
        scanner.close();
    }

    private void printBanner() {
        System.out.println("=========================================================================");
        System.out.println("               METRO MIND - INTELLIGENT PUBLIC TRANSPORT PLATFORM        ");
        System.out.println("                     Academic DSA Demonstration System                   ");
        System.out.println("=========================================================================");
        System.out.println(" [Notice: Network layout and timetables are fictional sample data for DSA testing]");
    }

    private void displayMainMenu() {
        System.out.println("\n=========================================================================");
        System.out.println("                               MAIN MENU                                 ");
        System.out.println("=========================================================================");
        System.out.println("  1.  View All Metro Stations");
        System.out.println("  2.  Search for a Station (Exact, Partial & Fuzzy Autocomplete)");
        System.out.println("  3.  View Metro Network Connections (Adjacency List & BFS/DFS)");
        System.out.println("  4.  Find Shortest Route (Dijkstra's Algorithm)");
        System.out.println("  5.  Add Metro Station");
        System.out.println("  6.  Add Station Connection (Track Edge)");
        System.out.println("  7.  Add Train Schedule");
        System.out.println("  8.  View Upcoming Trains (PriorityQueue Min-Heap Timetable)");
        System.out.println("  9.  View Journey History (Stack - LIFO Order)");
        System.out.println("  10. Clear Journey History");
        System.out.println("  11. View Public Transport Analytics");
        System.out.println("  12. Advanced DSA Lab (CO1-CO6 Algorithms from Syllabus)");
        System.out.println("  13. Run Automated System Tests (DSA Verification Suite)");
        System.out.println("  14. Exit");
        System.out.println("=========================================================================");
    }

    // ---------------------------------------------------------
    // 1. View All Metro Stations
    // ---------------------------------------------------------
    private void handleViewAllStations() {
        System.out.println("--- ALL METRO STATIONS IN NETWORK ---");
        System.out.printf("%-6s | %-20s | %-16s | %-12s | %-10s\n", "ID", "Station Name", "Primary Line", "Interchange?", "Connections");
        System.out.println("-------------------------------------------------------------------------");

        List<Station> stations = new ArrayList<>(graph.getAllStations());
        stations.sort(Comparator.comparing(Station::getId));

        for (Station s : stations) {
            int conns = graph.getConnections(s.getId()).size();
            System.out.printf("%-6s | %-20s | %-16s | %-12s | %-10d\n",
                    s.getId(), s.getName(), s.getPrimaryLine(),
                    s.isInterchange() ? "YES (" + s.getLines().size() + ")" : "NO", conns);
        }
        System.out.println("-------------------------------------------------------------------------");
        System.out.println("Total Stations Registered: " + stations.size());
    }

    // ---------------------------------------------------------
    // 2. Search for a Station
    // ---------------------------------------------------------
    private void handleSearchStation() {
        System.out.println("--- SEARCH FOR A METRO STATION ---");
        String query = InputValidator.readNonEmptyString(scanner, "Enter Station ID or Name to search: ");

        // Step 1: Direct lookup
        Station direct = searchService.searchById(query);
        if (direct == null) {
            direct = searchService.searchByExactName(query);
        }

        if (direct != null) {
            System.out.println("\n[Match Found via Direct HashMap Lookup O(1)]");
            System.out.println(searchService.getStationDetailsFormatted(direct));
            return;
        }

        // Step 2: Partial name lookup
        List<Station> partialMatches = searchService.searchByPartialQuery(query);
        if (!partialMatches.isEmpty()) {
            System.out.println("\n[Partial Matches Found via Linear Scan O(V)] (" + partialMatches.size() + " matches):");
            for (Station s : partialMatches) {
                System.out.println(searchService.getStationDetailsFormatted(s));
            }
            return;
        }

        // Step 3: Fuzzy typo-tolerant search via Damerau-Levenshtein
        System.out.println("(!) No exact or partial match found for: \"" + query + "\"");
        List<Map.Entry<Station, Integer>> fuzzy = DynamicProgrammingService.findFuzzyMatches(graph.getAllStations(), query, 3);
        if (!fuzzy.isEmpty()) {
            System.out.println("\nDid you mean one of these stations? (Damerau-Levenshtein Fuzzy Match):");
            for (Map.Entry<Station, Integer> entry : fuzzy) {
                System.out.printf("  - %s [%s] (Edit Distance: %d)\n",
                        entry.getKey().getName(), entry.getKey().getId(), entry.getValue());
            }
        }
    }

    // ---------------------------------------------------------
    // 3. View Metro Network Connections
    // ---------------------------------------------------------
    private void handleViewConnections() {
        System.out.println("--- METRO NETWORK CONNECTIONS (ADJACENCY LIST) ---");
        for (Station s : graph.getAllStations()) {
            List<Connection> edges = graph.getConnections(s.getId());
            System.out.printf("[%s] %-18s -> %d connection(s)\n", s.getId(), s.getName(), edges.size());
            for (Connection c : edges) {
                System.out.printf("      ==> %-16s | Distance: %5.2f km | Line: %-12s | Cap: %d pax/h\n",
                        c.getDestination().getName(), c.getDistance(), c.getLine(), c.getCapacity());
            }
        }
        System.out.println("-----------------------------------------------------------------");
        System.out.println("Total Unique Undirected Connections: " + graph.getTotalConnectionsCount());

        System.out.print("\nWould you like to run Graph Traversals from a station? (Y/N): ");
        String ans = scanner.nextLine().trim();
        if (ans.equalsIgnoreCase("Y")) {
            String startId = InputValidator.readNonEmptyString(scanner, "Enter starting Station ID (e.g. ST01): ");
            Station startStation = graph.getStation(startId);
            if (startStation != null) {
                System.out.println("\nBFS Traversal Order O(V + E) from " + startStation.getName() + ":");
                List<String> bfs = graph.bfsTraversal(startId);
                for (String id : bfs) {
                    System.out.print(graph.getStation(id).getName() + " -> ");
                }
                System.out.println("[END]");

                System.out.println("\nDFS Traversal Order O(V + E) from " + startStation.getName() + ":");
                List<String> dfs = graph.dfsTraversal(startId);
                for (String id : dfs) {
                    System.out.print(graph.getStation(id).getName() + " -> ");
                }
                System.out.println("[END]");
            } else {
                System.out.println("(!) Station ID not found.");
            }
        }
    }

    // ---------------------------------------------------------
    // 4. Find Shortest Route (Dijkstra)
    // ---------------------------------------------------------
    private void handleFindShortestRoute() {
        System.out.println("--- FIND SHORTEST ROUTE (DIJKSTRA'S ALGORITHM) ---");
        String srcInput = InputValidator.readNonEmptyString(scanner, "Enter Source Station (ID or Name): ");
        String dstInput = InputValidator.readNonEmptyString(scanner, "Enter Destination Station (ID or Name): ");

        Station src = resolveStationInput(srcInput);
        Station dst = resolveStationInput(dstInput);

        if (src == null) {
            System.out.println("(!) Error: Source station \"" + srcInput + "\" not found.");
            return;
        }
        if (dst == null) {
            System.out.println("(!) Error: Destination station \"" + dstInput + "\" not found.");
            return;
        }

        RouteService.RouteResult res = routeService.findShortestRoute(src.getId(), dst.getId());

        if (!res.isSuccess()) {
            System.out.println("\n(!) Route Search Result: " + res.getMessage());
            return;
        }

        System.out.println("\n=========================================================================");
        System.out.println("                        OPTIMAL ROUTE ITINERARY                          ");
        System.out.println("=========================================================================");
        System.out.printf("Origin      : %s [%s]\n", res.getSource().getName(), res.getSource().getId());
        System.out.printf("Destination : %s [%s]\n", res.getDestination().getName(), res.getDestination().getId());
        System.out.printf("Total Dist. : %.2f km\n", res.getTotalDistanceKm());
        System.out.printf("Est. Time   : %.1f minutes (Running + Dwell + Transfers)\n", res.getEstimatedMinutes());
        System.out.printf("Total Stops : %d station stops\n", res.getStopCount());
        System.out.printf("Interchanges: %d line transfer(s)\n", res.getInterchangeCount());
        System.out.println("-------------------------------------------------------------------------");

        if (res.getPathSegments().isEmpty()) {
            System.out.println("You are already at your destination: " + res.getSource().getName());
        } else {
            System.out.println("STEP-BY-STEP TRAVEL INSTRUCTIONS:");
            int step = 1;
            for (Connection conn : res.getPathSegments()) {
                System.out.printf("  %2d. Board [%-12s] at %-18s -> Ride to %-18s (%.2f km)\n",
                        step++, conn.getLine(), conn.getSource().getName(), conn.getDestination().getName(), conn.getDistance());
            }

            if (!res.getInterchangeDetails().isEmpty()) {
                System.out.println("\nLINE TRANSFER NOTICE(S):");
                for (String transfer : res.getInterchangeDetails()) {
                    System.out.println("  * " + transfer);
                }
            }
        }
        System.out.println("-------------------------------------------------------------------------");
        System.out.println("Algorithm Note: Dijkstra minimizes cumulative physical track distance (km).");
        System.out.println("=========================================================================");

        // Push to History Stack
        List<String> pathNames = new ArrayList<>();
        for (Station s : res.getPathStations()) pathNames.add(s.getName());
        List<String> lines = new ArrayList<>();
        for (Connection c : res.getPathSegments()) lines.add(c.getLine());

        JourneyRecord record = new JourneyRecord(
                src.getId(), src.getName(), dst.getId(), dst.getName(),
                pathNames, lines, res.getTotalDistanceKm(), res.getEstimatedMinutes(),
                res.getStopCount(), res.getInterchangeCount());
        historyService.pushSearch(record);
        System.out.println("[Search recorded to session Journey History Stack]");
    }

    private Station resolveStationInput(String input) {
        if (input == null) return null;
        String trimmed = input.trim();
        Station s = graph.getStation(trimmed);
        if (s != null) return s;
        return graph.getStationByName(trimmed);
    }

    // ---------------------------------------------------------
    // 5. Add Metro Station
    // ---------------------------------------------------------
    private void handleAddStation() {
        System.out.println("--- ADD NEW METRO STATION ---");
        String id = InputValidator.readNonEmptyString(scanner, "Enter Station ID (e.g. ST15): ").toUpperCase();
        if (graph.containsStation(id)) {
            System.out.println("(!) Error: Station ID [" + id + "] already exists in the network.");
            return;
        }

        String name = InputValidator.readNonEmptyString(scanner, "Enter Station Name: ");
        if (graph.getStationByName(name) != null) {
            System.out.println("(!) Error: A station with name \"" + name + "\" already exists.");
            return;
        }

        String line = InputValidator.readNonEmptyString(scanner, "Enter Primary Metro Line (e.g. Silver Line): ");

        Station station = new Station(id, name, line);
        boolean success = graph.addStation(station);
        if (success) {
            System.out.println("\n[SUCCESS] Station " + station + " added to the network.");
        } else {
            System.out.println("\n(!) Failed to add station.");
        }
    }

    // ---------------------------------------------------------
    // 6. Add Station Connection
    // ---------------------------------------------------------
    private void handleAddConnection() {
        System.out.println("--- ADD METRO CONNECTION (TRACK EDGE) ---");
        String srcId = InputValidator.readNonEmptyString(scanner, "Enter Source Station ID: ").toUpperCase();
        if (!graph.containsStation(srcId)) {
            System.out.println("(!) Error: Source station [" + srcId + "] does not exist.");
            return;
        }

        String dstId = InputValidator.readNonEmptyString(scanner, "Enter Destination Station ID: ").toUpperCase();
        if (!graph.containsStation(dstId)) {
            System.out.println("(!) Error: Destination station [" + dstId + "] does not exist.");
            return;
        }

        if (srcId.equals(dstId)) {
            System.out.println("(!) Error: Cannot connect a station to itself.");
            return;
        }

        double distance = InputValidator.readPositiveDouble(scanner, "Enter track distance in km (must be > 0.0): ");
        String line = InputValidator.readNonEmptyString(scanner, "Enter Metro Line name: ");

        System.out.print("Is this connection bidirectional? (Y/N, default Y): ");
        String biInput = scanner.nextLine().trim();
        boolean bidirectional = !biInput.equalsIgnoreCase("N");

        boolean success = graph.addConnection(srcId, dstId, distance, line, bidirectional);
        if (success) {
            System.out.printf("\n[SUCCESS] Connection added between %s and %s (%.2f km, Line: %s, Bidirectional: %s)\n",
                    graph.getStation(srcId).getName(), graph.getStation(dstId).getName(), distance, line, bidirectional);
        } else {
            System.out.println("\n(!) Error: Failed to add connection. A direct connection on this line already exists.");
        }
    }

    // ---------------------------------------------------------
    // 7. Add Train Schedule
    // ---------------------------------------------------------
    private void handleAddTrainSchedule() {
        System.out.println("--- ADD TRAIN SCHEDULE ---");
        String trainId = InputValidator.readNonEmptyString(scanner, "Enter Train ID (e.g. TR-501): ").toUpperCase();
        String line = InputValidator.readNonEmptyString(scanner, "Enter Metro Line: ");
        String srcId = InputValidator.readNonEmptyString(scanner, "Enter Origin Station ID: ").toUpperCase();
        String dstId = InputValidator.readNonEmptyString(scanner, "Enter Destination Station ID: ").toUpperCase();

        Station src = graph.getStation(srcId);
        Station dst = graph.getStation(dstId);

        String srcName = (src != null) ? src.getName() : srcId;
        String dstName = (dst != null) ? dst.getName() : dstId;

        String depTime = InputValidator.readValidTime(scanner, "Enter Departure Time");

        TrainSchedule schedule = new TrainSchedule(trainId, line, srcId, srcName, dstId, dstName, depTime);
        boolean success = trainService.addSchedule(schedule);
        if (success) {
            System.out.println("\n[SUCCESS] Schedule added to PriorityQueue Min-Heap: " + schedule);
        } else {
            System.out.println("\n(!) Error: Train ID [" + trainId + "] already exists in active schedule registry.");
        }
    }

    // ---------------------------------------------------------
    // 8. View Upcoming Trains
    // ---------------------------------------------------------
    private void handleViewUpcomingTrains() {
        System.out.println("--- UPCOMING TRAIN TIMETABLE (PRIORITYQUEUE MIN-HEAP) ---");
        if (trainService.isEmpty()) {
            System.out.println("(!) No train schedules currently registered.");
            return;
        }

        TrainSchedule next = trainService.getNextTrain();
        System.out.println(">>> NEXT AVAILABLE DEPARTURE (Heap Min): " + next);
        System.out.println("---------------------------------------------------------------------------------");

        System.out.println("Filter Options: [1] All Trains Chronological  [2] Filter by Destination  [3] Check Headway Conflicts");
        int opt = InputValidator.readIntChoice(scanner, 1, 3);

        if (opt == 1) {
            List<TrainSchedule> all = trainService.getUpcomingTrains();
            System.out.printf("%-8s | %-12s | %-16s -> %-16s | %-8s | %-10s\n",
                    "Train ID", "Line", "Origin", "Destination", "Dep.Time", "Status");
            System.out.println("---------------------------------------------------------------------------------");
            for (TrainSchedule s : all) {
                System.out.printf("%-8s | %-12s | %-16s -> %-16s | %-8s | %-10s\n",
                        s.getTrainId(), s.getLine(), s.getSourceStationName(), s.getDestinationStationName(), s.getDepartureTime(), s.getStatus());
            }
        } else if (opt == 2) {
            String dest = InputValidator.readNonEmptyString(scanner, "Enter destination keyword: ");
            List<TrainSchedule> filtered = trainService.getSchedulesByDestination(dest);
            if (filtered.isEmpty()) {
                System.out.println("(!) No scheduled departures found for destination: " + dest);
            } else {
                for (TrainSchedule s : filtered) System.out.println("  " + s);
            }
        } else {
            System.out.println("Analyzing headway buffer (minimum 4-minute gap on same line)...");
            List<String> conflicts = trainService.detectHeadwayConflicts(4);
            if (conflicts.isEmpty()) {
                System.out.println("[OK] All scheduled departures satisfy safe headway intervals.");
            } else {
                for (String c : conflicts) System.out.println("(!) " + c);
            }
        }
    }

    // ---------------------------------------------------------
    // 9. View Journey History
    // ---------------------------------------------------------
    private void handleViewJourneyHistory() {
        System.out.println("--- RECENT JOURNEY SEARCH HISTORY (STACK - LIFO ORDER) ---");
        if (historyService.isEmpty()) {
            System.out.println("(!) Journey history stack is empty. No searches performed this session.");
            return;
        }

        List<JourneyRecord> history = historyService.getRecentSearches();
        System.out.printf("Total Sessions Searches: %d (Most Recent on Top)\n", history.size());
        System.out.println("=========================================================================");
        int idx = 1;
        for (JourneyRecord rec : history) {
            System.out.printf("#%d: %s\n", idx++, rec);
            System.out.println("-------------------------------------------------------------------------");
        }
    }

    // ---------------------------------------------------------
    // 10. Clear Journey History
    // ---------------------------------------------------------
    private void handleClearJourneyHistory() {
        System.out.println("--- CLEAR JOURNEY HISTORY ---");
        if (historyService.isEmpty()) {
            System.out.println("Journey history is already empty.");
            return;
        }
        historyService.clearHistory();
        System.out.println("[SUCCESS] Journey history stack has been emptied.");
    }

    // ---------------------------------------------------------
    // 11. View Public Transport Analytics
    // ---------------------------------------------------------
    private void handleViewAnalytics() {
        AnalyticsService.AnalyticsReport report = analyticsService.generateReport();
        System.out.println(analyticsService.formatReport(report));
    }

    // ---------------------------------------------------------
    // 12. Advanced DSA Lab (CO1 - CO6)
    // ---------------------------------------------------------
    private void handleAdvancedDsaLab() {
        boolean inLab = true;
        while (inLab) {
            System.out.println("\n=========================================================================");
            System.out.println("               ADVANCED DSA ALGORITHM LAB (CO1 - CO6)                    ");
            System.out.println("=========================================================================");
            System.out.println("  1. CO1 - String Algorithms (KMP, Z-Function, Rabin-Karp & Aho-Corasick)");
            System.out.println("  2. CO2 - Suffix Structures (Suffix Array, Kasai LCP & Suffix Automaton)");
            System.out.println("  3. CO3 - Advanced Dynamic Programming (Fuzzy Levenshtein, Bitmask DP, MCM, OBST)");
            System.out.println("  4. CO4 - Network Flow (Edmonds-Karp, Dinic's Max-Flow, Min-Cut & Bipartite Matching)");
            System.out.println("  5. CO5 - NP-Completeness & Approximation (DPLL SAT Solver & 2-Approx Vertex Cover)");
            System.out.println("  6. CO6 - Randomized & Parallel Algorithms (Blelloch Scan, Parallel Reduce, Reservoir Sampling, Miller-Rabin)");
            System.out.println("  7. Return to Main Menu");
            System.out.println("=========================================================================");

            int labChoice = InputValidator.readIntChoice(scanner, 1, 7);
            System.out.println();

            switch (labChoice) {
                case 1 -> runCo1StringLab();
                case 2 -> runCo2SuffixLab();
                case 3 -> runCo3DpLab();
                case 4 -> runCo4FlowLab();
                case 5 -> runCo5NpLab();
                case 6 -> runCo6ParallelRandLab();
                case 7 -> inLab = false;
            }
            if (inLab) {
                System.out.println("\nPress [Enter] to return to the Advanced DSA Lab Menu...");
                scanner.nextLine();
            }
        }
    }

    private void runCo1StringLab() {
        System.out.println(">>> [CO1] STRING ALGORITHMS IN PUBLIC TRANSPORT <<<");
        List<ServiceAlert> alerts = SampleData.getSampleServiceAlerts();

        System.out.println("1. Knuth-Morris-Pratt (KMP) exact alert search O(N + M):");
        String text = alerts.get(0).getMessage();
        String pattern = "maintenance";
        List<Integer> matches = StringAlgorithmService.kmpSearch(text, pattern);
        System.out.printf("   Text: \"%s\"\n", text);
        System.out.printf("   Pattern: \"%s\" -> Matched at index: %s\n", pattern, matches);

        System.out.println("\n2. Z-Algorithm O(N) periodic repeat pattern detection:");
        String logEntry = "RED_LINE_ALERT_RED_LINE_ALERT_RED_LINE_UPDATE";
        String pat = "RED_LINE_ALERT";
        List<Integer> zMatches = StringAlgorithmService.findRepeatedPatternsZ(logEntry, pat);
        System.out.printf("   Log: \"%s\"\n", logEntry);
        System.out.printf("   Repeated Pattern \"%s\" found at indices: %s\n", pat, zMatches);

        System.out.println("\n3. Rabin-Karp polynomial rolling hash code verification:");
        String serviceCodeText = "SERVICE_VERIFICATION_PASS_HASH_TR9924_AUTHORIZED";
        String code = "TR9924";
        List<Integer> rkMatches = StringAlgorithmService.rabinKarpSearch(serviceCodeText, code);
        System.out.printf("   Service Document: \"%s\"\n", serviceCodeText);
        System.out.printf("   Code \"%s\" found at index: %s\n", code, rkMatches);

        System.out.println("\n4. Aho-Corasick Multi-Pattern Automaton:");
        StringAlgorithmService.AhoCorasickMatcher ac = new StringAlgorithmService.AhoCorasickMatcher();
        ac.addPattern("signal");
        ac.addPattern("maintenance");
        ac.addPattern("overcrowding");
        ac.addPattern("delay");
        ac.buildFailureLinks();

        System.out.println("   Keywords monitored: [signal, maintenance, overcrowding, delay]");
        for (ServiceAlert alert : alerts) {
            Map<String, List<Integer>> found = ac.searchAll(alert.getMessage());
            if (!found.isEmpty()) {
                System.out.printf("   Alert [%s]: Keywords matched: %s\n", alert.getAlertId(), found.keySet());
            }
        }
    }

    private void runCo2SuffixLab() {
        System.out.println(">>> [CO2] SUFFIX STRUCTURES - DOCUMENT INDEXING & SIMILARITY <<<");
        String routeDoc = "CENTRAL_TO_AIRPORT_VIA_RED_LINE_CENTRAL_TO_AIRPORT_EXPRESS";

        System.out.println("1. Suffix Array Construction for Route Document:");
        Integer[] sa = SuffixStructureService.buildSuffixArray(routeDoc);
        System.out.println("   First 5 Sorted Suffixes (Lexicographical order):");
        for (int i = 0; i < Math.min(5, sa.length); i++) {
            System.out.printf("   SA[%d] = %2d -> \"%s\"\n", i, sa[i], routeDoc.substring(sa[i]));
        }

        System.out.println("\n2. Kasai's LCP Algorithm O(N) - Identifying Longest Shared Description:");
        String longestRepeat = SuffixStructureService.findLongestRepeatedSubstring(routeDoc);
        System.out.printf("   Longest Common Substring identified: \"%s\" (Length: %d)\n",
                longestRepeat, longestRepeat.length());

        System.out.println("\n3. Suffix Automaton (DAWG) linear-size substring retrieval:");
        SuffixStructureService.SuffixAutomaton sam = new SuffixStructureService.SuffixAutomaton(routeDoc);
        System.out.printf("   Automaton built with %d linear states.\n", sam.getStateCount());
        System.out.println("   Query 'AIRPORT_EXPRESS' exists? " + sam.containsSubstring("AIRPORT_EXPRESS"));
        System.out.println("   Query 'GREEN_LINE' exists?      " + sam.containsSubstring("GREEN_LINE"));
    }

    private void runCo3DpLab() {
        System.out.println(">>> [CO3] ADVANCED DYNAMIC PROGRAMMING IN TRANSPORT <<<");

        System.out.println("1. Damerau-Levenshtein Fuzzy Autocomplete:");
        String typo = "Centrl";
        List<Map.Entry<Station, Integer>> fuzzy = DynamicProgrammingService.findFuzzyMatches(graph.getAllStations(), typo, 2);
        System.out.printf("   Query: \"%s\" -> Closest Matches:\n", typo);
        for (Map.Entry<Station, Integer> e : fuzzy) {
            System.out.printf("     - %s (Edit Distance: %d)\n", e.getKey().getName(), e.getValue());
        }

        System.out.println("\n2. Bitmask DP - Multi-Stop Maintenance Inspection Tour:");
        List<Station> inspection = Arrays.asList(
                graph.getStation("ST01"), // Central
                graph.getStation("ST04"), // University
                graph.getStation("ST06"), // Airport
                graph.getStation("ST09")  // Stadium
        );
        // Compute pairwise shortest paths
        int k = inspection.size();
        double[][] distMatrix = new double[k][k];
        for (int i = 0; i < k; i++) {
            for (int j = 0; j < k; j++) {
                if (i == j) distMatrix[i][j] = 0;
                else {
                    RouteService.RouteResult rr = routeService.findShortestRoute(inspection.get(i).getId(), inspection.get(j).getId());
                    distMatrix[i][j] = rr.getTotalDistanceKm();
                }
            }
        }
        DynamicProgrammingService.BitmaskTourResult tour = DynamicProgrammingService.computeOptimalMultiStopTour(inspection, distMatrix);
        System.out.printf("   Optimal Tour Distance covering all %d stations: %.2f km\n", k, tour.minTotalDistance);
        System.out.print("   Inspection Order: ");
        for (Station s : tour.orderedTour) System.out.print(s.getName() + " -> ");
        System.out.println("[COMPLETED]");

        System.out.println("\n3. Matrix-Chain Multiplication (MCM) for Transition Probability Matrices:");
        int[] dims = {14, 20, 15, 30, 14}; // 4 sector matrices
        DynamicProgrammingService.McmResult mcm = DynamicProgrammingService.computeMatrixChainMultiplication(dims);
        System.out.printf("   Chain dimensions: %s\n", Arrays.toString(dims));
        System.out.printf("   Minimum Scalar Multiplications: %d ops\n", mcm.minOperations);
        System.out.printf("   Optimal Evaluation Parenthesization: %s\n", mcm.parenthesization);

        System.out.println("\n4. Optimal Binary Search Tree (OBST) for Station Search Popularity:");
        List<String> popularStations = Arrays.asList("Airport", "Central", "City Hall", "Stadium");
        double[] probs = {0.35, 0.40, 0.10, 0.15};
        DynamicProgrammingService.ObstResult obst = DynamicProgrammingService.computeOptimalBst(popularStations, probs);
        System.out.printf("   Stations: %s\n", popularStations);
        System.out.printf("   Access Probabilities: %s\n", Arrays.toString(probs));
        System.out.printf("   Minimum Expected Search Cost: %.3f depth units\n", obst.minExpectedCost);
    }

    private void runCo4FlowLab() {
        System.out.println(">>> [CO4] NETWORK FLOW & TRANSPORT CAPACITY <<<");

        System.out.println("1. Building Metro Transit Capacity Flow Network (Central -> Airport):");
        NetworkFlowService.FlowNetwork net = new NetworkFlowService.FlowNetwork(6);
        net.indexToNode.put(0, "Central (ST01)");
        net.indexToNode.put(1, "City Hall (ST02)");
        net.indexToNode.put(2, "University (ST04)");
        net.indexToNode.put(3, "Innovation Hub (ST14)");
        net.indexToNode.put(4, "Tech Park (ST05)");
        net.indexToNode.put(5, "Airport (ST06)");

        // Add line capacities (passengers / hour)
        net.addEdge(0, 1, 16000); // Red Line North
        net.addEdge(0, 3, 22000); // Yellow Line Express
        net.addEdge(1, 2, 15000);
        net.addEdge(2, 4, 18000);
        net.addEdge(3, 5, 22000);
        net.addEdge(4, 5, 18000);

        int maxFlow = NetworkFlowService.dinicsMaxFlow(net, 0, 5);
        System.out.printf("   Maximum Passenger Flow (Dinic's Algorithm): %d passengers/hour\n", maxFlow);

        System.out.println("\n2. Min-Cut Critical Bottleneck Analysis:");
        List<String> cuts = NetworkFlowService.findMinCutBottlenecks(net, 0);
        for (String cut : cuts) {
            System.out.println("   [!] " + cut);
        }

        System.out.println("\n3. Maximum Bipartite Matching (Assigning Train Sets to Services):");
        List<String> trains = Arrays.asList("Rake-101", "Rake-102", "Rake-103", "Rake-104");
        List<String> services = Arrays.asList("Red-Express-08:00", "Blue-Local-08:15", "Yellow-Exp-08:30", "Green-Orb-08:45");
        Map<String, List<String>> compatibility = new HashMap<>();
        compatibility.put("Rake-101", Arrays.asList("Red-Express-08:00", "Yellow-Exp-08:30"));
        compatibility.put("Rake-102", Arrays.asList("Blue-Local-08:15"));
        compatibility.put("Rake-103", Arrays.asList("Yellow-Exp-08:30", "Green-Orb-08:45"));
        compatibility.put("Rake-104", Arrays.asList("Green-Orb-08:45"));

        Map<String, String> matched = NetworkFlowService.computeBipartiteVehicleMatching(trains, services, compatibility);
        System.out.printf("   Optimal Train Rake Assignments (%d matched services):\n", matched.size());
        for (Map.Entry<String, String> m : matched.entrySet()) {
            System.out.printf("     %s ===> %s\n", m.getKey(), m.getValue());
        }
    }

    private void runCo5NpLab() {
        System.out.println(">>> [CO5] NP-COMPLETENESS & APPROXIMATION ALGORITHMS <<<");

        System.out.println(OptimizationService.explainNpHardReductionPipeline());

        System.out.println("1. DPLL SAT Solver for Train Platform Scheduling Constraints:");
        // Let x1 = TR-101 at Platform 1, x2 = TR-102 at Platform 1
        // Clause 1: (x1 v x2) - At least one train assigned
        // Clause 2: (~x1 v ~x2) - At most one train on Platform 1
        OptimizationService.Clause c1 = new OptimizationService.Clause(1, 2);
        OptimizationService.Clause c2 = new OptimizationService.Clause(-1, -2);
        Set<Integer> vars = new HashSet<>(Arrays.asList(1, 2));
        Map<Integer, Boolean> model = OptimizationService.solveDpllSat(Arrays.asList(c1, c2), vars);

        if (model != null) {
            System.out.println("   SAT Solution Found: " + model + " (Schedule constraints conflict-free)");
        } else {
            System.out.println("   UNSAT: Schedule conflict cannot be resolved simultaneously.");
        }

        System.out.println("\n2. Vertex-Cover 2-Approximation for Station Surveillance Coverage:");
        Set<String> cover = OptimizationService.computeVertexCover2Approx(graph);
        System.out.printf("   Minimum Surveillance Checkpoints (2-Approx): %d stations selected:\n", cover.size());
        for (String stId : cover) {
            Station s = graph.getStation(stId);
            System.out.printf("     * [%s] %-18s (%s)\n", stId, s.getName(), s.getPrimaryLine());
        }
        System.out.println("   Theoretical Guarantee: |C| <= 2 * |C*| (at most 2x optimal minimum cover).");
    }

    private void runCo6ParallelRandLab() {
        System.out.println(">>> [CO6] RANDOMIZED & PARALLEL ALGORITHMS IN TRANSPORT <<<");

        System.out.println("1. Randomized QuickSort on Streaming Passenger Events:");
        List<TransitEvent> events = SampleData.getSampleTransitEvents();
        System.out.println("   Before Sorting: " + events.size() + " events.");
        ParallelRandomService.randomizedQuickSort(events, 0, events.size() - 1);
        System.out.println("   After Randomized QuickSort (Sorted by event value/delay):");
        for (TransitEvent e : events) {
            System.out.println("     " + e);
        }

        System.out.println("\n2. Reservoir Sampling (Algorithm R) on Continuous Sensor Stream:");
        List<TransitEvent> sample = ParallelRandomService.reservoirSample(SampleData.getSampleTransitEvents().iterator(), 3);
        System.out.printf("   Selected %d uniform random samples from unbounded event stream:\n", sample.size());
        for (TransitEvent s : sample) {
            System.out.println("     " + s);
        }

        System.out.println("\n3. Miller-Rabin Primality Test for Digital Security Tokens:");
        BigInteger testTokenPrime = BigInteger.valueOf(1000000007L);
        boolean isPrime = ParallelRandomService.millerRabinTest(testTokenPrime, 20);
        System.out.printf("   Token Seed %s is Prime? %s (Verified with 20 rounds, error bound < 4^-20)\n",
                testTokenPrime, isPrime ? "YES (Cryptographically Secure)" : "NO");

        System.out.println("\n4. Blelloch Parallel Work-Efficient Prefix Scan (Cumulative Ridership):");
        long[] hourlyPassengers = {1200, 2400, 3100, 4800, 6200, 5100, 4300, 3900};
        long[] prefixSums = ParallelRandomService.blellochExclusiveScan(hourlyPassengers);
        System.out.println("   Hourly Passenger Counts : " + Arrays.toString(hourlyPassengers));
        System.out.println("   Blelloch Cumulative Scan: " + Arrays.toString(prefixSums));

        System.out.println("\n5. Parallel Reduce & Brent's Theorem Evaluation:");
        long totalPax = ParallelRandomService.executeParallelReduce(hourlyPassengers);
        System.out.printf("   ForkJoin Parallel Reduction Total Passenger Volume: %d\n", totalPax);
        int cores = Runtime.getRuntime().availableProcessors();
        System.out.println(ParallelRandomService.explainBrentsTheorem(1000000, 20, cores));
    }

    // ---------------------------------------------------------
    // 13. Run Automated System Tests
    // ---------------------------------------------------------
    private void handleRunAutomatedTests() {
        System.out.println("Executing Complete Automated DSA Verification Test Suite...\n");
        boolean pass = TestRunner.runAllTests();
        if (pass) {
            System.out.println("\n[SYSTEM HEALTH CHECK: 100% OPERATIONAL]");
        } else {
            System.out.println("\n(!) Warnings or errors encountered during testing.");
        }
    }
}

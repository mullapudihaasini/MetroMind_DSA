package test;

import model.*;
import service.*;
import util.InputValidator;
import util.SampleData;

import java.math.BigInteger;
import java.util.*;

/**
 * Automated Verification Test Runner for Metro Mind.
 * Tests all 15 required functional scenarios from the specification plus the advanced DSA CO1-CO6 modules.
 */
public class TestRunner {

    private static int testsPassed = 0;
    private static int testsFailed = 0;

    public static void main(String[] args) {
        System.out.println("===============================================================");
        System.out.println("       METRO MIND - AUTOMATED DSA VERIFICATION TEST SUITE       ");
        System.out.println("===============================================================");

        runAllTests();

        System.out.println("\n---------------------------------------------------------------");
        System.out.printf("TEST SUMMARY: %d Passed, %d Failed. Success Rate: %.1f%%\n",
                testsPassed, testsFailed, (testsPassed * 100.0) / (testsPassed + testsFailed));
        System.out.println("===============================================================");
        if (testsFailed > 0) {
            System.err.println("(!) Some tests failed. Please review the output above.");
        } else {
            System.out.println("ALL TEST SUITE SCENARIOS COMPLETED SUCCESSFULLY!");
        }
    }

    public static boolean runAllTests() {
        testsPassed = 0;
        testsFailed = 0;

        test1_ValidShortestRoute();
        test2_RouteWithInterchange();
        test3_DisconnectedStationHandling();
        test4_SearchNonexistentStation();
        test5_AddNewStation();
        test6_RejectDuplicateStationId();
        test7_AddValidConnection();
        test8_RejectInvalidConnectionDistance();
        test9_TrainDeparturePriorityQueueOrder();
        test10_RejectInvalidTrainTime();
        test11_JourneyHistoryStackOrder();
        test12_ClearJourneyHistory();
        test13_AnalyticsReportSafety();
        test14_InputValidatorIntegrity();
        test15_AdvancedDsaModuleCO1toCO6();

        return testsFailed == 0;
    }

    private static void assertTrue(String testName, boolean condition, String detail) {
        if (condition) {
            System.out.printf(" [PASS] %-45s | %s\n", testName, detail);
            testsPassed++;
        } else {
            System.err.printf(" [FAIL] %-45s | %s\n", testName, detail);
            testsFailed++;
        }
    }

    private static void test1_ValidShortestRoute() {
        MetroGraph graph = new MetroGraph();
        SampleData.populateMetroNetwork(graph);
        RouteService routeService = new RouteService(graph);

        // Path Central (ST01) to Museum (ST03) on Red Line
        // Central -> City Hall (2.1) -> Museum (1.8) = 3.9 km
        RouteService.RouteResult res = routeService.findShortestRoute("ST01", "ST03");
        assertTrue("Scenario 1: Find Valid Shortest Route",
                res.isSuccess() && Math.abs(res.getTotalDistanceKm() - 3.90) < 0.01,
                "Distance: " + res.getTotalDistanceKm() + " km (Expected: 3.90 km)");
    }

    private static void test2_RouteWithInterchange() {
        MetroGraph graph = new MetroGraph();
        SampleData.populateMetroNetwork(graph);
        RouteService routeService = new RouteService(graph);

        // Terminal (ST07, Blue Line) to Airport (ST06, via Yellow Line from Central)
        // Path: Terminal -> Market -> Central -> Innovation Hub -> Airport
        RouteService.RouteResult res = routeService.findShortestRoute("ST07", "ST06");
        assertTrue("Scenario 2: Route Involving Interchange",
                res.isSuccess() && res.getInterchangeCount() >= 1,
                "Interchanges detected: " + res.getInterchangeCount() + ", Path stops: " + res.getStopCount());
    }

    private static void test3_DisconnectedStationHandling() {
        MetroGraph graph = new MetroGraph();
        SampleData.populateMetroNetwork(graph);
        // Add isolated station
        Station isolated = new Station("ST99", "Isolated Island", "Purple Line");
        graph.addStation(isolated);
        RouteService routeService = new RouteService(graph);

        RouteService.RouteResult res = routeService.findShortestRoute("ST01", "ST99");
        assertTrue("Scenario 3: Handle Disconnected Stations",
                !res.isSuccess() && res.getMessage().contains("disconnected"),
                "Correctly returned unreachable failure: " + res.getMessage());
    }

    private static void test4_SearchNonexistentStation() {
        MetroGraph graph = new MetroGraph();
        SampleData.populateMetroNetwork(graph);
        StationSearchService searchService = new StationSearchService(graph);

        Station res = searchService.searchById("NON_EXISTENT_ID");
        assertTrue("Scenario 4: Search Nonexistent Station",
                res == null,
                "Nonexistent station correctly resolved to null");
    }

    private static void test5_AddNewStation() {
        MetroGraph graph = new MetroGraph();
        SampleData.populateMetroNetwork(graph);
        int initialCount = graph.getStationCount();

        Station newSt = new Station("ST15", "Cyber City", "Silver Line");
        boolean added = graph.addStation(newSt);
        assertTrue("Scenario 5: Add New Station",
                added && graph.getStationCount() == initialCount + 1,
                "New station ST15 added. Total stations: " + graph.getStationCount());
    }

    private static void test6_RejectDuplicateStationId() {
        MetroGraph graph = new MetroGraph();
        SampleData.populateMetroNetwork(graph);

        // ST01 already exists
        Station dup = new Station("ST01", "Duplicate Central", "Red Line");
        boolean added = graph.addStation(dup);
        assertTrue("Scenario 6: Reject Duplicate Station ID",
                !added,
                "Duplicate ID ST01 correctly rejected");
    }

    private static void test7_AddValidConnection() {
        MetroGraph graph = new MetroGraph();
        SampleData.populateMetroNetwork(graph);

        Station stA = new Station("STA1", "Alpha", "Red Line");
        Station stB = new Station("STB1", "Beta", "Red Line");
        graph.addStation(stA);
        graph.addStation(stB);

        boolean connAdded = graph.addConnection("STA1", "STB1", 3.25, "Red Line", true);
        assertTrue("Scenario 7: Add Valid Connection",
                connAdded && graph.hasDirectConnection("STA1", "STB1", "Red Line"),
                "Direct connection Alpha <-> Beta added successfully");
    }

    private static void test8_RejectInvalidConnectionDistance() {
        MetroGraph graph = new MetroGraph();
        SampleData.populateMetroNetwork(graph);

        boolean rejectedNegative = false;
        try {
            graph.addConnection("ST01", "ST04", -5.0, "Red Line", true);
        } catch (IllegalArgumentException e) {
            rejectedNegative = true;
        }

        boolean rejectedZero = false;
        try {
            graph.addConnection("ST01", "ST04", 0.0, "Red Line", true);
        } catch (IllegalArgumentException e) {
            rejectedZero = true;
        }

        assertTrue("Scenario 8: Reject Invalid Distance (<= 0)",
                rejectedNegative && rejectedZero,
                "Negative and zero edge weights strictly prohibited");
    }

    private static void test9_TrainDeparturePriorityQueueOrder() {
        TrainService trainService = new TrainService();
        trainService.addSchedule(new TrainSchedule("TR-B", "Red Line", "ST01", "Central", "ST06", "Airport", "10:30"));
        trainService.addSchedule(new TrainSchedule("TR-A", "Red Line", "ST01", "Central", "ST06", "Airport", "06:15"));
        trainService.addSchedule(new TrainSchedule("TR-C", "Red Line", "ST01", "Central", "ST06", "Airport", "08:45"));

        TrainSchedule next = trainService.getNextTrain();
        List<TrainSchedule> sorted = trainService.getUpcomingTrains();

        assertTrue("Scenario 9: Train Departure PriorityQueue Order",
                next != null && next.getTrainId().equals("TR-A") &&
                sorted.get(0).getDepartureTime().equals("06:15") &&
                sorted.get(1).getDepartureTime().equals("08:45") &&
                sorted.get(2).getDepartureTime().equals("10:30"),
                "PriorityQueue min-heap head: " + (next != null ? next.getTrainId() : "null"));
    }

    private static void test10_RejectInvalidTrainTime() {
        boolean rejectedFormat = false;
        try {
            new TrainSchedule("TR-BAD", "Red Line", "ST01", "Central", "ST02", "City Hall", "25:70");
        } catch (IllegalArgumentException e) {
            rejectedFormat = true;
        }
        assertTrue("Scenario 10: Reject Invalid Train Time",
                rejectedFormat,
                "Invalid time 25:70 rejected by regex validator");
    }

    private static void test11_JourneyHistoryStackOrder() {
        HistoryService history = new HistoryService();
        JourneyRecord rec1 = new JourneyRecord("ST01", "Central", "ST02", "City Hall",
                Arrays.asList("Central", "City Hall"), Collections.singletonList("Red Line"), 2.1, 4.5, 1, 0);
        JourneyRecord rec2 = new JourneyRecord("ST01", "Central", "ST06", "Airport",
                Arrays.asList("Central", "Airport"), Collections.singletonList("Yellow Line"), 11.3, 18.0, 1, 0);

        history.pushSearch(rec1);
        history.pushSearch(rec2);

        List<JourneyRecord> recent = history.getRecentSearches();
        assertTrue("Scenario 11: Journey History LIFO Stack Order",
                recent.size() == 2 && recent.get(0).getDestinationName().equals("Airport"),
                "Top of history is most recent search: " + recent.get(0).getDestinationName());
    }

    private static void test12_ClearJourneyHistory() {
        HistoryService history = new HistoryService();
        history.pushSearch(new JourneyRecord("ST01", "Central", "ST02", "City Hall",
                Arrays.asList("Central", "City Hall"), Collections.singletonList("Red Line"), 2.1, 4.5, 1, 0));
        history.clearHistory();

        assertTrue("Scenario 12: Clear Journey History",
                history.isEmpty() && history.getHistoryCount() == 0,
                "History stack successfully cleared");
    }

    private static void test13_AnalyticsReportSafety() {
        MetroGraph graph = new MetroGraph();
        TrainService trains = new TrainService();
        HistoryService history = new HistoryService();
        AnalyticsService analytics = new AnalyticsService(graph, trains, history);

        // Test with empty dataset (no division by zero!)
        AnalyticsService.AnalyticsReport rEmpty = analytics.generateReport();
        boolean safeEmpty = (rEmpty.averageRouteDistanceKm == 0.0 && rEmpty.averageTravelDurationMins == 0.0);

        SampleData.populateMetroNetwork(graph);
        SampleData.populateTrainSchedules(trains);
        history.pushSearch(new JourneyRecord("ST01", "Central", "ST02", "City Hall",
                Arrays.asList("Central", "City Hall"), Collections.singletonList("Red Line"), 10.0, 20.0, 1, 0));
        AnalyticsService.AnalyticsReport rPopulated = analytics.generateReport();

        assertTrue("Scenario 13: Analytics Real Metrics & Zero Safety",
                safeEmpty && rPopulated.totalStations == 14 && rPopulated.totalTrainSchedules == 10,
                "Total Stations: " + rPopulated.totalStations + ", Train Schedules: " + rPopulated.totalTrainSchedules);
    }

    private static void test14_InputValidatorIntegrity() {
        boolean validTime = InputValidator.isValidTime("14:35");
        boolean invalidTime = !InputValidator.isValidTime("99:99");
        boolean validId = InputValidator.isValidStationId("ST01");
        boolean invalidId = !InputValidator.isValidStationId("?");

        assertTrue("Scenario 14: Input Validator Integrity",
                validTime && invalidTime && validId && invalidId,
                "Time and station ID pattern validators operating strictly");
    }

    private static void test15_AdvancedDsaModuleCO1toCO6() {
        // CO1: KMP & Rabin-Karp & Aho-Corasick
        List<Integer> kmpMatches = StringAlgorithmService.kmpSearch("Track maintenance scheduled between Museum and University", "maintenance");
        List<Integer> rkMatches = StringAlgorithmService.rabinKarpSearch("SERVICE_CODE_TX9982_VERIFIED", "TX9982");
        
        StringAlgorithmService.AhoCorasickMatcher ac = new StringAlgorithmService.AhoCorasickMatcher();
        ac.addPattern("signal");
        ac.addPattern("delay");
        ac.buildFailureLinks();
        Map<String, List<Integer>> acRes = ac.searchAll("Severe signal failure causing unexpected delay on line");

        boolean co1Pass = !kmpMatches.isEmpty() && !rkMatches.isEmpty() && acRes.containsKey("signal") && acRes.containsKey("delay");

        // CO2: Suffix Array, Kasai LCP & Suffix Automaton
        String doc = "CENTRAL_AIRPORT_EXPRESS_CENTRAL_AIRPORT_LOCAL";
        String repeated = SuffixStructureService.findLongestRepeatedSubstring(doc);
        SuffixStructureService.SuffixAutomaton sam = new SuffixStructureService.SuffixAutomaton("METRO_MIND_RAPID_TRANSIT");
        boolean co2Pass = repeated.contains("CENTRAL_AIRPORT_") && sam.containsSubstring("RAPID");

        // CO3: Levenshtein & Bitmask DP Tour & MCM & OBST
        int editDist = DynamicProgrammingService.damerauLevenshteinDistance("Cetnral", "Central"); // 1 transposition
        DynamicProgrammingService.McmResult mcm = DynamicProgrammingService.computeMatrixChainMultiplication(new int[]{10, 20, 30, 40});
        boolean co3Pass = (editDist == 1) && mcm.minOperations > 0;

        // CO4: Dinic's & Edmonds-Karp & Bipartite Matching
        NetworkFlowService.FlowNetwork net = new NetworkFlowService.FlowNetwork(4);
        net.addEdge(0, 1, 10);
        net.addEdge(0, 2, 10);
        net.addEdge(1, 2, 2);
        net.addEdge(1, 3, 10);
        net.addEdge(2, 3, 10);
        int maxFlowDinic = NetworkFlowService.dinicsMaxFlow(net, 0, 3);
        boolean co4Pass = (maxFlowDinic == 20);

        // CO5: SAT DPLL & 2-Approx Vertex Cover
        OptimizationService.Clause c1 = new OptimizationService.Clause(1, 2);
        OptimizationService.Clause c2 = new OptimizationService.Clause(-1, 2);
        Map<Integer, Boolean> satModel = OptimizationService.solveDpllSat(Arrays.asList(c1, c2), new HashSet<>(Arrays.asList(1, 2)));
        boolean co5Pass = satModel != null && Boolean.TRUE.equals(satModel.get(2));

        // CO6: Blelloch Scan, Parallel Reduce & Miller-Rabin
        long[] scanIn = new long[]{1, 2, 3, 4, 5, 6, 7, 8};
        long[] scanned = ParallelRandomService.blellochExclusiveScan(scanIn);
        boolean mrPrime = ParallelRandomService.millerRabinTest(BigInteger.valueOf(1000000007), 10);
        boolean co6Pass = (scanned[7] == 28) && mrPrime;

        assertTrue("Scenario 15: Advanced DSA Modules (CO1 - CO6)",
                co1Pass && co2Pass && co3Pass && co4Pass && co5Pass && co6Pass,
                "CO1 (Strings), CO2 (Suffix), CO3 (DP), CO4 (Flow), CO5 (NP/SAT), CO6 (Parallel/Rand) verified");
    }
}

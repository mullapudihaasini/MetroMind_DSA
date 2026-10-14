# Metro Mind – Intelligent Public Transport Platform
> **Advanced Data Structures & Algorithms (DSA) Academic Capstone Project**  
> *Developed in pure Java 17+ (No external libraries, zero frameworks, 100% in-memory)*

---

## 1. Project Overview & Problem Statement

### 1.1 Problem Statement
Modern urban rail and transit networks are complex systems characterized by multi-line intersections, variable segment lengths, high passenger throughput, and stringent scheduling constraints. Efficient transit operation demands computational solutions for:
- Optimal shortest-path routing between arbitrary transit hubs.
- Chronological departure sequencing and timetable conflict detection.
- Sub-millisecond station and document lookup with typo tolerance.
- Multi-station maintenance tour planning.
- Maximum passenger flow capacity modeling and bottleneck cut detection.
- Fast, secure stream processing and analytics.

### 1.2 Objectives
**Metro Mind** is a standalone, menu-driven Java console application designed to demonstrate the real-world application of foundational and advanced Data Structures and Algorithms (DSA). It models an intelligent public transport platform without reliance on external databases, web frameworks, or third-party pathfinding libraries.

Key project commitments:
1. **Zero External Dependencies:** Built purely using the standard Java Development Kit (JDK 17+).
2. **Algorithmic Rigor:** Hand-crafted implementations of Dijkstra's algorithm, KMP, Z-algorithm, Rabin-Karp, Aho-Corasick, Suffix Arrays, Kasai's LCP, Suffix Automata, Levenshtein DP, Bitmask DP, MCM, Optimal BST, Dinic's & Edmonds-Karp Max-Flow, Min-Cut, Hopcroft-Karp Bipartite Matching, DPLL SAT solver, Vertex Cover 2-Approximation, Randomized QuickSort, Reservoir Sampling, Miller-Rabin test, and Blelloch Parallel Scan.
3. **Robust Engineering:** Complete input validation (immune to scanner crashes, type mismatches, and negative weights), safe zero handling in analytics, and an integrated automated verification test suite.

---

## 2. System Architecture & Project Structure

The project adheres to clean separation of concerns across models, services, utilities, and testing:

```
TRAIN/
├── bin/                          # Compiled Java bytecode (.class files)
├── src/
│   ├── MetroMind.java            # Main console application & interactive menu
│   ├── model/
│   │   ├── Station.java          # Station vertex with multi-line & interchange flags
│   │   ├── Connection.java       # Weighted edge (distance, line, capacity)
│   │   ├── TrainSchedule.java    # Timetable record implementing Comparable
│   │   ├── JourneyRecord.java    # Historical search record stored in Stack
│   │   ├── ServiceAlert.java     # Operational bulletin for string matching
│   │   └── TransitEvent.java     # Sensor/tap event for streaming & parallel tests
│   ├── service/
│   │   ├── MetroGraph.java       # Adjacency list graph with BFS & DFS
│   │   ├── RouteService.java     # Custom Dijkstra shortest-path algorithm
│   │   ├── TrainService.java     # Min-Heap PriorityQueue timetable manager
│   │   ├── HistoryService.java   # Stack-based LIFO search history
│   │   ├── StationSearchService.java  # O(1) HashMap lookup & O(V) partial search
│   │   ├── AnalyticsService.java # Live in-memory graph & session analytics
│   │   ├── StringAlgorithmService.java # KMP, Z-Function, Rabin-Karp, Aho-Corasick (CO1)
│   │   ├── SuffixStructureService.java # Suffix Array, Kasai LCP, Suffix Automaton (CO2)
│   │   ├── DynamicProgrammingService.java # Damerau-Levenshtein, Bitmask DP, MCM, OBST (CO3)
│   │   ├── NetworkFlowService.java     # Edmonds-Karp, Dinic's, Min-Cut, Matching (CO4)
│   │   ├── OptimizationService.java   # DPLL SAT Solver & 2-Approx Vertex Cover (CO5)
│   │   └── ParallelRandomService.java # Blelloch Scan, Brent's Theorem, Reservoir (CO6)
│   ├── util/
│   │   ├── InputValidator.java   # Defensive console parsing & regex validation
│   │   └── SampleData.java       # Fictional 14-station network & timetable seed
│   └── test/
│       └── TestRunner.java       # Automated verification suite (15 core scenarios)
├── build.bat                     # 1-click Windows compilation script
├── run.bat                       # 1-click Windows launch script
├── test.bat                      # 1-click Windows test execution script
└── README.md                     # Comprehensive technical documentation
```

---

## 3. Core Modules & Syllabus Course Outcome (CO) Mapping

### Core Application Modules
- **Module 1: Metro Station Network:** Models stations as vertices and physical track segments as weighted edges using an Adjacency List. Supports bidirectional connectivity, enforces strictly positive distances, and prevents duplicate station IDs or parallel edge collisions.
- **Module 2: Intelligent Route Finder:** Custom Dijkstra shortest-path algorithm utilizing a binary-heap `PriorityQueue`. Accurately reconstructs itineraries, tracks line transfers (interchanges), calculates cumulative distance (km), and estimates travel time using realistic running speed (40 km/h), intermediate dwell times (1.5 min), and transfer overhead (4.0 min).
- **Module 3: Train Scheduling:** Maintains departures inside a Min-Heap `PriorityQueue` ordered chronologically by departure minutes. Supports $O(1)$ next-train retrieval, $O(T \log T)$ sorted timetable viewing, destination filtering, and safe track headway conflict detection.
- **Module 4: Journey History:** Records search itineraries in a `Stack` (LIFO). Displays recent queries in reverse chronological order and supports non-destructive inspections and instantaneous $O(1)$ clearing.
- **Module 5: Station Search:** Provides $O(1)$ average station lookup by unique ID or exact name via `HashMap`, linear $O(V)$ partial substring discovery, and fuzzy typo-tolerant autocomplete.
- **Module 6: Transport Analytics:** Evaluates live in-memory metrics without synthetic mock values: station degree centrality, network hubs, most searched corridors, and average journey durations with zero-division safety.

### Advanced Syllabus Algorithms (Topics from Syllabus Mapping Image)
| Module / Outcome | Topic | Implementation Details | Practical Public Transport Application |
|---|---|---|---|
| **M1 - CO1: String Algorithms** | **KMP Algorithm** | Precomputes LPS ($\pi$) in $O(M)$, linear scan in $O(N)$ | Fast searching of station names and service bulletin texts. |
| | **Z-Function** | Linear $O(N)$ Z-box array | Detects repetitive operational announcements and periodic logs. |
| | **Rabin-Karp** | Polynomial rolling hash with prime modulus $10^9+7$ | Rapid verification of digital service codes and train asset tags. |
| | **Aho-Corasick** | Trie + BFS failure links + dictionary outputs | Single-pass simultaneous scanning of multiple incident keywords. |
| **M2 - CO2: Suffix Structures** | **Suffix Array** | Lexicographically sorted suffix indices | Indexing large corpus of transit operating guidelines. |
| | **Kasai's LCP** | Linear $O(N)$ Longest Common Prefix array | Identifies shared route descriptions and duplicated instructions. |
| | **Suffix Automaton** | Minimal DAWG with linear states ($2N-1$) | Instant $O(\|P\|)$ substring verification in operational documents. |
| **M3 - CO3: Advanced DP** | **Damerau-Levenshtein** | 2D matrix DP with transposition handling | Typo-tolerant fuzzy station autocomplete (e.g., "Centrl" $\to$ "Central"). |
| | **Bitmask DP** | State $dp(\text{mask}, u)$ with bit transitions in $O(K^2 \cdot 2^K)$ | Computes optimal multi-station maintenance/inspection tour. |
| | **Matrix-Chain Mult. (MCM)** | Classical $O(N^3)$ table with parenthesization | Minimizes computation cost of sequential transfer probability matrices. |
| | **Optimal BST (OBST)** | Dynamic programming minimizing $\sum p_i \cdot \text{depth}_i$ | Arranges station lookup hierarchy based on historical search frequencies. |
| **M4 - CO4: Network Flow** | **Edmonds-Karp** | BFS augmenting paths $O(V \cdot E^2)$ | Maximum hourly passenger throughput between line terminals. |
| | **Dinic's Algorithm** | Level graph BFS + blocking flow DFS in $O(V^2 E)$ | High-throughput capacity evaluation on dense transit networks. |
| | **Min-Cut Bottlenecks** | Residual graph reachability cut | Identifies saturated bottleneck track segments limiting passenger flow. |
| | **Bipartite Matching** | Augmenting path matching (Hopcroft-Karp principles) | Assigns available train rakes to scheduled peak-hour service trips. |
| **M5 - CO5: NP-Completeness** | **SAT / 3-SAT (DPLL)** | Backtracking DPLL Boolean constraint solver | Verifies conflict-free platform allocation under exclusivity constraints. |
| | **Reduction Pipeline** | $3\text{-SAT} \to \text{CLIQUE} \to \text{IND-SET} \to \text{VERTEX-COVER}$ | Proves equivalence of service departure conflict resolution to NP-Hard graph problems. |
| | **Vertex Cover 2-Approx** | Maximal matching heuristic $|C| \le 2 \cdot |C^*|$ | Selects minimal station hubs for transit officer/security coverage. |
| **M6 - CO6: Parallel & Random** | **Randomized QuickSort** | Uniformly random pivot selection | Expected $O(N \log N)$ sorting of sensor and delay events without $O(N^2)$ bias. |
| | **Reservoir Sampling** | Algorithm R with probability $k/N$ | Maintains uniform random sample of $k$ events from unbounded event streams. |
| | **Miller-Rabin Test** | Probabilistic test bounded by $(1/4)^k$ error | Validates large prime cryptographic tokens for transit service authorization. |
| | **Blelloch Parallel Scan** | Work-efficient tree Upsweep & Downsweep | Parallel prefix sum for cumulative hourly passenger volume ($O(N)$ work, $O(\log N)$ span). |
| | **Parallel Reduce & Brent's** | Multi-threaded ForkJoin reduction & Brent's Bound | Evaluates $T_p \le (T_1 / p) + T_\infty$ for parallel transit statistics aggregation. |

---

## 4. Data Structures & Selection Rationale

| Data Structure | Implementation Used | Time Complexity | Selection Rationale |
|---|---|---|---|
| **Graph** | Adjacency List (`Map<String, List<Connection>>`) | Space: $O(V + E)$ | Metro transit graphs are planar and sparse ($E \ll V^2$). Adjacency matrices would waste $O(V^2)$ memory and slow neighbor iteration. |
| **PriorityQueue (Min-Heap)** | `java.util.PriorityQueue` | Insert: $O(\log N)$<br>Extract-Min: $O(\log N)$<br>Peek: $O(1)$ | Crucial for greedy frontier expansion in Dijkstra's shortest-path search and chronological departure ordering for train schedules. |
| **Stack** | `java.util.Stack` | Push: $O(1)$<br>Pop: $O(1)$<br>Peek: $O(1)$ | Models LIFO session search history, displaying the most recently executed journey search on top. |
| **HashMap** | `java.util.HashMap` / `LinkedHashMap` | Average Get: $O(1)$<br>Average Put: $O(1)$ | Provides instant $O(1)$ station lookup by Station ID and exact name, eliminating linear scans. |
| **HashSet** | `java.util.HashSet` | Average Add: $O(1)$<br>Average Contains: $O(1)$ | Tracks unique metro lines and settled vertices during Dijkstra and BFS/DFS traversals. |
| **ArrayList** | `java.util.ArrayList` | Append: $O(1)$ amortized<br>Get: $O(1)$ | Provides compact contiguous memory arrays for route path reconstruction and timetable filtering. |

---

## 5. Algorithmic Complexity Analysis

### Variable Definitions
- $V$: Number of metro stations (vertices) in the network ($V = 14$ in default sample).
- $E$: Number of physical metro connections (edges) ($E = 15$ undirected edges in default sample).
- $T$: Number of registered train timetable schedules ($T = 10$ in default sample).
- $N$: Length of text / document string or number of streaming events.
- $M$: Length of search pattern.
- $K$: Number of selected stations for multi-stop tour ($K \le 16$).

### Asymptotic Performance Matrix
| Algorithm | Time Complexity | Space Complexity | Notes |
|---|---|---|---|
| **Dijkstra Shortest Path** | $O((V + E) \log V)$ | $O(V + E)$ | Employs binary heap priority queue and predecessor tracking. Optimizes physical track distance. |
| **BFS / DFS Graph Traversal** | $O(V + E)$ | $O(V)$ | Complete reachability scan across vertices and edges. |
| **Direct Station Lookup** | $O(1)$ average | $O(V)$ | Hash table indexing on station ID and station name. |
| **Partial Station Search** | $O(V \cdot \text{len})$ | $O(1)$ auxiliary | Linear search comparing query substring across station names. |
| **Train Timetable Sorting** | $O(T \log T)$ | $O(T)$ | Orders schedules chronologically by departure minutes from midnight. |
| **Stack History Push/Pop** | $O(1)$ | $O(S)$ | Constant-time LIFO management where $S$ is history size. |
| **KMP Pattern Search** | $O(N + M)$ | $O(M)$ | Guaranteed worst-case linear time via failure function $\pi$. |
| **Z-Algorithm** | $O(N)$ | $O(N)$ | Linear time string matching using Z-box intervals. |
| **Rabin-Karp Search** | $O(N + M)$ avg, $O(N \cdot M)$ worst | $O(1)$ | Polynomial rolling hash with prime modulus $10^9+7$. |
| **Aho-Corasick Automaton** | $O(N + \sum M_i + Z)$ | $O(\sum M_i \cdot \Sigma)$ | Multi-pattern automaton matching all keywords in single text pass. |
| **Kasai's LCP Algorithm** | $O(N)$ | $O(N)$ | Linear time Longest Common Prefix calculation from suffix array. |
| **Suffix Automaton Query** | $O(M)$ | $O(N)$ states | Constant time transition per pattern character. |
| **Damerau-Levenshtein DP** | $O(\|S_1\| \cdot \|S_2\|)$ | $O(\|S_1\| \cdot \|S_2\|)$ | Edit distance with character transpositions. |
| **Bitmask DP Multi-Stop Tour** | $O(K^2 \cdot 2^K)$ | $O(K \cdot 2^K)$ | Exact optimal TSP tour over small subsets ($K \le 16$). |
| **Matrix-Chain Multiplication** | $O(P^3)$ | $O(P^2)$ | Dynamic programming table for $P$ matrices. |
| **Optimal BST** | $O(P^3)$ | $O(P^2)$ | Minimizes expected tree depth for station lookup frequencies. |
| **Dinic's Max-Flow** | $O(V^2 E)$ | $O(V + E)$ | Level graph BFS + blocking flow DFS. |
| **Bipartite Vehicle Matching** | $O(V \cdot E)$ | $O(V)$ | Maximum augmenting path matching for vehicle-to-trip allocation. |
| **Vertex-Cover 2-Approximation** | $O(V + E)$ | $O(V)$ | Maximal matching heuristic; bounds size to $\le 2 \cdot \|C^*\|$. |
| **Randomized QuickSort** | $O(N \log N)$ expected | $O(\log N)$ stack | Prevents worst-case quadratic degradation on sorted streams. |
| **Blelloch Parallel Scan** | $O(N)$ work, $O(\log N)$ span | $O(N)$ | Parallel prefix sum using tree upsweep/downsweep. |

---

## 6. Fictional Sample Metro Network

The platform initializes with a realistic, interconnected 14-station network spanning 4 distinct metro lines:

```
[ST07 Terminal] --(2.8 km)-- [ST08 Market] --(1.9 km)-- [ST01 Central] --(3.1 km)-- [ST09 Stadium] --(4.5 km)-- [ST10 Lake View]
                                                            | |                         | |
                                                   (2.1 km) | | (6.2 km)       (2.9 km) | | (4.0 km)
                                                            | |                         | |
                                                   [ST02 City Hall]             [ST12 Financial Dist.]  [ST13 Harbor Point]
                                                            |                           |
                                                   (1.8 km) |                  (3.7 km) |
                                                            |                           |
                                                   [ST03 Museum]                [ST04 University] --(2.5 km)-- [ST11 Botanical]
                                                            |                           |
                                                   (3.4 km) |                  (4.2 km) |
                                                            |                           |
                                                   [ST04 University]            [ST05 Tech Park]
                                                                                        |
                                                                               (5.6 km) |
                                                                                        |
[ST01 Central] --(6.2 km)-- [ST14 Innovation Hub] --(5.1 km)--------------------> [ST06 Airport]
```

### Line Summary:
1. **Red Line (North-South):** Central $\leftrightarrow$ City Hall $\leftrightarrow$ Museum $\leftrightarrow$ University $\leftrightarrow$ Tech Park $\leftrightarrow$ Airport
2. **Blue Line (East-West):** Terminal $\leftrightarrow$ Market $\leftrightarrow$ Central $\leftrightarrow$ Stadium $\leftrightarrow$ Lake View
3. **Green Line (Suburban Orbital):** Botanical Garden $\leftrightarrow$ University $\leftrightarrow$ Financial District $\leftrightarrow$ Stadium $\leftrightarrow$ Harbor Point
4. **Yellow Line (Airport Express):** Central $\leftrightarrow$ Innovation Hub $\leftrightarrow$ Airport

*Note: All station names, track lengths, capacities, and timetables are fictional data created exclusively for academic algorithm demonstration.*

---

## 7. Compilation & Execution Instructions

### Prerequisites
- Java Development Kit (JDK) 17 or later installed.
- Verify installation: `javac -version` and `java -version`.

### 7.1 Command Line (Windows / Linux / macOS)
```bash
# 1. Clone or navigate to the project directory
cd c:\Users\LENOVO\Desktop\TRAIN

# 2. Compile all source files into bin/
javac -d bin -sourcepath src src/MetroMind.java src/model/*.java src/service/*.java src/util/*.java src/test/*.java

# 3. Launch the Metro Mind Console Application
java -cp bin MetroMind

# 4. (Optional) Run the Automated Verification Test Suite
java -cp bin test.TestRunner
```

### 7.2 One-Click Scripts (Windows)
- Double click or run `build.bat` to compile.
- Double click or run `run.bat` to launch the application.
- Double click or run `test.bat` to run the test suite.

### 7.3 IDE Setup
- **Eclipse:** Select *File $\to$ Open Projects from File System...*, choose the `TRAIN` folder. Ensure `src/` is recognized as the build path source folder. Run `MetroMind.java` as a Java Application.
- **IntelliJ IDEA:** Select *File $\to$ Open...*, select the `TRAIN` directory. Mark `src` as the Source Root. Create an Application run configuration for `MetroMind`.
- **VS Code:** Open the `TRAIN` folder. The Java Language Support extension will automatically detect the `src/` directory. Open `src/MetroMind.java` and click **Run**.

---

## 8. Sample Interactive Execution Walkthrough

```text
=========================================================================
               METRO MIND - INTELLIGENT PUBLIC TRANSPORT PLATFORM        
                     Academic DSA Demonstration System                   
=========================================================================

=========================================================================
                               MAIN MENU                                 
=========================================================================
  1.  View All Metro Stations
  2.  Search for a Station (Exact, Partial & Fuzzy Autocomplete)
  3.  View Metro Network Connections (Adjacency List & BFS/DFS)
  4.  Find Shortest Route (Dijkstra's Algorithm)
  5.  Add Metro Station
  6.  Add Station Connection (Track Edge)
  7.  Add Train Schedule
  8.  View Upcoming Trains (PriorityQueue Min-Heap Timetable)
  9.  View Journey History (Stack - LIFO Order)
  10. Clear Journey History
  11. View Public Transport Analytics
  12. Advanced DSA Lab (CO1-CO6 Algorithms from Syllabus)
  13. Run Automated System Tests (DSA Verification Suite)
  14. Exit
=========================================================================
Enter your choice (1 - 14): 4

--- FIND SHORTEST ROUTE (DIJKSTRA'S ALGORITHM) ---
Enter Source Station (ID or Name): ST07
Enter Destination Station (ID or Name): ST06

=========================================================================
                        OPTIMAL ROUTE ITINERARY                          
=========================================================================
Origin      : Terminal [ST07]
Destination : Airport [ST06]
Total Dist. : 16.00 km
Est. Time   : 32.5 minutes (Running + Dwell + Transfers)
Total Stops : 4 station stops
Interchanges: 1 line transfer(s)
-------------------------------------------------------------------------
STEP-BY-STEP TRAVEL INSTRUCTIONS:
   1. Board [Blue Line   ] at Terminal           -> Ride to Market             (2.80 km)
   2. Board [Blue Line   ] at Market             -> Ride to Central            (1.90 km)
   3. Board [Yellow Line ] at Central            -> Ride to Innovation Hub     (6.20 km)
   4. Board [Yellow Line ] at Innovation Hub     -> Ride to Airport            (5.10 km)

LINE TRANSFER NOTICE(S):
  * Transfer at Central from [Blue Line] to [Yellow Line]
-------------------------------------------------------------------------
Algorithm Note: Dijkstra minimizes cumulative physical track distance (km).
=========================================================================
[Search recorded to session Journey History Stack]
```

---

## 9. Automated Test Verification Results

All 15 required core functional scenarios and the advanced CO1-CO6 algorithms were executed via `test.TestRunner`:

```text
===============================================================
       METRO MIND - AUTOMATED DSA VERIFICATION TEST SUITE       
===============================================================
 [PASS] Scenario 1: Find Valid Shortest Route         | Distance: 3.9 km (Expected: 3.90 km)
 [PASS] Scenario 2: Route Involving Interchange       | Interchanges detected: 1, Path stops: 4
 [PASS] Scenario 3: Handle Disconnected Stations      | Correctly returned unreachable failure
 [PASS] Scenario 4: Search Nonexistent Station        | Nonexistent station correctly resolved to null
 [PASS] Scenario 5: Add New Station                   | New station ST15 added. Total stations: 15
 [PASS] Scenario 6: Reject Duplicate Station ID       | Duplicate ID ST01 correctly rejected
 [PASS] Scenario 7: Add Valid Connection              | Direct connection Alpha <-> Beta added successfully
 [PASS] Scenario 8: Reject Invalid Distance (<= 0)    | Negative and zero edge weights strictly prohibited
 [PASS] Scenario 9: Train Departure PriorityQueue Order | PriorityQueue min-heap head: TR-A
 [PASS] Scenario 10: Reject Invalid Train Time        | Invalid time 25:70 rejected by regex validator
 [PASS] Scenario 11: Journey History LIFO Stack Order | Top of history is most recent search: Airport
 [PASS] Scenario 12: Clear Journey History            | History stack successfully cleared
 [PASS] Scenario 13: Analytics Real Metrics & Zero Safety | Total Stations: 14, Train Schedules: 10
 [PASS] Scenario 14: Input Validator Integrity        | Time and station ID pattern validators operating strictly
 [PASS] Scenario 15: Advanced DSA Modules (CO1 - CO6) | CO1 (Strings), CO2 (Suffix), CO3 (DP), CO4 (Flow), CO5 (NP/SAT), CO6 (Parallel/Rand) verified

---------------------------------------------------------------
TEST SUMMARY: 15 Passed, 0 Failed. Success Rate: 100.0%
===============================================================
ALL TEST SUITE SCENARIOS COMPLETED SUCCESSFULLY!
```

---

## 10. Known Limitations & Future Enhancements

### Known Limitations
1. **Volatile In-Memory Storage:** Graph modifications, newly added stations, and journey histories reside strictly in RAM and reset upon program termination.
2. **Distance-Centric Routing:** Dijkstra strictly optimizes physical track distance ($km$). In transit scenarios with lengthy walking transfers, a route with slightly longer distance but fewer interchanges might be preferred by passengers.
3. **Fixed Cruising Speed Assumption:** The estimated journey duration assumes an average cruising velocity of $40.0\text{ km/h}$ plus constant dwell times, omitting dynamic acceleration and deceleration physics.

### Possible Future Enhancements
1. **Multi-Objective Pareto Routing:** Implement multi-criteria A* or bi-criterion Dijkstra to simultaneously minimize travel time, financial fare, and number of line interchanges.
2. **Time-Dependent Transit Graphs:** Model scheduled timetable arrivals directly as time-expanded vertices (RAPTOR algorithm).
3. **Persistent File Serialization:** Add lightweight JSON or binary serialization to save modified networks and history across application restarts.

---

## 11. DSA Viva Questions & Answers for Review / Viva Voce

#### Q1: Why did you choose an Adjacency List over an Adjacency Matrix for the metro graph?
**Answer:** A metro network is a planar, sparse graph where the number of connections $E$ is proportional to $O(V)$ rather than $O(V^2)$. An Adjacency List uses $O(V + E)$ memory, whereas an Adjacency Matrix requires $O(V^2)$ space (most cells being zero). Moreover, exploring neighbors during Dijkstra's algorithm takes $O(\text{deg}(v))$ time with an Adjacency List, compared to scanning all $V$ entries in a matrix row ($O(V)$).

#### Q2: What is the time complexity of Dijkstra's algorithm in your implementation?
**Answer:** The time complexity is $O((V + E) \log V)$ using an Adjacency List and a binary-heap `PriorityQueue`. Each vertex is extracted from the PriorityQueue at most once ($V \log V$), and each edge connection is relaxed at most once, which may insert a new key into the PriorityQueue in $O(\log V)$ time ($E \log V$). Space complexity is $O(V + E)$ for distance tables, predecessor maps, and heap elements.

#### Q3: Does Dijkstra guarantee finding the path with the fewest train line transfers (interchanges)?
**Answer:** No. Standard Dijkstra minimizes the sum of edge weights (physical track distance in kilometres). A route with $12\text{ km}$ and $2$ transfers will be preferred by Dijkstra over a route with $13\text{ km}$ and $0$ transfers. To optimize for transfers, edge weights must be modified to include a transfer penalty cost or a multi-objective shortest path algorithm must be used.

#### Q4: How does the PriorityQueue min-heap order train departures in Module 3?
**Answer:** The `TrainSchedule` class implements `Comparable<TrainSchedule>`. It converts departure times formatted as `HH:mm` into total minutes elapsed since midnight (`hours * 60 + minutes`). The `compareTo` method compares these integer minutes, ensuring the train departing earliest is always placed at the root of the Min-Heap ($O(1)$ peek).

#### Q5: Why is a Stack suitable for the Journey History module?
**Answer:** Route search history exhibits Last-In, First-Out (LIFO) behavior: commuters and dispatchers want to review their most recent search first. A Stack supports constant-time $O(1)$ push and pop operations without the overhead of shifting elements found in primitive arrays.

#### Q6: How does KMP achieve $O(N + M)$ time complexity for service alert searching?
**Answer:** Traditional brute-force string matching backtracks the text pointer upon a mismatch, leading to $O(N \cdot M)$ worst-case time. KMP precomputes the Longest Proper Prefix which is also a Suffix (LPS array / $\pi$ table) in $O(M)$ time. During the text scan, when a mismatch occurs, the pattern pointer shifts using the LPS table while the text pointer never backtracks, guaranteeing $O(N)$ text traversal steps.

#### Q7: Explain Kasai's algorithm and why it is used with Suffix Arrays.
**Answer:** Suffix Arrays sort all suffixes of a text lexicographically. Kasai's algorithm computes the Longest Common Prefix (LCP) between consecutive suffixes in the sorted array in linear $O(N)$ time. It leverages the mathematical property that if the LCP between suffix $i$ and its predecessor is $h$, then the LCP for suffix $i+1$ is at least $h - 1$. In Metro Mind, this identifies duplicate and shared corridor descriptions across multi-line operating documents.

#### Q8: How does Bitmask Dynamic Programming solve the multi-station maintenance tour?
**Answer:** For a set of $K$ inspection stations ($K \le 16$), an integer bitmask of length $K$ represents the subset of stations already visited (where the $i$-th bit is $1$ if visited). The DP state $dp(\text{mask}, u)$ stores the minimum distance to have visited the subset `mask` ending at station $u$. Transitions iterate over unvisited vertices $v$: $dp(\text{mask} \mid 2^v, v) = \min(dp(\text{mask} \mid 2^v, v), dp(\text{mask}, u) + \text{dist}(u, v))$. This reduces complexity from brute force $O(K!)$ to $O(K^2 \cdot 2^K)$.

#### Q9: What is the significance of the Min-Cut in the Network Flow module?
**Answer:** By the Max-Flow Min-Cut Theorem, the maximum flow of passengers from a source terminal to a destination equals the capacity of the minimum cut separating them. After saturating the network with Dinic's or Edmonds-Karp, the cut edges identify the exact physical track bottleneck corridors that constrain transit network capacity during peak hours.

#### Q10: How does the Vertex Cover 2-Approximation algorithm guarantee bounded quality?
**Answer:** Finding the Minimum Vertex Cover is NP-Hard. The 2-approximation algorithm uses the Maximal Matching heuristic: it repeatedly selects an uncovered edge $(u, v)$, adds both endpoints $u$ and $v$ to the cover, and eliminates all edges incident to $u$ or $v$. Because no two matched edges share any vertex, any optimal vertex cover $C^*$ must pick at least one vertex from each matched edge ($|C^*| \ge M$). Since our heuristic picks both endpoints ($|C| = 2M$), we have $|C| = 2M \le 2|C^*|$, guaranteeing at most twice the optimal size.

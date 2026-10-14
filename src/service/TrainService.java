package service;

import model.TrainSchedule;

import java.util.*;

/**
 * Service for managing metro train departures and timetable operations.
 * Employs a Min-Heap (PriorityQueue) to maintain schedules sorted chronologically by departure time.
 * 
 * Algorithm Complexity:
 * - Insertion into schedule heap: O(log T) where T = number of train schedules.
 * - Retrieval of next available train: O(1) peek.
 * - Extraction of sorted timetable: O(T log T).
 */
public class TrainService {
    // PriorityQueue min-heap sorted by departure minutes
    private final PriorityQueue<TrainSchedule> scheduleHeap;
    // Master registry of all added schedules for filtering and analytics
    private final List<TrainSchedule> allSchedules;

    public TrainService() {
        this.scheduleHeap = new PriorityQueue<>();
        this.allSchedules = new ArrayList<>();
    }

    /**
     * Adds a verified train schedule into the priority queue.
     */
    public synchronized boolean addSchedule(TrainSchedule schedule) {
        if (schedule == null) {
            throw new IllegalArgumentException("Schedule cannot be null.");
        }
        // Avoid duplicate train IDs
        for (TrainSchedule s : allSchedules) {
            if (s.getTrainId().equalsIgnoreCase(schedule.getTrainId())) {
                return false;
            }
        }
        allSchedules.add(schedule);
        scheduleHeap.add(schedule);
        return true;
    }

    /**
     * Returns the next upcoming train departure from the min-heap without removing it.
     */
    public TrainSchedule getNextTrain() {
        return scheduleHeap.peek();
    }

    /**
     * Retrieves all upcoming trains ordered chronologically.
     * Time Complexity: O(T log T)
     */
    public List<TrainSchedule> getUpcomingTrains() {
        List<TrainSchedule> sortedList = new ArrayList<>(allSchedules);
        Collections.sort(sortedList);
        return sortedList;
    }

    /**
     * Filters train schedules for a specific destination station ID or name.
     */
    public List<TrainSchedule> getSchedulesByDestination(String destinationQuery) {
        if (destinationQuery == null || destinationQuery.trim().isEmpty()) {
            return Collections.emptyList();
        }
        String q = destinationQuery.trim().toLowerCase();
        List<TrainSchedule> matches = new ArrayList<>();
        for (TrainSchedule s : allSchedules) {
            if (s.getDestinationStationId().toLowerCase().contains(q) ||
                s.getDestinationStationName().toLowerCase().contains(q)) {
                matches.add(s);
            }
        }
        Collections.sort(matches);
        return matches;
    }

    /**
     * Filters train schedules for a specific metro line.
     */
    public List<TrainSchedule> getSchedulesByLine(String lineName) {
        if (lineName == null || lineName.trim().isEmpty()) {
            return Collections.emptyList();
        }
        String q = lineName.trim().toLowerCase();
        List<TrainSchedule> matches = new ArrayList<>();
        for (TrainSchedule s : allSchedules) {
            if (s.getLine().toLowerCase().contains(q)) {
                matches.add(s);
            }
        }
        Collections.sort(matches);
        return matches;
    }

    public int getTotalSchedulesCount() {
        return allSchedules.size();
    }

    public boolean isEmpty() {
        return allSchedules.isEmpty();
    }

    /**
     * Analyzes scheduling headway conflicts (e.g. two trains departing on the same line within 4 minutes).
     */
    public List<String> detectHeadwayConflicts(int bufferMinutes) {
        List<String> conflicts = new ArrayList<>();
        Map<String, List<TrainSchedule>> byLine = new HashMap<>();
        for (TrainSchedule s : allSchedules) {
            byLine.computeIfAbsent(s.getLine().toUpperCase(), k -> new ArrayList<>()).add(s);
        }

        for (Map.Entry<String, List<TrainSchedule>> entry : byLine.entrySet()) {
            List<TrainSchedule> list = entry.getValue();
            Collections.sort(list);
            for (int i = 0; i < list.size() - 1; i++) {
                TrainSchedule t1 = list.get(i);
                TrainSchedule t2 = list.get(i + 1);
                int diff = Math.abs(t2.getDepartureMinutes() - t1.getDepartureMinutes());
                if (diff < bufferMinutes) {
                    conflicts.add(String.format("Line [%s] Headway Warning: %s (%s) and %s (%s) depart within %d mins (gap: %d mins)",
                            entry.getKey(), t1.getTrainId(), t1.getDepartureTime(), t2.getTrainId(), t2.getDepartureTime(), bufferMinutes, diff));
                }
            }
        }
        return conflicts;
    }
}

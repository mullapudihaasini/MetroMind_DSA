package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Stores historical route search queries and computed solutions in a Stack (LIFO order).
 */
public class JourneyRecord {
    private final String sourceId;
    private final String sourceName;
    private final String destinationId;
    private final String destinationName;
    private final List<String> pathStations;
    private final List<String> linesUsed;
    private final double totalDistanceKm;
    private final double estimatedMinutes;
    private final int stopCount;
    private final int interchangeCount;
    private final String searchTimestamp;

    public JourneyRecord(String sourceId, String sourceName,
                         String destinationId, String destinationName,
                         List<String> pathStations, List<String> linesUsed,
                         double totalDistanceKm, double estimatedMinutes,
                         int stopCount, int interchangeCount) {
        this.sourceId = sourceId;
        this.sourceName = sourceName;
        this.destinationId = destinationId;
        this.destinationName = destinationName;
        this.pathStations = new ArrayList<>(pathStations);
        this.linesUsed = new ArrayList<>(linesUsed);
        this.totalDistanceKm = Math.round(totalDistanceKm * 100.0) / 100.0;
        this.estimatedMinutes = Math.round(estimatedMinutes * 10.0) / 10.0;
        this.stopCount = stopCount;
        this.interchangeCount = interchangeCount;
        this.searchTimestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public String getSourceId() {
        return sourceId;
    }

    public String getSourceName() {
        return sourceName;
    }

    public String getDestinationId() {
        return destinationId;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public List<String> getPathStations() {
        return Collections.unmodifiableList(pathStations);
    }

    public List<String> getLinesUsed() {
        return Collections.unmodifiableList(linesUsed);
    }

    public double getTotalDistanceKm() {
        return totalDistanceKm;
    }

    public double getEstimatedMinutes() {
        return estimatedMinutes;
    }

    public int getStopCount() {
        return stopCount;
    }

    public int getInterchangeCount() {
        return interchangeCount;
    }

    public String getSearchTimestamp() {
        return searchTimestamp;
    }

    public String getRouteSummary() {
        return String.join(" -> ", pathStations);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s -> %s | Distance: %.2f km | Est. Time: %.1f mins | Stops: %d | Interchanges: %d\n  Path: %s",
                searchTimestamp, sourceName, destinationName, totalDistanceKm, estimatedMinutes, stopCount, interchangeCount, getRouteSummary());
    }
}

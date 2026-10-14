package model;

import java.util.Objects;

/**
 * Represents a scheduled train trip within the metro network.
 * Ordered primarily by departure time for PriorityQueue min-heap processing.
 */
public class TrainSchedule implements Comparable<TrainSchedule> {
    private final String trainId;
    private final String line;
    private final String sourceStationId;
    private final String sourceStationName;
    private final String destinationStationId;
    private final String destinationStationName;
    private final String departureTime; // Format "HH:mm"
    private final int departureMinutes; // Minutes since 00:00
    private String status;              // "On Time", "Boarding", "Delayed"

    public TrainSchedule(String trainId, String line, 
                         String sourceStationId, String sourceStationName,
                         String destinationStationId, String destinationStationName, 
                         String departureTime, String status) {
        if (trainId == null || trainId.trim().isEmpty()) {
            throw new IllegalArgumentException("Train ID cannot be empty.");
        }
        if (departureTime == null || !departureTime.matches("^([01]\\d|2[0-3]):[0-5]\\d$")) {
            throw new IllegalArgumentException("Departure time must be valid 24-hour format HH:mm (e.g., 08:30).");
        }

        this.trainId = trainId.trim().toUpperCase();
        this.line = line != null ? line.trim() : "General Line";
        this.sourceStationId = sourceStationId != null ? sourceStationId.trim().toUpperCase() : "UNKNOWN";
        this.sourceStationName = sourceStationName != null ? sourceStationName.trim() : "Unknown Station";
        this.destinationStationId = destinationStationId != null ? destinationStationId.trim().toUpperCase() : "UNKNOWN";
        this.destinationStationName = destinationStationName != null ? destinationStationName.trim() : "Unknown Station";
        this.departureTime = departureTime.trim();
        
        String[] parts = this.departureTime.split(":");
        this.departureMinutes = Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
        this.status = status != null ? status.trim() : "On Time";
    }

    public TrainSchedule(String trainId, String line, 
                         String sourceStationId, String sourceStationName,
                         String destinationStationId, String destinationStationName, 
                         String departureTime) {
        this(trainId, line, sourceStationId, sourceStationName, destinationStationId, destinationStationName, departureTime, "On Time");
    }

    public String getTrainId() {
        return trainId;
    }

    public String getLine() {
        return line;
    }

    public String getSourceStationId() {
        return sourceStationId;
    }

    public String getSourceStationName() {
        return sourceStationName;
    }

    public String getDestinationStationId() {
        return destinationStationId;
    }

    public String getDestinationStationName() {
        return destinationStationName;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public int getDepartureMinutes() {
        return departureMinutes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        if (status != null && !status.trim().isEmpty()) {
            this.status = status.trim();
        }
    }

    @Override
    public int compareTo(TrainSchedule other) {
        if (other == null) return 1;
        int timeCmp = Integer.compare(this.departureMinutes, other.departureMinutes);
        if (timeCmp != 0) return timeCmp;
        return this.trainId.compareTo(other.trainId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrainSchedule that = (TrainSchedule) o;
        return departureMinutes == that.departureMinutes &&
                trainId.equalsIgnoreCase(that.trainId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(trainId.toUpperCase(), departureMinutes);
    }

    @Override
    public String toString() {
        return String.format("[%s] %-12s | %-16s -> %-16s | Dep: %s | Status: %s",
                trainId, line, sourceStationName, destinationStationName, departureTime, status);
    }
}

package model;

/**
 * Represents a streaming or historical transit operational event (e.g. sensor count, tap-in event, train dispatch).
 * Used for randomized ranking (QuickSort), reservoir sampling, and parallel prefix scans (Blelloch scan).
 */
public class TransitEvent implements Comparable<TransitEvent> {
    private final long eventId;
    private final String stationId;
    private final String eventType; // PASSENGER_ENTRY, PASSENGER_EXIT, DISPATCH, SENSOR_PING
    private final int value;         // e.g. passenger count or delay in seconds
    private final long timestamp;

    public TransitEvent(long eventId, String stationId, String eventType, int value, long timestamp) {
        this.eventId = eventId;
        this.stationId = stationId;
        this.eventType = eventType;
        this.value = value;
        this.timestamp = timestamp;
    }

    public long getEventId() {
        return eventId;
    }

    public String getStationId() {
        return stationId;
    }

    public String getEventType() {
        return eventType;
    }

    public int getValue() {
        return value;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public int compareTo(TransitEvent o) {
        return Integer.compare(this.value, o.value);
    }

    @Override
    public String toString() {
        return String.format("Event #%-5d | Station: %-6s | Type: %-15s | Value: %-5d | Time: %d",
                eventId, stationId, eventType, value, timestamp);
    }
}

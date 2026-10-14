package model;

import java.util.Objects;

/**
 * Represents a directed or bidirectional edge in the metro graph between two stations.
 * Stores distance in kilometers, line information, and hourly passenger capacity.
 */
public class Connection {
    private final Station source;
    private final Station destination;
    private final double distance;
    private final String line;
    private final int capacity; // Passengers per hour (for network flow modeling)

    public Connection(Station source, Station destination, double distance, String line, int capacity) {
        if (source == null || destination == null) {
            throw new IllegalArgumentException("Source and destination stations cannot be null.");
        }
        if (distance <= 0) {
            throw new IllegalArgumentException("Connection distance must be strictly positive (> 0).");
        }
        if (line == null || line.trim().isEmpty()) {
            throw new IllegalArgumentException("Metro line name cannot be empty.");
        }
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be strictly positive.");
        }

        this.source = source;
        this.destination = destination;
        this.distance = Math.round(distance * 100.0) / 100.0; // round to 2 decimal places
        this.line = line.trim();
        this.capacity = capacity;
    }

    public Connection(Station source, Station destination, double distance, String line) {
        this(source, destination, distance, line, 12000); // Default 12,000 passengers/hour capacity
    }

    public Station getSource() {
        return source;
    }

    public Station getDestination() {
        return destination;
    }

    public double getDistance() {
        return distance;
    }

    public String getLine() {
        return line;
    }

    public int getCapacity() {
        return capacity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Connection that = (Connection) o;
        return Double.compare(that.distance, distance) == 0 &&
                source.equals(that.source) &&
                destination.equals(that.destination) &&
                line.equalsIgnoreCase(that.line);
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, destination, distance, line.toUpperCase());
    }

    @Override
    public String toString() {
        return String.format("%s -> %s (%.2f km, Line: %s, Cap: %d pax/hr)",
                source.getName(), destination.getName(), distance, line, capacity);
    }
}

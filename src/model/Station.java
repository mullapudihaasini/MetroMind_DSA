package model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Represents a metro station vertex in the transport graph.
 * Stores station identification, geographic/network name, and metro line associations.
 */
public class Station {
    private final String id;
    private final String name;
    private final String primaryLine;
    private final Set<String> lines;
    private boolean isInterchange;

    public Station(String id, String name, String primaryLine) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Station ID cannot be null or empty.");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Station name cannot be null or empty.");
        }
        if (primaryLine == null || primaryLine.trim().isEmpty()) {
            throw new IllegalArgumentException("Metro line cannot be null or empty.");
        }

        this.id = id.trim().toUpperCase();
        this.name = name.trim();
        this.primaryLine = primaryLine.trim();
        this.lines = new HashSet<>();
        this.lines.add(this.primaryLine);
        this.isInterchange = false;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPrimaryLine() {
        return primaryLine;
    }

    public Set<String> getLines() {
        return Collections.unmodifiableSet(lines);
    }

    public boolean isInterchange() {
        return isInterchange;
    }

    /**
     * Adds an additional connecting metro line to this station (marking it as an interchange).
     * @param line Name of the intersecting metro line.
     */
    public void addLine(String line) {
        if (line != null && !line.trim().isEmpty()) {
            this.lines.add(line.trim());
            if (this.lines.size() > 1) {
                this.isInterchange = true;
            }
        }
    }

    public String getLinesFormatted() {
        return String.join(", ", lines);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Station station = (Station) o;
        return id.equalsIgnoreCase(station.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id.toUpperCase());
    }

    @Override
    public String toString() {
        return String.format("[%s] %s (%s)%s", 
                id, name, getLinesFormatted(), isInterchange ? " [Interchange]" : "");
    }
}

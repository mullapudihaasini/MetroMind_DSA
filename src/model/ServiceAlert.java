package model;

/**
 * Represents an operational transit alert or bulletin.
 * Used for multi-pattern search algorithms (KMP, Rabin-Karp, Aho-Corasick, Suffix Structures).
 */
public class ServiceAlert {
    private final String alertId;
    private final String line;
    private final String severity; // INFO, WARNING, CRITICAL
    private final String message;
    private final String timestamp;

    public ServiceAlert(String alertId, String line, String severity, String message, String timestamp) {
        this.alertId = alertId;
        this.line = line;
        this.severity = severity;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getAlertId() {
        return alertId;
    }

    public String getLine() {
        return line;
    }

    public String getSeverity() {
        return severity;
    }

    public String getMessage() {
        return message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return String.format("[%s] [%s - %s] %s (Time: %s)",
                alertId, severity, line, message, timestamp);
    }
}

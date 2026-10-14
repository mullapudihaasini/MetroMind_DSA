package util;

import java.util.Scanner;

/**
 * Utility class providing robust, defensive input validation and safe console reading routines.
 * Prevents scanner crashes, infinite loops on bad input, and guarantees data integrity.
 */
public class InputValidator {

    private InputValidator() {
        // Static utility class
    }

    /**
     * Reads a menu integer selection safely within [min, max].
     */
    public static int readIntChoice(Scanner scanner, int min, int max) {
        while (true) {
            System.out.print("Enter your choice (" + min + " - " + max + "): ");
            String line = scanner.nextLine().trim();
            try {
                int choice = Integer.parseInt(line);
                if (choice >= min && choice <= max) {
                    return choice;
                }
                System.out.println("(!) Error: Selection must be between " + min + " and " + max + ". Please try again.");
            } catch (NumberFormatException e) {
                System.out.println("(!) Error: Invalid input. Please enter a valid integer number.");
            }
        }
    }

    /**
     * Reads a strictly positive double (e.g. distance > 0).
     */
    public static double readPositiveDouble(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                double val = Double.parseDouble(line);
                if (val > 0.0) {
                    return Math.round(val * 100.0) / 100.0;
                }
                System.out.println("(!) Error: Distance must be strictly positive (> 0.0 km).");
            } catch (NumberFormatException e) {
                System.out.println("(!) Error: Please enter a valid decimal number.");
            }
        }
    }

    /**
     * Reads a non-empty string.
     */
    public static String readNonEmptyString(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            if (!line.isEmpty()) {
                return line;
            }
            System.out.println("(!) Error: Input cannot be empty. Please enter a value.");
        }
    }

    /**
     * Reads and validates a 24-hour time string in HH:mm format.
     */
    public static String readValidTime(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt + " (Format HH:mm, e.g. 08:30): ");
            String line = scanner.nextLine().trim();
            if (isValidTime(line)) {
                return line;
            }
            System.out.println("(!) Error: Invalid time format. Must be 24-hour HH:mm between 00:00 and 23:59.");
        }
    }

    /**
     * Validates whether a string matches HH:mm format.
     */
    public static boolean isValidTime(String time) {
        if (time == null) return false;
        return time.matches("^([01]\\d|2[0-3]):[0-5]\\d$");
    }

    /**
     * Validates a station ID format (alphanumeric, 2-10 chars).
     */
    public static boolean isValidStationId(String id) {
        if (id == null) return false;
        String trimmed = id.trim();
        return trimmed.length() >= 2 && trimmed.length() <= 10 && trimmed.matches("^[a-zA-Z0-9_-]+$");
    }
}

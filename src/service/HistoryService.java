package service;

import model.JourneyRecord;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

/**
 * Service for maintaining session route search history using a Stack (LIFO data structure).
 * Demonstrates constant-time O(1) push and pop operations, presenting recent queries in reverse chronological order.
 * 
 * Algorithm Complexity:
 * - Push search record: O(1)
 * - Clear history: O(1)
 * - View search history: O(S) where S = number of recorded searches.
 */
public class HistoryService {
    // Stack maintaining search history (top of stack = most recent search)
    private final Stack<JourneyRecord> searchStack;

    public HistoryService() {
        this.searchStack = new Stack<>();
    }

    /**
     * Pushes a new journey search record onto the history stack.
     * Time Complexity: O(1)
     */
    public synchronized void pushSearch(JourneyRecord record) {
        if (record != null) {
            searchStack.push(record);
        }
    }

    /**
     * Retrieves all recorded journey searches in reverse chronological order (most recent first).
     * Does not mutate or empty the underlying Stack.
     */
    public List<JourneyRecord> getRecentSearches() {
        List<JourneyRecord> list = new ArrayList<>(searchStack);
        Collections.reverse(list); // LIFO display order
        return list;
    }

    /**
     * Clears all session search history from the stack.
     * Time Complexity: O(1)
     */
    public synchronized void clearHistory() {
        searchStack.clear();
    }

    public boolean isEmpty() {
        return searchStack.isEmpty();
    }

    public int getHistoryCount() {
        return searchStack.size();
    }

    /**
     * Returns an unmodifiable snapshot of the underlying stack for analytics computation.
     */
    public List<JourneyRecord> getAllRecordsSnapshot() {
        return Collections.unmodifiableList(new ArrayList<>(searchStack));
    }
}

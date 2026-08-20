/**
 * Represents a task that starts at a specific date or time and ends at another.
 */
public class Event extends Task {
    protected String from;
    protected String to;

    /**
     * Creates an event task with the given description, start string, and end string.
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    @Override
    public String toString() {
        return "[E][" + getStatusIcon() + "] " + description + " (from: " + from + " to: " + to + ")";
    }
}

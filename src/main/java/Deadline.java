/**
 * Represents a task that needs to be done by a specific date or time.
 */
public class Deadline extends Task {
    protected String by;

    /**
     * Creates a deadline task with the given description and deadline string.
     */
    public Deadline(String description, String by) {
        super(description);
        this.by = by;
    }

    @Override
    public String toString() {
        return "[D][" + getStatusIcon() + "] " + description + " (by: " + by + ")";
    }

}

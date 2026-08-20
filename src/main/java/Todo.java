/**
 * Represents a task without any date or time attached to it.
 */
public class Todo extends Task {
    /**
     * Creates a todo task that is not marked as done yet.
     */
    public Todo(String description) {
        super(description);
    }

    @Override
    public String toString() {
        return "[T][" + getStatusIcon() + "] " + description;
    }
}

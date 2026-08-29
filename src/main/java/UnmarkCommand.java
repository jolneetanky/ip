/**
 * Marks a task as not done.
 */
public class UnmarkCommand extends Command {
    private final int taskNumber;

    /**
     * Creates a command that unmarks the given one-based task number.
     */
    public UnmarkCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws MrChatbotException {
        Task task = tasks.unmark(taskNumber);
        storage.saveTasks(tasks);
        ui.showTaskUnmarked(task);
    }
}

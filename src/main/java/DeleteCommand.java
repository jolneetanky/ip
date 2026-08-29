/**
 * Deletes a task from the task list.
 */
public class DeleteCommand extends Command {
    private final int taskNumber;

    /**
     * Creates a command that deletes the given one-based task number.
     */
    public DeleteCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws MrChatbotException {
        Task task = tasks.delete(taskNumber);
        storage.saveTasks(tasks);
        ui.showTaskDeleted(task, tasks.size());
    }
}

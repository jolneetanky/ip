package mrchatbot.command;

import mrchatbot.storage.Storage;
import mrchatbot.task.TaskList;
import mrchatbot.ui.Ui;

/**
 * Shows all tasks in the task list.
 */
public class ListCommand extends Command {
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTaskList(tasks);
    }
}

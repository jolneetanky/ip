package mrchatbot.command;

import mrchatbot.storage.Storage;
import mrchatbot.task.TaskList;
import mrchatbot.ui.Ui;

/**
 * Exits the chatbot session.
 */
public class ExitCommand extends Command {
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showBye();
    }

    @Override
    public boolean isExit() {
        return true;
    }
}

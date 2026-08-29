package mrchatbot.command;

import mrchatbot.storage.Storage;
import mrchatbot.task.TaskList;
import mrchatbot.ui.Ui;

/**
 * Shows the accepted command formats.
 */
public class HelpCommand extends Command {
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showHelp();
    }
}

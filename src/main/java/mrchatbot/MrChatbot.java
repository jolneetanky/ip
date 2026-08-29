package mrchatbot;

import mrchatbot.command.Command;
import mrchatbot.exception.MrChatbotException;
import mrchatbot.parser.Parser;
import mrchatbot.storage.Storage;
import mrchatbot.task.TaskList;
import mrchatbot.ui.Ui;

/**
 * Runs Mr Chatbot and coordinates parsing, storage, task operations, and UI output.
 */
public class MrChatbot {
    private static final String SAVE_FILE_PATH = "data/duke.txt";

    /**
     * Converts a name to title case, where the first letter of each word is capitalized.
     */
    private static String toTitleCase(String name) {
        if (name.trim().isEmpty()) {
            return "";
        }
        String[] words = name.trim().toLowerCase().split("\\s+");
        String titleCaseName = "";
        for (String word : words) {
            if (!titleCaseName.isEmpty()) {
                titleCaseName += " ";
            }
            titleCaseName += word.substring(0, 1).toUpperCase() + word.substring(1);
        }
        return titleCaseName;
    }

    /**
     * Starts the chatbot and processes commands until the user exits or input ends.
     */
    public static void main(String[] args) {
        Parser parser = new Parser();
        String name = "User";
        Ui ui = new Ui(name);
        ui.showWelcome();

        Storage storage = new Storage(SAVE_FILE_PATH);
        TaskList tasks = new TaskList();

        // Load tasks on startup
        try {
            tasks = new TaskList(storage.loadTasks());
        } catch (MrChatbotException e) {
            ui.showError(e.getMessage());
        }

        // read inputs
        while (ui.hasNextInput()) {
            String input = ui.readInput();
            ui.showLine();

            try {
                Command command = parser.parseCommand(input);
                command.execute(tasks, ui, storage);
                if (command.isExit()) {
                    ui.showLine();
                    break;
                }
            } catch (MrChatbotException e) {
                ui.showError(e.getMessage());
            }
            ui.showLine();
        }
    }
}

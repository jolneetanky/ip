package mrchatbot;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import mrchatbot.command.Command;
import mrchatbot.exception.MrChatbotException;
import mrchatbot.parser.Parser;
import mrchatbot.storage.Storage;
import mrchatbot.task.TaskList;
import mrchatbot.ui.Ui;

/**
 * Handles chatbot command processing without depending on a specific user interface.
 */
public class MrChatbotEngine {
    private static final String SAVE_FILE_PATH = "data/duke.txt";

    private final Parser parser;
    private final Storage storage;
    private final TaskList tasks;
    private final String startupError;

    /**
     * Creates a chatbot engine and loads saved tasks from disk.
     */
    public MrChatbotEngine() {
        parser = new Parser();
        storage = new Storage(SAVE_FILE_PATH);

        TaskList loadedTasks = new TaskList();
        String loadError = null;
        try {
            loadedTasks = new TaskList(storage.loadTasks());
        } catch (MrChatbotException e) {
            loadError = e.getMessage();
        }
        tasks = loadedTasks;
        startupError = loadError;
    }

    /**
     * Returns the startup error message, or null if startup completed without errors.
     */
    public String getStartupError() {
        return startupError;
    }

    /**
     * Processes a user command using the given UI and returns true if it exits the session.
     */
    public boolean processCommand(String input, Ui ui) throws MrChatbotException {
        Command command = parser.parseCommand(input);
        command.execute(tasks, ui, storage);
        return command.isExit();
    }

    /**
     * Processes a user command and returns the response text for GUI display.
     */
    public String getResponse(String input) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Ui ui = new Ui("User", new PrintStream(outputStream, true, StandardCharsets.UTF_8));
        try {
            processCommand(input, ui);
        } catch (MrChatbotException e) {
            ui.showError(e.getMessage());
        }
        return outputStream.toString(StandardCharsets.UTF_8).stripTrailing();
    }
}

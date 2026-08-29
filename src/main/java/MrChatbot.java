import java.util.ArrayList;

public class MrChatbot {
    private static final int MAX_TASKS = 100;
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

    public static void main(String[] args) {
        Ui ui = new Ui();
        Parser parser = new Parser();
        String name = "User";
        ui.showWelcome(name);

        Storage storage = new Storage(SAVE_FILE_PATH);
        // Store tasks in an ArrayList so tasks can be added and deleted easily.
        ArrayList<Task> tasks = new ArrayList<>();

        // Load tasks on startup
        try {
            tasks = storage.loadTasks();
        } catch (MrChatbotException e) {
            ui.showError(e.getMessage());
        }

        // read inputs
        while (ui.hasNextInput()) {
            String input = ui.readInput();
            String lowerCaseInput = input.toLowerCase();
            CommandType commandType = parser.parseCommandType(input);
            ui.showLine();
            if (commandType == CommandType.BYE && commandType.matchesExactly(lowerCaseInput)) {
                ui.showBye(name);
                ui.showLine();
                break;
            }

            try {
                if (commandType == CommandType.HELP && commandType.matchesExactly(lowerCaseInput)) {
                    ui.showHelp();
                } else if (commandType == CommandType.LIST && commandType.matchesExactly(lowerCaseInput)) {
                    ui.showTaskList(tasks);
                } else if (commandType == CommandType.MARK) {
                    int taskNumber = parser.parseTaskNumber(parser.commandArgument(input, CommandType.MARK));
                    if (taskNumber < 1 || taskNumber > tasks.size()) {
                        throw new MrChatbotException("This task doesn't exist...");
                    }
                    Task task = tasks.get(taskNumber - 1);
                    task.markAsDone();
                    storage.saveTasks(tasks);
                    ui.showTaskMarked(task);
                } else if (commandType == CommandType.UNMARK) {
                    int taskNumber = parser.parseTaskNumber(parser.commandArgument(input, CommandType.UNMARK));
                    if (taskNumber < 1 || taskNumber > tasks.size()) {
                        throw new MrChatbotException("This task doesn't exist...");
                    }
                    Task task = tasks.get(taskNumber - 1);
                    task.markAsNotDone();
                    storage.saveTasks(tasks);
                    ui.showTaskUnmarked(task);
                } else if (commandType == CommandType.DELETE) {
                    int taskNumber = parser.parseTaskNumber(parser.commandArgument(input, CommandType.DELETE));
                    if (taskNumber < 1 || taskNumber > tasks.size()) {
                        throw new MrChatbotException("This task doesn't exist...");
                    }
                    Task task = tasks.remove(taskNumber - 1);
                    storage.saveTasks(tasks);
                    ui.showTaskDeleted(task, tasks.size());
                } else if (tasks.size() == MAX_TASKS) {
                    throw new MrChatbotException(
                            "Sorry, Mr " + name + ", your task list is full. No more tasks can be added.");
                } else {
                    Task task = parser.createTask(input);
                    tasks.add(task);
                    storage.saveTasks(tasks);
                    ui.showTaskAdded(task, tasks.size());
                }
            } catch (MrChatbotException e) {
                ui.showError(e.getMessage());
            }
            ui.showLine();
        }
    }
}

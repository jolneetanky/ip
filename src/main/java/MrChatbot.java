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
            CommandType commandType = parser.parseCommandType(input);
            ui.showLine();
            if (commandType == CommandType.BYE) {
                ui.showBye(name);
                ui.showLine();
                break;
            }

            try {
                if (commandType == CommandType.HELP) {
                    ui.showHelp();
                } else if (commandType == CommandType.LIST) {
                    ui.showTaskList(tasks);
                } else if (commandType == CommandType.MARK) {
                    int taskNumber = parser.parseTaskNumber(parser.commandArgument(input, CommandType.MARK));
                    Task task = tasks.mark(taskNumber);
                    storage.saveTasks(tasks);
                    ui.showTaskMarked(task);
                } else if (commandType == CommandType.UNMARK) {
                    int taskNumber = parser.parseTaskNumber(parser.commandArgument(input, CommandType.UNMARK));
                    Task task = tasks.unmark(taskNumber);
                    storage.saveTasks(tasks);
                    ui.showTaskUnmarked(task);
                } else if (commandType == CommandType.DELETE) {
                    int taskNumber = parser.parseTaskNumber(parser.commandArgument(input, CommandType.DELETE));
                    Task task = tasks.delete(taskNumber);
                    storage.saveTasks(tasks);
                    ui.showTaskDeleted(task, tasks.size());
                } else if (tasks.isFull(MAX_TASKS)) {
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

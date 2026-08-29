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
                if (command != null) {
                    command.execute(tasks, ui, storage);
                    if (command.isExit()) {
                        ui.showLine();
                        break;
                    }
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

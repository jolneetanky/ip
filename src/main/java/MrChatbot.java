import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;

public class MrChatbot {
    private static final int MAX_TASKS = 100;
    private static final String SAVE_FILE_PATH = "data/duke.txt";
    private static final String INVALID_TASK_FORMAT_MESSAGE = "Sorry, I don't understand that task format.";
    private static final String UNKNOWN_COMMAND_MESSAGE = "Sorry, I don't understand that command. Please type \"help\".";
    private static final String TODO_FORMAT_MESSAGE = "Todo description cannot be empty. Please use the format: todo <description>";
    private static final String DEADLINE_FORMAT_MESSAGE =
            "Please use the format: deadline <description> /by <yyyy-mm-dd>";
    private static final String DEADLINE_DESCRIPTION_MISSING_MESSAGE =
            "Deadline description cannot be empty. " + DEADLINE_FORMAT_MESSAGE;
    private static final String DEADLINE_BY_MISSING_MESSAGE =
            "Deadline /by cannot be empty. " + DEADLINE_FORMAT_MESSAGE;
    private static final String DEADLINE_DESCRIPTION_AND_BY_MISSING_MESSAGE =
            "Deadline description and /by cannot be empty. " + DEADLINE_FORMAT_MESSAGE;
    private static final String EVENT_FORMAT_MESSAGE =
            "Please use the format: event <description> /from <yyyy-mm-dd> /to <yyyy-mm-dd>";
    private static final String DEADLINE_DATE_FORMAT_MESSAGE =
            "Deadline date must be in yyyy-mm-dd format. " + DEADLINE_FORMAT_MESSAGE;
    private static final String EVENT_DATE_FORMAT_MESSAGE =
            "Event dates must be in yyyy-mm-dd format. " + EVENT_FORMAT_MESSAGE;

    /**
     * Represents the command words accepted by the chatbot.
     */
    private enum CommandType {
        TODO("todo"),
        DEADLINE("deadline"),
        EVENT("event"),
        LIST("list"),
        MARK("mark"),
        UNMARK("unmark"),
        DELETE("delete"),
        BYE("bye"),
        HELP("help"),
        UNKNOWN("");

        private final String word;

        CommandType(String word) {
            this.word = word;
        }

        private boolean matches(String lowerCaseInput) {
            return lowerCaseInput.equals(word) || lowerCaseInput.startsWith(word + " ");
        }

        private boolean matchesExactly(String lowerCaseInput) {
            return lowerCaseInput.equals(word);
        }

        private String withTrailingSpace() {
            return word + " ";
        }

        private static CommandType from(String input) {
            String lowerCaseInput = input.toLowerCase();
            for (CommandType commandType : values()) {
                if (commandType != UNKNOWN && commandType.matches(lowerCaseInput)) {
                    return commandType;
                }
            }
            return UNKNOWN;
        }
    }

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
     * Creates a task from the user's command.
     */
    private static Task createTask(String input) throws MrChatbotException {
        String lowerCaseInput = input.toLowerCase();
        CommandType commandType = CommandType.from(input);

        if (lowerCaseInput.equals(CommandType.TODO.word)) {
            throw new MrChatbotException(TODO_FORMAT_MESSAGE);
        }

        if (lowerCaseInput.equals(CommandType.DEADLINE.word)) {
            throw new MrChatbotException(DEADLINE_DESCRIPTION_AND_BY_MISSING_MESSAGE);
        }

        if (lowerCaseInput.equals(CommandType.EVENT.word)) {
            throw new MrChatbotException(eventMissingMessage(true, true, true));
        }

        if (commandType == CommandType.MARK || commandType == CommandType.UNMARK || commandType == CommandType.DELETE) {
            throw new MrChatbotException(INVALID_TASK_FORMAT_MESSAGE);
        }

        if (commandType == CommandType.TODO) {
            String description = input.substring(CommandType.TODO.withTrailingSpace().length());
            if (description.isBlank()) {
                throw new MrChatbotException(TODO_FORMAT_MESSAGE);
            }
            return new Todo(description);
        }

        if (commandType == CommandType.DEADLINE) {
            int byIndex = lowerCaseInput.indexOf(" /by ");
            if (byIndex == -1) {
                String description = input.substring(CommandType.DEADLINE.withTrailingSpace().length());
                if (description.isBlank()) {
                    throw new MrChatbotException(DEADLINE_DESCRIPTION_AND_BY_MISSING_MESSAGE);
                }
                throw new MrChatbotException(DEADLINE_BY_MISSING_MESSAGE);
            }
            if (byIndex <= CommandType.DEADLINE.withTrailingSpace().length()) {
                throw new MrChatbotException(DEADLINE_DESCRIPTION_MISSING_MESSAGE);
            }
            String description = input.substring(CommandType.DEADLINE.withTrailingSpace().length(), byIndex);
            String by = input.substring(byIndex + " /by ".length());
            if (description.isBlank() && by.isBlank()) {
                throw new MrChatbotException(DEADLINE_DESCRIPTION_AND_BY_MISSING_MESSAGE);
            }
            if (description.isBlank()) {
                throw new MrChatbotException(DEADLINE_DESCRIPTION_MISSING_MESSAGE);
            }
            if (by.isBlank()) {
                throw new MrChatbotException(DEADLINE_BY_MISSING_MESSAGE);
            }
            return new Deadline(description, parseDate(by, DEADLINE_DATE_FORMAT_MESSAGE));
        }

        if (commandType == CommandType.EVENT) {
            int fromIndex = lowerCaseInput.indexOf(" /from ");
            int toIndex = lowerCaseInput.indexOf(" /to ");
            String description = eventDescription(input, fromIndex, toIndex);
            boolean isDescriptionMissing = description.isBlank();
            boolean hasFrom = fromIndex != -1;
            boolean hasTo = toIndex != -1;
            boolean isFromMissing = !hasFrom;
            boolean isToMissing = !hasTo;
            String from = "";
            String to = "";

            if (hasFrom) {
                from = eventValue(input, fromIndex, " /from ".length(), toIndex);
                isFromMissing = from.isBlank();
            }
            if (hasTo) {
                to = eventValue(input, toIndex, " /to ".length(), fromIndex);
                isToMissing = to.isBlank();
            }

            if (isDescriptionMissing || isFromMissing || isToMissing) {
                throw new MrChatbotException(eventMissingMessage(isDescriptionMissing, isFromMissing, isToMissing));
            }
            return new Event(description,
                    parseDate(from, EVENT_DATE_FORMAT_MESSAGE),
                    parseDate(to, EVENT_DATE_FORMAT_MESSAGE));
        }

        throw new MrChatbotException(UNKNOWN_COMMAND_MESSAGE);
    }

    /**
     * Parses a date in the command format accepted by the chatbot.
     */
    private static LocalDate parseDate(String dateText, String errorMessage) throws MrChatbotException {
        try {
            return LocalDate.parse(dateText.trim());
        } catch (DateTimeParseException e) {
            throw new MrChatbotException(errorMessage);
        }
    }

    /**
     * Extracts the event description based on the first available event delimiter.
     */
    private static String eventDescription(String input, int fromIndex, int toIndex) {
        // CASE 1: if fromIndex comes first, check if there's description between COMMAND and fromIndex
        if (fromIndex != -1 && (toIndex == -1 || fromIndex < toIndex)) {
            if (fromIndex <= CommandType.EVENT.withTrailingSpace().length()) {
                return "";
            }
            return input.substring(CommandType.EVENT.withTrailingSpace().length(), fromIndex);
        }

        // CASE 2: if toIndex comes first, check if there's description between COMMAND and toIndex
        if (toIndex != -1) {
            if (toIndex <= CommandType.EVENT.withTrailingSpace().length()) {
                return "";
            }
            return input.substring(CommandType.EVENT.withTrailingSpace().length(), toIndex);
        }

        return input.substring(CommandType.EVENT.withTrailingSpace().length());
    }

    /**
     * Extracts an event value until the other event delimiter, if the other delimiter comes later.
     * Suppose we have <delimiter> <value> <otherDelimiter>
     * This function extracts <value>.
     *
     * `valueIndex`: the starting index of <delimiter>.
     */
    private static String eventValue(String input, int valueIndex, int delimiterLength, int otherIndex) {
        int valueStartIndex = valueIndex + delimiterLength;
        int valueEndIndex = otherIndex != -1 && otherIndex > valueIndex ? otherIndex : input.length();
        // handles the edge case where, after adding the delimiter, valueStartIndex exceeds valueEndIndex.
        if (valueStartIndex > valueEndIndex) {
            return "";
        }
        return input.substring(valueStartIndex, valueEndIndex);
    }

    /**
     * Creates a message that says which event fields are missing.
     */
    private static String eventMissingMessage(boolean isDescriptionMissing, boolean isFromMissing, boolean isToMissing) {
        String missingParts = "";
        if (isDescriptionMissing) {
            missingParts = "description";
        }
        if (isFromMissing) {
            missingParts = appendMissingPart(missingParts, "/from");
        }
        if (isToMissing) {
            missingParts = appendMissingPart(missingParts, "/to");
        }
        return "Event " + missingParts + " cannot be empty. " + EVENT_FORMAT_MESSAGE;
    }

    /**
     * Adds a missing part to a human-readable list.
     */
    private static String appendMissingPart(String missingParts, String newPart) {
        if (missingParts.isEmpty()) {
            return newPart;
        }
        if (missingParts.contains(" and ")) {
            return missingParts.replace(" and ", ", ") + ", and " + newPart;
        }
        return missingParts + " and " + newPart;
    }

    /**
     * Shows the user all accepted commands and their formats.
     */
    private static void printHelp() {
        System.out.println("Accepted inputs:");
        System.out.println("todo <description>");
        System.out.println("deadline <description> /by <yyyy-mm-dd>");
        System.out.println("event <description> /from <yyyy-mm-dd> /to <yyyy-mm-dd>");
        System.out.println("list");
        System.out.println("mark <task number>");
        System.out.println("unmark <task number>");
        System.out.println("delete <task number>");
        System.out.println("bye");
        System.out.println("help");
    }

    /**
     * Returns task or tasks depending on the task count.
     */
    private static String taskWord(int taskCount) {
        return taskCount == 1 ? "task" : "tasks";
    }

    /**
     * Reads a task number from a command argument.
     */
    private static int parseTaskNumber(String taskNumberText) throws MrChatbotException {
        if (taskNumberText.isBlank()) {
            throw new MrChatbotException("This task doesn't exist...");
        }
        try {
            return Integer.parseInt(taskNumberText.trim());
        } catch (NumberFormatException e) {
            throw new MrChatbotException("This task doesn't exist...");
        }
    }

    public static void main(String[] args) {
        String line = "____________________________________________________________";
        String banner = "                       _           _   _           _   \n"
                + " _ __ ___  _ __    ___| |__   __ _| |_| |__   ___ | |_ \n"
                + "| '_ ` _ \\| '__|  / __| '_ \\ / _` | __| '_ \\ / _ \\| __|\n"
                + "| | | | | | |    | (__| | | | (_| | |_| |_) | (_) | |_ \n"
                + "|_| |_| |_|_|     \\___|_| |_|\\__,_|\\__|_.__/ \\___/ \\__|\n";

        System.out.println(line);
        System.out.println(banner);
        System.out.println("Hello! I'm Mr Chatbot, your personal companion.");
        Scanner scanner = new Scanner(System.in);
        String name = "User";
        System.out.println("What can I do for you, Mr " + name + "?");
        System.out.println(line);

        Storage storage = new Storage(SAVE_FILE_PATH);
        // Store tasks in an ArrayList so tasks can be added and deleted easily.
        ArrayList<Task> tasks = new ArrayList<>();

        // Load tasks on startup
        try {
            tasks = storage.loadTasks();
        } catch (MrChatbotException e) {
            System.out.println(e.getMessage());
        }

        // read inputs
        while (scanner.hasNextLine()) {
            String input = scanner.nextLine();
            String lowerCaseInput = input.toLowerCase();
            System.out.println(line);
            if (CommandType.BYE.matchesExactly(lowerCaseInput)) {
                System.out.println("Bye Mr " + name + ". Hope to see you again soon!");
                System.out.println(line);
                break;
            }

            try {
                if (CommandType.HELP.matchesExactly(lowerCaseInput)) {
                    printHelp();
                } else if (CommandType.LIST.matchesExactly(lowerCaseInput)) {
                    System.out.println("Here are the tasks in your list:");
                    for (int i = 0; i < tasks.size(); i++) {
                        System.out.println((i + 1) + "." + tasks.get(i));
                    }
                } else if (lowerCaseInput.startsWith(CommandType.MARK.withTrailingSpace())) {
                    int taskNumber = parseTaskNumber(input.substring(CommandType.MARK.withTrailingSpace().length()));
                    if (taskNumber < 1 || taskNumber > tasks.size()) {
                        throw new MrChatbotException("This task doesn't exist...");
                    }
                    Task task = tasks.get(taskNumber - 1);
                    task.markAsDone();
                    storage.saveTasks(tasks);
                    System.out.println("Nice! I've marked this task as done:");
                    System.out.println("  " + task);
                } else if (lowerCaseInput.startsWith(CommandType.UNMARK.withTrailingSpace())) {
                    int taskNumber = parseTaskNumber(input.substring(CommandType.UNMARK.withTrailingSpace().length()));
                    if (taskNumber < 1 || taskNumber > tasks.size()) {
                        throw new MrChatbotException("This task doesn't exist...");
                    }
                    Task task = tasks.get(taskNumber - 1);
                    task.markAsNotDone();
                    storage.saveTasks(tasks);
                    System.out.println("OK, I've marked this task as not done yet:");
                    System.out.println("  " + task);
                } else if (lowerCaseInput.startsWith(CommandType.DELETE.withTrailingSpace())) {
                    int taskNumber = parseTaskNumber(input.substring(CommandType.DELETE.withTrailingSpace().length()));
                    if (taskNumber < 1 || taskNumber > tasks.size()) {
                        throw new MrChatbotException("This task doesn't exist...");
                    }
                    Task task = tasks.remove(taskNumber - 1);
                    storage.saveTasks(tasks);
                    System.out.println("Noted. I've removed this task:");
                    System.out.println("  " + task);
                    System.out.println("Now you have " + tasks.size() + " " + taskWord(tasks.size()) + " in the list.");
                } else if (tasks.size() == MAX_TASKS) {
                    throw new MrChatbotException(
                            "Sorry, Mr " + name + ", your task list is full. No more tasks can be added.");
                } else {
                    Task task = createTask(input);
                    tasks.add(task);
                    storage.saveTasks(tasks);
                    System.out.println("Got it. I've added this task:");
                    System.out.println("  " + task);
                    System.out.println("Now you have " + tasks.size() + " " + taskWord(tasks.size()) + " in the list.");
                }
            } catch (MrChatbotException e) {
                System.out.println(e.getMessage());
            }
            System.out.println(line);
        }
    }
}

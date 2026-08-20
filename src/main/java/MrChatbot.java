import java.util.Scanner;

public class MrChatbot {
    private static final int MAX_TASKS = 100;
    private static final String TODO_COMMAND = "todo ";
    private static final String DEADLINE_COMMAND = "deadline ";
    private static final String EVENT_COMMAND = "event ";
    private static final String INVALID_TASK_FORMAT_MESSAGE = "Sorry, I don't understand that task format.";
    private static final String UNKNOWN_COMMAND_MESSAGE = "Sorry, I don't understand that command. Please type \"help\".";
    private static final String TODO_FORMAT_MESSAGE = "Todo description cannot be empty. Please use the format: todo <description>";
    private static final String DEADLINE_FORMAT_MESSAGE = "Please use the format: deadline <description> /by <deadline>";
    private static final String DEADLINE_DESCRIPTION_MISSING_MESSAGE =
            "Deadline description cannot be empty. " + DEADLINE_FORMAT_MESSAGE;
    private static final String DEADLINE_BY_MISSING_MESSAGE =
            "Deadline /by cannot be empty. " + DEADLINE_FORMAT_MESSAGE;
    private static final String DEADLINE_DESCRIPTION_AND_BY_MISSING_MESSAGE =
            "Deadline description and /by cannot be empty. " + DEADLINE_FORMAT_MESSAGE;
    private static final String EVENT_FORMAT_MESSAGE = "Please use the format: event <description> /from <start> /to <end>";

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

        if (lowerCaseInput.equals("todo")) {
            throw new MrChatbotException(TODO_FORMAT_MESSAGE);
        }

        if (lowerCaseInput.equals("deadline")) {
            throw new MrChatbotException(DEADLINE_DESCRIPTION_AND_BY_MISSING_MESSAGE);
        }

        if (lowerCaseInput.equals("event")) {
            throw new MrChatbotException(eventMissingMessage(true, true, true));
        }

        if (lowerCaseInput.equals("mark") || lowerCaseInput.equals("unmark")) {
            throw new MrChatbotException(INVALID_TASK_FORMAT_MESSAGE);
        }

        if (lowerCaseInput.startsWith(TODO_COMMAND)) {
            String description = input.substring(TODO_COMMAND.length());
            if (description.isBlank()) {
                throw new MrChatbotException(TODO_FORMAT_MESSAGE);
            }
            return new Todo(description);
        }

        if (lowerCaseInput.startsWith(DEADLINE_COMMAND)) {
            int byIndex = lowerCaseInput.indexOf(" /by ");
            if (byIndex == -1) {
                String description = input.substring(DEADLINE_COMMAND.length());
                if (description.isBlank()) {
                    throw new MrChatbotException(DEADLINE_DESCRIPTION_AND_BY_MISSING_MESSAGE);
                }
                throw new MrChatbotException(DEADLINE_BY_MISSING_MESSAGE);
            }
            if (byIndex <= DEADLINE_COMMAND.length()) {
                throw new MrChatbotException(DEADLINE_DESCRIPTION_MISSING_MESSAGE);
            }
            String description = input.substring(DEADLINE_COMMAND.length(), byIndex);
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
            return new Deadline(description, by);
        }

        if (lowerCaseInput.startsWith(EVENT_COMMAND)) {
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
            return new Event(description, from, to);
        }

        throw new MrChatbotException(UNKNOWN_COMMAND_MESSAGE);
    }

    /**
     * Extracts the event description based on the first available event delimiter.
     */
    private static String eventDescription(String input, int fromIndex, int toIndex) {
        // CASE 1: if fromIndex comes first, check if there's description between COMMAND and fromIndex
        if (fromIndex != -1 && (toIndex == -1 || fromIndex < toIndex)) {
            if (fromIndex <= EVENT_COMMAND.length()) {
                return "";
            }
            return input.substring(EVENT_COMMAND.length(), fromIndex);
        }

        // CASE 2: if toIndex comes first, check if there's description between COMMAND and toIndex
        if (toIndex != -1) {
            if (toIndex <= EVENT_COMMAND.length()) {
                return "";
            }
            return input.substring(EVENT_COMMAND.length(), toIndex);
        }

        return input.substring(EVENT_COMMAND.length());
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
        System.out.println("deadline <description> /by <deadline>");
        System.out.println("event <description> /from <start> /to <end>");
        System.out.println("list");
        System.out.println("mark <task number>");
        System.out.println("unmark <task number>");
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
//        System.out.println("What is your name?");
        Scanner scanner = new Scanner(System.in);
        String name = "User";
        System.out.println("What can I do for you, Mr " + name + "?");
        System.out.println(line);

        // Store tasks in a fixed-size array.
        Task[] tasks = new Task[MAX_TASKS];
        int taskCount = 0;
        while (scanner.hasNextLine()) {
            String input = scanner.nextLine();
            String lowerCaseInput = input.toLowerCase();
            System.out.println(line);
            if (input.equalsIgnoreCase("bye")) {
                System.out.println("Bye Mr " + name + ". Hope to see you again soon!");
                System.out.println(line);
                break;
            }

            try {
                if (input.equalsIgnoreCase("help")) {
                    printHelp();
                } else if (input.equalsIgnoreCase("list")) {
                    System.out.println("Here are the tasks in your list:");
                    for (int i = 0; i < taskCount; i++) {
                        System.out.println((i + 1) + "." + tasks[i]);
                    }
                } else if (lowerCaseInput.startsWith("mark ")) {
                    int taskNumber = parseTaskNumber(input.substring(5));
                    if (taskNumber < 1 || taskNumber > taskCount) {
                        throw new MrChatbotException("This task doesn't exist...");
                    }
                    Task task = tasks[taskNumber - 1];
                    task.markAsDone();
                    System.out.println("Nice! I've marked this task as done:");
                    System.out.println("  " + task);
                } else if (lowerCaseInput.startsWith("unmark ")) {
                    int taskNumber = parseTaskNumber(input.substring(7));
                    if (taskNumber < 1 || taskNumber > taskCount) {
                        throw new MrChatbotException("This task doesn't exist...");
                    }
                    Task task = tasks[taskNumber - 1];
                    task.markAsNotDone();
                    System.out.println("OK, I've marked this task as not done yet:");
                    System.out.println("  " + task);
                } else if (taskCount == MAX_TASKS) {
                    throw new MrChatbotException(
                            "Sorry, Mr " + name + ", your task list is full. No more tasks can be added.");
                } else {
                    Task task = createTask(input);
                    tasks[taskCount] = task;
                    taskCount++;
                    System.out.println("Got it. I've added this task:");
                    System.out.println("  " + task);
                    System.out.println("Now you have " + taskCount + " " + taskWord(taskCount) + " in the list.");
                }
            } catch (MrChatbotException e) {
                System.out.println(e.getMessage());
            }
            System.out.println(line);
        }
    }
}

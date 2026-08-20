import java.util.Scanner;

public class MrChatbot {
    private static final int MAX_TASKS = 100;
    private static final String TODO_COMMAND = "todo ";
    private static final String DEADLINE_COMMAND = "deadline ";
    private static final String EVENT_COMMAND = "event ";
    private static final String INVALID_TASK_FORMAT_MESSAGE = "Sorry, I don't understand that task format.";
    private static final String UNKNOWN_COMMAND_MESSAGE = "Sorry, I don't understand that command. Please type \"help\".";
    private static final String TODO_FORMAT_MESSAGE = "Please use the format: todo <description>";
    private static final String DEADLINE_FORMAT_MESSAGE = "Please use the format: deadline <description> /by <deadline>";
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
    private static Task createTask(String input) {
        String lowerCaseInput = input.toLowerCase();

        if (lowerCaseInput.equals("todo")) {
            System.out.println(TODO_FORMAT_MESSAGE);
            return null;
        }

        if (lowerCaseInput.equals("deadline")) {
            System.out.println(DEADLINE_FORMAT_MESSAGE);
            return null;
        }

        if (lowerCaseInput.equals("event")) {
            System.out.println(EVENT_FORMAT_MESSAGE);
            return null;
        }

        if (lowerCaseInput.equals("mark") || lowerCaseInput.equals("unmark")) {
            System.out.println(INVALID_TASK_FORMAT_MESSAGE);
            return null;
        }

        if (lowerCaseInput.startsWith(TODO_COMMAND)) {
            String description = input.substring(TODO_COMMAND.length());
            if (description.isBlank()) {
                System.out.println(TODO_FORMAT_MESSAGE);
                return null;
            }
            return new Todo(description);
        }

        if (lowerCaseInput.startsWith(DEADLINE_COMMAND)) {
            int byIndex = lowerCaseInput.indexOf(" /by ");
            if (byIndex <= DEADLINE_COMMAND.length()) {
                System.out.println(DEADLINE_FORMAT_MESSAGE);
                return null;
            }
            String description = input.substring(DEADLINE_COMMAND.length(), byIndex);
            String by = input.substring(byIndex + " /by ".length());
            if (description.isBlank() || by.isBlank()) {
                System.out.println(DEADLINE_FORMAT_MESSAGE);
                return null;
            }
            return new Deadline(description, by);
        }

        if (lowerCaseInput.startsWith(EVENT_COMMAND)) {
            int fromIndex = lowerCaseInput.indexOf(" /from ");
            int toIndex = lowerCaseInput.indexOf(" /to ");
            if (fromIndex <= EVENT_COMMAND.length() || toIndex == -1 || fromIndex > toIndex) {
                System.out.println(EVENT_FORMAT_MESSAGE);
                return null;
            }
            String description = input.substring(EVENT_COMMAND.length(), fromIndex);
            String from = input.substring(fromIndex + " /from ".length(), toIndex);
            String to = input.substring(toIndex + " /to ".length());
            if (description.isBlank() || from.isBlank() || to.isBlank()) {
                System.out.println(EVENT_FORMAT_MESSAGE);
                return null;
            }
            return new Event(description, from, to);
        }

        System.out.println(UNKNOWN_COMMAND_MESSAGE);
        return null;
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
    private static int parseTaskNumber(String taskNumberText) {
        if (taskNumberText.isBlank()) {
            return -1;
        }
        try {
            return Integer.parseInt(taskNumberText.trim());
        } catch (NumberFormatException e) {
            return -1;
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
                    System.out.println("This task doesn't exist...");
                    continue;
                }
                Task task = tasks[taskNumber - 1];
                task.markAsDone();
                System.out.println("Nice! I've marked this task as done:");
                System.out.println("  " + task);
            } else if (lowerCaseInput.startsWith("unmark ")) {
                int taskNumber = parseTaskNumber(input.substring(7));
                if (taskNumber < 1 || taskNumber > taskCount) {
                    System.out.println("This task doesn't exist...");
                    continue;
                }
                Task task = tasks[taskNumber - 1];
                task.markAsNotDone();
                System.out.println("OK, I've marked this task as not done yet:");
                System.out.println("  " + task);
            } else if (taskCount == MAX_TASKS) {
                System.out.println("Sorry, Mr " + name + ", your task list is full. No more tasks can be added.");
            } else {
                Task task = createTask(input);
                if (task == null) {
                    System.out.println(line);
                    continue;
                }
                tasks[taskCount] = task;
                taskCount++;
                System.out.println("Got it. I've added this task:");
                System.out.println("  " + task);
                System.out.println("Now you have " + taskCount + " " + taskWord(taskCount) + " in the list.");
            }
            System.out.println(line);
        }
    }
}

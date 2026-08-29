import java.util.ArrayList;
import java.util.Scanner;

/**
 * Handles all interactions with the user.
 */
public class Ui {
    private static final String LINE = "____________________________________________________________";
    private static final String BANNER = "                       _           _   _           _   \n"
            + " _ __ ___  _ __    ___| |__   __ _| |_| |__   ___ | |_ \n"
            + "| '_ ` _ \\| '__|  / __| '_ \\ / _` | __| '_ \\ / _ \\| __|\n"
            + "| | | | | | |    | (__| | | | (_| | |_| |_) | (_) | |_ \n"
            + "|_| |_| |_|_|     \\___|_| |_|\\__,_|\\__|_.__/ \\___/ \\__|\n";

    private final Scanner scanner;

    /**
     * Creates a UI helper that reads user commands from standard input.
     */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /**
     * Returns true if another user command is available.
     */
    public boolean hasNextInput() {
        return scanner.hasNextLine();
    }

    /**
     * Reads the next user command.
     */
    public String readInput() {
        return scanner.nextLine();
    }

    /**
     * Shows the startup banner and greeting.
     */
    public void showWelcome(String name) {
        System.out.println(LINE);
        System.out.println(BANNER);
        System.out.println("Hello! I'm Mr Chatbot, your personal companion.");
        System.out.println("What can I do for you, Mr " + name + "?");
        System.out.println(LINE);
    }

    /**
     * Shows a divider before or after a command response.
     */
    public void showLine() {
        System.out.println(LINE);
    }

    /**
     * Shows the exit message.
     */
    public void showBye(String name) {
        System.out.println("Bye Mr " + name + ". Hope to see you again soon!");
    }

    /**
     * Shows an error message.
     */
    public void showError(String message) {
        System.out.println(message);
    }

    /**
     * Shows the user all accepted commands and their formats.
     */
    public void showHelp() {
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
     * Shows all tasks in the list.
     */
    public void showTaskList(ArrayList<Task> tasks) {
        System.out.println("Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + "." + tasks.get(i));
        }
    }

    /**
     * Shows the message for a newly added task.
     */
    public void showTaskAdded(Task task, int taskCount) {
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + taskCount + " " + taskWord(taskCount) + " in the list.");
    }

    /**
     * Shows the message for a marked task.
     */
    public void showTaskMarked(Task task) {
        System.out.println("Nice! I've marked this task as done:");
        System.out.println("  " + task);
    }

    /**
     * Shows the message for an unmarked task.
     */
    public void showTaskUnmarked(Task task) {
        System.out.println("OK, I've marked this task as not done yet:");
        System.out.println("  " + task);
    }

    /**
     * Shows the message for a deleted task.
     */
    public void showTaskDeleted(Task task, int taskCount) {
        System.out.println("Noted. I've removed this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + taskCount + " " + taskWord(taskCount) + " in the list.");
    }

    /**
     * Returns task or tasks depending on the task count.
     */
    private String taskWord(int taskCount) {
        return taskCount == 1 ? "task" : "tasks";
    }
}

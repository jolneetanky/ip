import java.util.Scanner;

public class MrChatbot {
    private static final int MAX_TASKS = 100;

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
        String line = "____________________________________________________________";
        String banner = "                       _           _   _           _   \n"
                + " _ __ ___  _ __    ___| |__   __ _| |_| |__   ___ | |_ \n"
                + "| '_ ` _ \\| '__|  / __| '_ \\ / _` | __| '_ \\ / _ \\| __|\n"
                + "| | | | | | |    | (__| | | | (_| | |_| |_) | (_) | |_ \n"
                + "|_| |_| |_|_|     \\___|_| |_|\\__,_|\\__|_.__/ \\___/ \\__|\n";

        System.out.println(line);
        System.out.println(banner);
        System.out.println("Hello! I'm Mr Chatbot, your personal companion.");
        System.out.println("What is your name?");
        Scanner scanner = new Scanner(System.in);
        String name = toTitleCase(scanner.nextLine());
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

            if (input.equalsIgnoreCase("list")) {
                System.out.println("Here are the tasks in your list:");
                for (int i = 0; i < taskCount; i++) {
                    System.out.println((i + 1) + "." + tasks[i]);
                }
            } else if (lowerCaseInput.startsWith("mark ")) {
                int taskNumber = Integer.parseInt(input.substring(5));
                if (taskNumber < 1 || taskNumber > taskCount) {
                    System.out.println("This task doesn't exist...");
                    continue;
                }
                Task task = tasks[taskNumber - 1];
                task.markAsDone();
                System.out.println("Nice! I've marked this task as done:");
                System.out.println("  " + task);
            } else if (lowerCaseInput.startsWith("unmark ")) {
                int taskNumber = Integer.parseInt(input.substring(7));
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
                tasks[taskCount] = new Task(input);
                taskCount++;
                System.out.println("added: " + input);
            }
            System.out.println(line);
        }
    }
}

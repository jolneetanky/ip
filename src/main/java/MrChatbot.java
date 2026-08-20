import java.util.Scanner;

public class MrChatbot {
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
        System.out.println("What can I do for you?");
        System.out.println(line);

        // Read user inputs in a loop until they say "bye"
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String input = scanner.nextLine();
            System.out.println(line);
            if (input.equalsIgnoreCase("bye")) {
                System.out.println("Bye. Hope to see you again soon!");
                System.out.println(line);
                break;
            }
            System.out.println(input);
            System.out.println(line);
        }
    }
}

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Makes sense of user commands and converts task commands into task objects.
 */
public class Parser {
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
     * Identifies the command word used by the user.
     */
    public CommandType parseCommandType(String input) {
        return CommandType.from(input);
    }

    /**
     * Creates a task from the user's command.
     */
    public Task createTask(String input) throws MrChatbotException {
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
     * Reads a task number from a command argument.
     */
    public int parseTaskNumber(String taskNumberText) throws MrChatbotException {
        if (taskNumberText.isBlank()) {
            throw new MrChatbotException("This task doesn't exist...");
        }
        try {
            return Integer.parseInt(taskNumberText.trim());
        } catch (NumberFormatException e) {
            throw new MrChatbotException("This task doesn't exist...");
        }
    }

    /**
     * Extracts the text after the command word.
     */
    public String commandArgument(String input, CommandType commandType) throws MrChatbotException {
        if (input.toLowerCase().equals(commandType.word)) {
            throw new MrChatbotException(INVALID_TASK_FORMAT_MESSAGE);
        }
        return input.substring(commandType.withTrailingSpace().length());
    }

    /**
     * Parses a date in the command format accepted by the chatbot.
     */
    private LocalDate parseDate(String dateText, String errorMessage) throws MrChatbotException {
        try {
            return LocalDate.parse(dateText.trim());
        } catch (DateTimeParseException e) {
            throw new MrChatbotException(errorMessage);
        }
    }

    /**
     * Extracts the event description based on the first available event delimiter.
     */
    private String eventDescription(String input, int fromIndex, int toIndex) {
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
    private String eventValue(String input, int valueIndex, int delimiterLength, int otherIndex) {
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
    private String eventMissingMessage(boolean isDescriptionMissing, boolean isFromMissing, boolean isToMissing) {
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
    private String appendMissingPart(String missingParts, String newPart) {
        if (missingParts.isEmpty()) {
            return newPart;
        }
        if (missingParts.contains(" and ")) {
            return missingParts.replace(" and ", ", ") + ", and " + newPart;
        }
        return missingParts + " and " + newPart;
    }
}

/**
 * Represents the command words accepted by the chatbot.
 */
enum CommandType {
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

    final String word;

    CommandType(String word) {
        this.word = word;
    }

    boolean matches(String lowerCaseInput) {
        if (requiresExactMatch()) {
            return lowerCaseInput.equals(word);
        }
        return lowerCaseInput.equals(word) || lowerCaseInput.startsWith(word + " ");
    }

    String withTrailingSpace() {
        return word + " ";
    }

    private boolean requiresExactMatch() {
        return this == LIST || this == HELP || this == BYE;
    }

    static CommandType from(String input) {
        String lowerCaseInput = input.toLowerCase();
        for (CommandType commandType : values()) {
            if (commandType != UNKNOWN && commandType.matches(lowerCaseInput)) {
                return commandType;
            }
        }
        return UNKNOWN;
    }
}

package mrchatbot.parser;

/**
 * Represents the command words accepted by the chatbot.
 */
public enum CommandType {
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

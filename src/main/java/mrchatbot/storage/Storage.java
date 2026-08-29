package mrchatbot.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import mrchatbot.exception.MrChatbotException;
import mrchatbot.task.Deadline;
import mrchatbot.task.Event;
import mrchatbot.task.Task;
import mrchatbot.task.TaskList;
import mrchatbot.task.Todo;

/**
 * Handles saving tasks to, and loading tasks from, the hard disk.
 */
public class Storage {
    private static final String LOAD_ERROR_MESSAGE = "Sorry, I could not load your tasks.";
    private static final String SAVE_ERROR_MESSAGE = "Sorry, I could not save your tasks.";

    private final Path filePath;

    /**
     * Creates a storage helper that reads and writes the given file.
     */
    public Storage(String filePath) {
        this.filePath = Path.of(filePath);
    }

    /**
     * Loads tasks from disk, or returns an empty list if the save file does not exist.
     */
    public ArrayList<Task> loadTasks() throws MrChatbotException {
        ArrayList<Task> tasks = new ArrayList<>();
        if (!Files.exists(filePath)) {
            return tasks;
        }

        if (Files.isDirectory(filePath)) {
            throw new MrChatbotException(LOAD_ERROR_MESSAGE);
        }

        try {
            List<String> lines = Files.readAllLines(filePath);
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                if (!line.isBlank()) {
                    tasks.add(parseTask(line));
                }
            }
            return tasks;
        } catch (IOException e) {
            throw new MrChatbotException(LOAD_ERROR_MESSAGE);
        }
    }

    /**
     * Saves all tasks to disk using one line per task.
     */
    public void saveTasks(TaskList tasks) throws MrChatbotException {
        Path tempFile = null;
        try {
            Path parentDirectory = filePath.getParent();
            if (parentDirectory != null) {
                Files.createDirectories(parentDirectory);
            }
            ArrayList<String> taskLines = new ArrayList<>();
            for (Task task : tasks.asArrayList()) {
                taskLines.add(toStorageString(task));
            }
            if (parentDirectory == null) {
                tempFile = Files.createTempFile("duke", ".tmp");
            } else {
                tempFile = Files.createTempFile(parentDirectory, "duke", ".tmp");
            }
            Files.write(tempFile, taskLines);
            Files.move(tempFile, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            if (tempFile != null) {
                try {
                    Files.deleteIfExists(tempFile);
                } catch (IOException ignored) {
                    // The original save failure is the useful error for the user.
                }
            }
            throw new MrChatbotException(SAVE_ERROR_MESSAGE);
        }
    }

    /**
     * Converts one saved line into a task.
     */
    private Task parseTask(String line) throws MrChatbotException {
        ArrayList<String> parts = splitFields(line);
        if (parts.size() < 3) {
            throw new MrChatbotException(LOAD_ERROR_MESSAGE);
        }

        Task task;
        String taskType = parts.get(0);
        String status = parts.get(1);
        if (!status.equals("0") && !status.equals("1")) {
            throw new MrChatbotException(LOAD_ERROR_MESSAGE);
        }

        if (taskType.equals("T") && parts.size() == 3) {
            task = new Todo(parts.get(2));
        } else if (taskType.equals("D") && parts.size() == 4) {
            task = new Deadline(parts.get(2), parseDate(parts.get(3)));
        } else if (taskType.equals("E") && parts.size() == 5) {
            task = new Event(parts.get(2), parseDate(parts.get(3)), parseDate(parts.get(4)));
        } else {
            throw new MrChatbotException(LOAD_ERROR_MESSAGE);
        }

        if (hasBlankRequiredField(parts)) {
            throw new MrChatbotException(LOAD_ERROR_MESSAGE);
        }

        if (status.equals("1")) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Splits one storage line by unescaped pipe characters.
     * Parses the line (disk representation) into an in-memory representation.
     */
    private ArrayList<String> splitFields(String line) throws MrChatbotException {
        ArrayList<String> fields = new ArrayList<>();
        String field = "";
        boolean isEscaped = false;

        for (int i = 0; i < line.length(); i++) {
            char character = line.charAt(i);
            // Only two escaped characters are allowed: \| and \\.
            if (isEscaped) {
                if (character != '\\' && character != '|') {
                    throw new MrChatbotException(LOAD_ERROR_MESSAGE);
                }
                field += character;
                isEscaped = false;
            } else if (character == '\\') {
                isEscaped = true;
            } else if (character == '|') {
                fields.add(field.trim());
                field = "";
            } else {
                field += character;
            }
        }

        if (isEscaped) {
            throw new MrChatbotException(LOAD_ERROR_MESSAGE);
        }
        fields.add(field.trim());
        return fields;
    }

    /**
     * Returns true if any field that forms a task is empty.
     */
    private boolean hasBlankRequiredField(ArrayList<String> parts) {
        for (String part : parts) {
            if (part.isBlank()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Converts one task into the line format used for saving to disk.
     */
    private String toStorageString(Task task) {
        if (task instanceof Deadline) {
            Deadline deadline = (Deadline) task;
            return joinFields("D", statusOf(task), deadline.getDescription(), deadline.getBy().toString());
        }
        if (task instanceof Event) {
            Event event = (Event) task;
            return joinFields("E", statusOf(task), event.getDescription(),
                    event.getFrom().toString(), event.getTo().toString());
        }
        return joinFields("T", statusOf(task), task.getDescription());
    }

    /**
     * Parses a saved ISO date into a LocalDate.
     */
    private LocalDate parseDate(String dateText) throws MrChatbotException {
        try {
            return LocalDate.parse(dateText);
        } catch (DateTimeParseException e) {
            throw new MrChatbotException(LOAD_ERROR_MESSAGE);
        }
    }

    /**
     * Returns 1 if the task is done, or 0 otherwise.
     */
    private String statusOf(Task task) {
        return task.isDone() ? "1" : "0";
    }

    /**
     * Joins escaped fields using the storage delimiter.
     */
    private String joinFields(String... fields) {
        String line = "";
        for (String field : fields) {
            if (!line.isEmpty()) {
                line += " | ";
            }
            line += escape(field);
        }
        return line;
    }

    /**
     * Escapes characters that have a special meaning in the storage format.
     */
    private String escape(String text) {
        return text.replace("\\", "\\\\").replace("|", "\\|");
    }
}

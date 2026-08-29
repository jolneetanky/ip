package mrchatbot.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import mrchatbot.exception.MrChatbotException;
import mrchatbot.task.Deadline;
import mrchatbot.task.Event;
import mrchatbot.task.Task;
import mrchatbot.task.TaskList;
import mrchatbot.task.Todo;

public class StorageTest {
    @TempDir
    private Path tempDir;

    @Test
    public void saveTasks_taskWithSpecialCharacters_escapesSpecialCharacters() throws Exception {
        Path filePath = tempDir.resolve("duke.txt");
        Storage storage = new Storage(filePath.toString());
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read | book \\ notes"));

        storage.saveTasks(tasks);

        assertEquals("T | 0 | read \\| book \\\\ notes" + System.lineSeparator(), Files.readString(filePath));
    }

    @Test
    public void saveTasks_nestedMissingDirectory_createsDirectoryAndFile() throws Exception {
        Path filePath = tempDir.resolve("data").resolve("duke.txt");
        Storage storage = new Storage(filePath.toString());
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        storage.saveTasks(tasks);

        assertEquals("T | 0 | read book" + System.lineSeparator(), Files.readString(filePath));
    }

    @Test
    public void loadTasks_missingFile_emptyListReturned() throws MrChatbotException {
        Path filePath = tempDir.resolve("duke.txt");
        Storage storage = new Storage(filePath.toString());

        ArrayList<Task> tasks = storage.loadTasks();

        assertTrue(tasks.isEmpty());
    }

    @Test
    public void loadTasks_validTodoDeadlineEvent_tasksRestored() throws Exception {
        Path filePath = tempDir.resolve("duke.txt");
        Files.writeString(filePath, "T | 1 | read book" + System.lineSeparator()
                + "D | 0 | return book | 2019-12-01" + System.lineSeparator()
                + "E | 0 | project meeting | 2019-12-01 | 2019-12-02" + System.lineSeparator());
        Storage storage = new Storage(filePath.toString());

        ArrayList<Task> tasks = storage.loadTasks();

        assertEquals(3, tasks.size());
        assertEquals("[T][X] read book", tasks.get(0).toString());
        assertEquals("[D][ ] return book (by: Dec 1 2019)", tasks.get(1).toString());
        assertEquals("[E][ ] project meeting (from: Dec 1 2019 to: Dec 2 2019)", tasks.get(2).toString());
    }

    @Test
    public void loadTasks_escapedSpecialCharacters_taskRestored() throws Exception {
        Path filePath = tempDir.resolve("duke.txt");
        Files.writeString(filePath, "T | 0 | read \\| book \\\\ notes" + System.lineSeparator());
        Storage storage = new Storage(filePath.toString());

        ArrayList<Task> tasks = storage.loadTasks();

        assertEquals(1, tasks.size());
        assertEquals("[T][ ] read | book \\ notes", tasks.get(0).toString());
    }

    @Test
    public void loadTasks_malformedTaskType_exceptionThrown() throws Exception {
        Path filePath = tempDir.resolve("duke.txt");
        Files.writeString(filePath, "X | 0 | read book" + System.lineSeparator());
        Storage storage = new Storage(filePath.toString());

        MrChatbotException exception = assertThrows(MrChatbotException.class, storage::loadTasks);
        assertEquals("Sorry, I could not load your tasks.", exception.getMessage());
    }

    @Test
    public void loadTasks_invalidEscapedCharacter_exceptionThrown() throws Exception {
        Path filePath = tempDir.resolve("duke.txt");
        Files.writeString(filePath, "T | 0 | read \\n book" + System.lineSeparator());
        Storage storage = new Storage(filePath.toString());

        MrChatbotException exception = assertThrows(MrChatbotException.class, storage::loadTasks);
        assertEquals("Sorry, I could not load your tasks.", exception.getMessage());
    }

    @Test
    public void saveAndLoadTasks_todoDeadlineEvent_tasksRoundTripped() throws Exception {
        Path filePath = tempDir.resolve("duke.txt");
        Storage storage = new Storage(filePath.toString());
        TaskList tasks = new TaskList();
        Todo todo = new Todo("read book");
        Deadline deadline = new Deadline("return book", LocalDate.parse("2019-12-01"));
        Event event = new Event("project meeting", LocalDate.parse("2019-12-01"), LocalDate.parse("2019-12-02"));
        deadline.markAsDone();
        tasks.add(todo);
        tasks.add(deadline);
        tasks.add(event);

        storage.saveTasks(tasks);
        ArrayList<Task> loadedTasks = storage.loadTasks();

        assertEquals(3, loadedTasks.size());
        assertEquals("[T][ ] read book", loadedTasks.get(0).toString());
        assertEquals("[D][X] return book (by: Dec 1 2019)", loadedTasks.get(1).toString());
        assertEquals("[E][ ] project meeting (from: Dec 1 2019 to: Dec 2 2019)", loadedTasks.get(2).toString());
    }
}

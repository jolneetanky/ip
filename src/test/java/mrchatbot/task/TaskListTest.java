package mrchatbot.task;

import org.junit.jupiter.api.Test;

import mrchatbot.exception.MrChatbotException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TaskListTest {
    @Test
    public void isFull_emptyList_false() {
        TaskList tasks = new TaskList();

        assertFalse(tasks.isFull(1));
    }

    @Test
    public void isFull_taskCountBelowLimit_false() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        assertFalse(tasks.isFull(2));
    }

    @Test
    public void isFull_taskCountAtLimit_true() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        assertTrue(tasks.isFull(1));
    }

    @Test
    public void isFull_taskCountAboveLimit_true() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("return book"));

        assertTrue(tasks.isFull(1));
    }

    @Test
    public void mark_existingTask_taskMarkedAndReturned() throws MrChatbotException {
        TaskList tasks = new TaskList();
        Todo task = new Todo("read book");
        tasks.add(task);

        Task markedTask = tasks.mark(1);

        assertSame(task, markedTask);
        assertEquals("[T][X] read book", task.toString());
    }

    @Test
    public void unmark_existingDoneTask_taskUnmarkedAndReturned() throws MrChatbotException {
        TaskList tasks = new TaskList();
        Todo task = new Todo("read book");
        task.markAsDone();
        tasks.add(task);

        Task unmarkedTask = tasks.unmark(1);

        assertSame(task, unmarkedTask);
        assertEquals("[T][ ] read book", task.toString());
    }

    @Test
    public void delete_existingTask_taskRemovedAndReturned() throws MrChatbotException {
        TaskList tasks = new TaskList();
        Todo firstTask = new Todo("read book");
        Todo secondTask = new Todo("return book");
        tasks.add(firstTask);
        tasks.add(secondTask);

        Task deletedTask = tasks.delete(1);

        assertSame(firstTask, deletedTask);
        assertEquals(1, tasks.size());
        assertSame(secondTask, tasks.get(0));
    }

    @Test
    public void mark_zeroTaskNumber_exceptionThrown() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        MrChatbotException exception = assertThrows(MrChatbotException.class, () -> tasks.mark(0));
        assertEquals("This task doesn't exist...", exception.getMessage());
    }

    @Test
    public void delete_taskNumberAboveSize_exceptionThrown() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        MrChatbotException exception = assertThrows(MrChatbotException.class, () -> tasks.delete(2));
        assertEquals("This task doesn't exist...", exception.getMessage());
    }
}

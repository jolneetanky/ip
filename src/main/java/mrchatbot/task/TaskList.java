package mrchatbot.task;

import java.util.ArrayList;

import mrchatbot.exception.MrChatbotException;

/**
 * Contains the user's tasks and provides operations on the task list.
 */
public class TaskList {
    private static final String TASK_NOT_FOUND_MESSAGE = "This task doesn't exist...";

    private final ArrayList<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    /**
     * Creates a task list using tasks loaded from storage.
     */
    public TaskList(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    /**
     * Returns the number of tasks in the list.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns true if the task list has reached the given limit.
     */
    public boolean isFull(int maxTasks) {
        return tasks.size() == maxTasks;
    }

    /**
     * Adds a new task to the list.
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Marks the task at the given one-based task number as done.
     */
    public Task mark(int taskNumber) throws MrChatbotException {
        Task task = getTask(taskNumber);
        task.markAsDone();
        return task;
    }

    /**
     * Marks the task at the given one-based task number as not done.
     */
    public Task unmark(int taskNumber) throws MrChatbotException {
        Task task = getTask(taskNumber);
        task.markAsNotDone();
        return task;
    }

    /**
     * Deletes and returns the task at the given one-based task number.
     */
    public Task delete(int taskNumber) throws MrChatbotException {
        validateTaskNumber(taskNumber);
        return tasks.remove(taskNumber - 1);
    }

    /**
     * Returns the task at the given zero-based index for display.
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Returns the task list in the format used by Storage.
     */
    public ArrayList<Task> asArrayList() {
        return tasks;
    }

    /**
     * Returns the task at the given one-based task number.
     */
    private Task getTask(int taskNumber) throws MrChatbotException {
        validateTaskNumber(taskNumber);
        return tasks.get(taskNumber - 1);
    }

    /**
     * Checks that a one-based task number refers to an existing task.
     */
    private void validateTaskNumber(int taskNumber) throws MrChatbotException {
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new MrChatbotException(TASK_NOT_FOUND_MESSAGE);
        }
    }
}

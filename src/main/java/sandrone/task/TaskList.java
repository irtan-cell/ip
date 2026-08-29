package sandrone.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import sandrone.SandroneException;

/** Stores and manages the tasks in the current chatbot session. */
public class TaskList {
    private static final int MAX_TASKS = 10;
    private final ArrayList<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    /** Returns the number of tasks in this list. */
    public int size() {
        return tasks.size();
    }

    /** Returns whether this list has reached its task limit. */
    public boolean isFull() {
        return size() >= MAX_TASKS;
    }

    /** Adds a task unless the task limit has been reached. */
    public void addTask(Task task) throws SandroneException {
        if (isFull()) {
            throw new SandroneException("Cannot exceed maximum number of tasks");
        }
        tasks.add(task);
    }

    /** Returns the task at the specified zero-based index. */
    public Task getTask(int index) {
        return tasks.get(index);
    }

    /** Returns the task identified by a one-based user task number. */
    public Task getTaskByNumber(int taskNumber) throws SandroneException {
        if (taskNumber <= 0 || taskNumber > size()) {
            throw new SandroneException("Invalid task index");
        }
        return getTask(taskNumber - 1);
    }

    /** Marks the specified task as done and returns it. */
    public Task markTask(int index) {
        Task task = getTask(index);
        task.markAsDone();
        return task;
    }

    /** Marks the specified task as not done and returns it. */
    public Task unmarkTask(int index) {
        Task task = getTask(index);
        task.markAsNotDone();
        return task;
    }

    /** Removes and returns the task at the specified zero-based index. */
    public Task removeTask(int index) {
        return tasks.remove(index);
    }

    /** Removes and returns the task identified by a one-based user task number. */
    public Task removeTaskByNumber(int taskNumber) throws SandroneException {
        getTaskByNumber(taskNumber);
        return removeTask(taskNumber - 1);
    }

    /**
     * Returns tasks whose descriptions contain the keyword, ignoring case.
     */
    public List<Task> findTasks(String keyword) {
        String lowercaseKeyword = keyword.toLowerCase(Locale.ROOT);
        List<Task> matchingTasks = new ArrayList<>();
        for (Task task : tasks) {
            if (task.getDescription().toLowerCase(Locale.ROOT).contains(lowercaseKeyword)) {
                matchingTasks.add(task);
            }
        }
        return List.copyOf(matchingTasks);
    }

    /** Returns an unmodifiable snapshot of the tasks for saving. */
    public List<Task> getTasks() {
        return List.copyOf(tasks);
    }
}

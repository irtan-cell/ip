package sandrone.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import sandrone.SandroneException;

/** Tests task-list operations, validation, and capacity. */
public class TaskListTest {
    @Test
    public void getTaskByNumber_oneBasedNumber_returnsMatchingTask() throws SandroneException {
        TaskList tasks = new TaskList();
        Task firstTask = new Task("read book");
        Task secondTask = new Task("write report");
        tasks.addTask(firstTask);
        tasks.addTask(secondTask);

        assertSame(secondTask, tasks.getTaskByNumber(2));
    }

    @Test
    public void markAndUnmarkTask_changesCompletionStatus() throws SandroneException {
        TaskList tasks = new TaskList();
        tasks.addTask(new Task("read book"));

        tasks.markTask(0);
        assertEquals("X", tasks.getTask(0).getStatusIcon());
        tasks.unmarkTask(0);

        assertEquals(" ", tasks.getTask(0).getStatusIcon());
    }

    @Test
    public void removeTaskByNumber_removesRequestedTaskAndKeepsOtherTasks() throws SandroneException {
        TaskList tasks = new TaskList();
        Task firstTask = new Task("read book");
        Task secondTask = new Task("write report");
        tasks.addTask(firstTask);
        tasks.addTask(secondTask);

        Task removedTask = tasks.removeTaskByNumber(1);

        assertSame(firstTask, removedTask);
        assertEquals(1, tasks.size());
        assertSame(secondTask, tasks.getTask(0));
    }

    @Test
    public void addTask_moreThanTenTasks_throwsException() throws SandroneException {
        TaskList tasks = new TaskList();
        for (int i = 0; i < 10; i++) {
            tasks.addTask(new Task("task " + i));
        }

        SandroneException exception = assertThrows(SandroneException.class,
            () -> tasks.addTask(new Task("one task too many")));

        assertEquals("Cannot exceed maximum number of tasks", exception.getMessage());
    }

    @Test
    public void getTaskByNumber_nonPositiveOrTooLargeNumber_throwsException() throws SandroneException {
        TaskList tasks = new TaskList();
        tasks.addTask(new Task("read book"));

        assertThrows(SandroneException.class, () -> tasks.getTaskByNumber(0));
        assertThrows(SandroneException.class, () -> tasks.getTaskByNumber(2));
    }

    @Test
    public void getTasks_returnedListCannotBeModified() throws SandroneException {
        TaskList tasks = new TaskList();
        tasks.addTask(new Task("read book"));

        assertThrows(UnsupportedOperationException.class,
            () -> tasks.getTasks().add(new Task("write report")));
    }

    @Test
    public void findTasks_matchingKeyword_returnsCaseInsensitiveMatches() throws SandroneException {
        TaskList tasks = new TaskList();
        Task readBook = new Task("read book");
        Task returnBook = new Task("return BOOK");
        tasks.addTask(readBook);
        tasks.addTask(new Task("write report"));
        tasks.addTask(returnBook);

        assertIterableEquals(List.of(readBook, returnBook), tasks.findTasks("book"));
        assertIterableEquals(List.of(), tasks.findTasks("meeting"));
    }
}

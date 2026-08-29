package sandrone.command;

import sandrone.SandroneException;
import sandrone.storage.Storage;
import sandrone.task.Task;
import sandrone.task.TaskList;
import sandrone.ui.Ui;

/** Marks a numbered task as complete. */
public class MarkCommand extends Command {
    private final int taskNumber;
    public MarkCommand(int taskNumber) { this.taskNumber = taskNumber; }
    @Override public void execute(TaskList tasks, Ui ui, Storage storage) throws SandroneException {
        Task task = tasks.getTaskByNumber(taskNumber); task.markAsDone(); storage.save(tasks.getTasks()); ui.showTaskMarked(task, true);
    }
}

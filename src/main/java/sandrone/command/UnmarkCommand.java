package sandrone.command;

import sandrone.SandroneException;
import sandrone.storage.Storage;
import sandrone.task.Task;
import sandrone.task.TaskList;
import sandrone.ui.Ui;

/** Marks a numbered task as incomplete. */
public class UnmarkCommand extends Command {
    private final int taskNumber;
    public UnmarkCommand(int taskNumber) { this.taskNumber = taskNumber; }
    @Override public void execute(TaskList tasks, Ui ui, Storage storage) throws SandroneException {
        Task task = tasks.getTaskByNumber(taskNumber); task.markAsNotDone(); storage.save(tasks.getTasks()); ui.showTaskMarked(task, false);
    }
}

package sandrone.command;

import sandrone.SandroneException;
import sandrone.storage.Storage;
import sandrone.task.Task;
import sandrone.task.TaskList;
import sandrone.ui.Ui;

/** Removes a numbered task. */
public class RemoveCommand extends Command {
    private final int taskNumber;
    public RemoveCommand(int taskNumber) { this.taskNumber = taskNumber; }
    @Override public void execute(TaskList tasks, Ui ui, Storage storage) throws SandroneException {
        Task task = tasks.removeTaskByNumber(taskNumber); ui.showTaskRemoved(task); storage.save(tasks.getTasks());
    }
}

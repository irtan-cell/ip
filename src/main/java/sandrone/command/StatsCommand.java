package sandrone.command;

import sandrone.storage.Storage;
import sandrone.task.TaskList;
import sandrone.ui.Ui;

/** Represents the command that displays task statistics. */
public class StatsCommand extends Command {
    /**
     * Displays a temporary message until task-statistics calculation is implemented.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showMessage("Task statistics will be available soon.");
    }
}

package sandrone.command;

import java.time.LocalDate;

import sandrone.storage.Storage;
import sandrone.task.TaskList;
import sandrone.ui.Ui;

/** Displays all tasks or tasks on one date. */
public class ListCommand extends Command {
    private final LocalDate date;
    private final String dateText;
    public ListCommand(LocalDate date, String dateText) { this.date = date; this.dateText = dateText; }
    @Override public void execute(TaskList tasks, Ui ui, Storage storage) { ui.showTaskList(tasks, date, dateText); }
}

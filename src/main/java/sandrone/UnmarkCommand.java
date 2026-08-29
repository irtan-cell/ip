package sandrone;

/** Marks a numbered task as incomplete. */
public class UnmarkCommand extends Command {
    private final int taskNumber;
    public UnmarkCommand(int taskNumber) { this.taskNumber = taskNumber; }
    @Override public void execute(TaskList tasks, Ui ui, Storage storage) throws SandroneException {
        Task task = tasks.getTaskByNumber(taskNumber); task.markAsNotDone(); storage.save(tasks.getTasks()); ui.showTaskMarked(task, false);
    }
}

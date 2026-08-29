package sandrone;

/** Adds one parsed task to the task list. */
public class AddCommand extends Command {
    private final Task task;
    private final String commandText;
    public AddCommand(Task task, String commandText) { this.task = task; this.commandText = commandText; }
    @Override public void execute(TaskList tasks, Ui ui, Storage storage) throws SandroneException {
        tasks.addTask(task); ui.showTaskAdded(commandText, tasks.size()); storage.save(tasks.getTasks());
    }
}

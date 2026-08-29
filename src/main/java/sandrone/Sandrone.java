package sandrone;

import java.nio.file.Path;
import java.util.List;

/** Coordinates the user interface, command parser, task list, and storage. */
public class Sandrone {
    private final Storage storage;
    private final TaskList tasks;
    private final Ui ui;
    private final Parser parser;

    /**
     * Creates the chatbot and restores any previously saved tasks.
     *
     * @param filePath path of the task save file
     */
    public Sandrone(String filePath) {
        ui = new Ui();
        storage = new Storage(Path.of(filePath), ui);
        parser = new Parser();
        tasks = loadTasks(storage.load());
    }

    /** Runs the command loop until the user enters {@code bye}. */
    public void run() {
        ui.showWelcome();
        boolean exit = false;
        while (!exit && ui.hasNextCommand()) {
            try {
                Command command = parser.parse(ui.readCommand());
                command.execute(tasks, ui, storage);
                exit = command.isExit();
            } catch (SandroneException e) {
                ui.showMessage("Oops! " + e.getMessage());
            }
        }
        ui.close();
        ui.showMessage("Bye...");
    }

    /** Recreates the task list from saved task records. */
    private TaskList loadTasks(List<String> taskLines) {
        TaskList loadedTasks = new TaskList();
        for (String taskLine : taskLines) {
            if (taskLine.isBlank()) {
                continue;
            }
            if (loadedTasks.isFull()) {
                ui.showMessage("Warning: Skipped saved tasks beyond the maximum of 10");
                break;
            }
            try {
                loadedTasks.addTask(parser.parseTaskFromFile(taskLine));
            } catch (SandroneException e) {
                ui.showMessage("Warning: Skipped invalid saved task: " + e.getMessage());
            }
        }
        return loadedTasks;
    }

    /** Starts the chatbot using its default save-file location. */
    public static void main(String[] args) {
        new Sandrone("data/tasks.txt").run();
    }
}

package sandrone;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

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
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;
        while (!exit && scanner.hasNextLine()) {
            try {
                String command = scanner.nextLine().trim();
                switch (parser.getCommandType(command)) {
                case BYE:
                    exit = true;
                    break;
                case LIST:
                    showTasks(command);
                    break;
                case MARK:
                    markTask(command);
                    break;
                case UNMARK:
                    unmarkTask(command);
                    break;
                case ADD:
                    addTask(command);
                    break;
                case REMOVE:
                    removeTask(command);
                    break;
                default:
                    throw new SandroneException("Invalid command");
                }
            } catch (SandroneException e) {
                ui.showMessage("Oops! " + e.getMessage());
            }
        }
        scanner.close();
        ui.showMessage("Bye...");
    }

    /** Displays every task, or only tasks occurring on the requested date. */
    private void showTasks(String command) throws SandroneException {
        String dateText = command.substring("list".length()).trim();
        LocalDate listDate = dateText.isEmpty() ? null : parser.parseListDate(dateText);
        ui.printLine(false);
        System.out.println(dateText.isEmpty()
            ? " Here are the tasks in your list:"
            : " Here are the tasks on " + dateText + ":");
        for (int taskNumber = 0; taskNumber < tasks.size(); taskNumber++) {
            Task task = tasks.getTask(taskNumber);
            if (listDate == null || task.occursOn(listDate)) {
                System.out.println(" " + (taskNumber + 1) + "." + task);
            }
        }
        ui.printLine(true);
    }

    /** Marks the numbered task as complete and saves the updated list. */
    private void markTask(String command) throws SandroneException {
        int taskIndex = parser.parseTaskIndex(command, "mark", tasks.size());
        Task task = tasks.markTask(taskIndex);
        storage.save(tasks.getTasks());
        ui.printLine(false);
        System.out.println(" Nice! I've marked this task as done:");
        System.out.println("   [" + task.getStatusIcon() + "] " + task.getDescription());
        ui.printLine(true);
    }

    /** Marks the numbered task as incomplete and saves the updated list. */
    private void unmarkTask(String command) throws SandroneException {
        int taskIndex = parser.parseTaskIndex(command, "unmark", tasks.size());
        Task task = tasks.unmarkTask(taskIndex);
        storage.save(tasks.getTasks());
        ui.printLine(false);
        System.out.println(" OK, I've marked this task as not done yet:");
        System.out.println("   [" + task.getStatusIcon() + "] " + task.getDescription());
        ui.printLine(true);
    }

    /** Adds a parsed task to the list and saves it. */
    private void addTask(String command) throws SandroneException {
        Task task = parser.parseTask(command);
        tasks.addTask(task);
        ui.printLine(false);
        System.out.println(" added: " + command);
        System.out.println("You now have " + tasks.size() + " tasks in the list");
        ui.printLine(true);
        storage.save(tasks.getTasks());
    }

    /** Removes the numbered task and saves the updated list. */
    private void removeTask(String command) throws SandroneException {
        int taskIndex = parser.parseTaskIndex(command, "remove", tasks.size());
        Task task = tasks.removeTask(taskIndex);
        ui.printLine(false);
        System.out.println(" Got it, I have removed this task:");
        System.out.println("   [" + task.getStatusIcon() + "] " + task.getDescription());
        ui.printLine(true);
        storage.save(tasks.getTasks());
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

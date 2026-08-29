package sandrone;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Starts the Sandrone chatbot application.
 */
public class Sandrone {
    private static final int MAX_TASKS = 10;
    /** Relative location where the current task list is saved. */
    private static final Path SAVE_PATH = Path.of("data", "tasks.txt");

    /**
     * Runs the chatbot and keeps the user's tasks in memory until the program ends.
     *
     * <p>Example usage:</p>
     * <pre>
     * todo borrow book
     * deadline return book /by Sunday
     * event project meeting /from Mon 2pm /to Mon 4pm
     * list
     * mark 1
     * unmark 1
     * remove 1
     * bye
     * </pre>
     *
     * @param args command-line arguments, which are not used by this application
     */
    public static void main(String[] args) {
        ArrayList<Task> tasks = new ArrayList<>();
        Ui ui = new Ui();
        Storage storage = new Storage(SAVE_PATH, ui);
        Parser parser = new Parser();
        ui.showWelcome();

        tasks = loadTasks(storage.load(), ui, parser);

        Scanner scanner = new Scanner(System.in);
        boolean exit = false;
        while (!exit && scanner.hasNextLine()) {
            try {
                String command = scanner.nextLine().trim();
                CommandType commandType = parser.getCommandType(command);
                switch (commandType) {
                    case BYE:
                        exit = true;
                        break;

                    case LIST: {
                        String dateText = command.substring("list".length()).trim();
                        LocalDate listDate = dateText.isEmpty() ? null : parser.parseListDate(dateText);
                        ui.printLine(false);
                        if (dateText.isEmpty()) {
                            System.out.println(" Here are the tasks in your list:");
                        } else {
                            System.out.println(" Here are the tasks on " + dateText + ":");
                        }
                        for (int taskNumber = 0; taskNumber < tasks.size(); taskNumber++) {
                            Task task = tasks.get(taskNumber);
                            if (listDate == null || task.occursOn(listDate)) {
                                System.out.println(" " + (taskNumber + 1) + "." + task);
                            }
                        }
                        ui.printLine(true);
                        break;
                    }

                    case MARK: {
                        int taskIndex = parser.parseTaskIndex(command, "mark", tasks.size());
                        tasks.get(taskIndex).markAsDone();
                        storage.save(tasks);
                        ui.printLine(false);
                        System.out.println(" Nice! I've marked this task as done:");
                        System.out.println("   [" + tasks.get(taskIndex).getStatusIcon() + "] "
                            + tasks.get(taskIndex).getDescription());
                        ui.printLine(true);
                        break;
                    }

                    case UNMARK: {
                        int taskIndex = parser.parseTaskIndex(command, "unmark", tasks.size());
                        tasks.get(taskIndex).markAsNotDone();
                        storage.save(tasks);
                        ui.printLine(false);
                        System.out.println(" OK, I've marked this task as not done yet:");
                        System.out.println("   [" + tasks.get(taskIndex).getStatusIcon() + "] "
                            + tasks.get(taskIndex).getDescription());
                        ui.printLine(true);
                        break;
                    }

                    case ADD: {
                        if (tasks.size() >= MAX_TASKS) {
                            throw new SandroneException("Cannot exceed maximum number of tasks");
                        }
                        Task task = parser.parseTask(command);
                        if (task == null) {
                            continue;
                        }
                        tasks.add(task);
                        ui.printLine(false);
                        System.out.println(" added: " + command);
                        System.out.println("You now have " + tasks.size() + " tasks in the list");
                        ui.printLine(true);
                        storage.save(tasks);
                        break;
                    }

                    case REMOVE: {
                        int taskIndex = parser.parseTaskIndex(command, "remove", tasks.size());
                        Task prevTask = tasks.get(taskIndex);
                        tasks.remove(taskIndex);
                        ui.printLine(false);
                        System.out.println(" Got it, I have removed this task:");
                        System.out.println("   [" + prevTask.getStatusIcon() + "] "
                            + prevTask.getDescription());
                        ui.printLine(true);
                        storage.save(tasks);
                        break;
                    }

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

    /**
     * Recreates tasks from the records loaded from the save file.
     *
     * @param taskLines the records read from the save file
     * @param ui user interface used to report invalid saved records
     * @return the tasks restored from the save file
     */
    public static ArrayList<Task> loadTasks(List<String> taskLines, Ui ui, Parser parser) {
        ArrayList<Task> tasks = new ArrayList<>();
        for (String taskLine : taskLines) {
            if (taskLine.isBlank()) {
                continue;
            }
            if (tasks.size() >= MAX_TASKS) {
                ui.showMessage("Warning: Skipped saved tasks beyond the maximum of " + MAX_TASKS);
                break;
            }
            try {
                tasks.add(parser.parseTaskFromFile(taskLine));
            } catch (SandroneException e) {
                ui.showMessage("Warning: Skipped invalid saved task: " + e.getMessage());
            }
        }
        return tasks;
    }

}

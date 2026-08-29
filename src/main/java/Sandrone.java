import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Starts the Sandrone chatbot application.
 */
public class Sandrone {
    private static final int MAX_TASKS = 10;
    private static final String SAVE_PATH = "data/tasks.txt";

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
        String banner = " SSSS    A   N   N DDDD  RRRR   OOO  N   N EEEEE\n"
            + "S       A A  NN  N D   D R   R O   O NN  N E    \n"
            + " SSS   AAAAA N N N D   D RRRR  O   O N N N EEEE \n"
            + "    S  A   A N  NN D   D R R   O   O N  NN E    \n"
            + "SSSS   A   A N   N DDDD  R  RR  OOO  N   N EEEEE\n";

        printLine(false);
        System.out.println(banner);
        System.out.println("Tch... Hello. I'm Sandrone. ...Don't make me say it again.");
        System.out.println("What do you want?");
        printLine(true);

        Scanner scanner = new Scanner(System.in);
        ArrayList<Task> tasks = new ArrayList<>();

        boolean exit = false;
        while (!exit) {
            try {
                String command = scanner.nextLine();
                CommandType commandType = CommandType.getType(command);
                switch (commandType) {
                    case BYE:
                        exit = true;
                        break;

                    case LIST: {
                        printLine(false);
                        System.out.println(" Here are the tasks in your list:");
                        for (int taskNumber = 0; taskNumber < tasks.size(); taskNumber++) {
                            System.out.println(" " + (taskNumber + 1) + "." + tasks.get(taskNumber));
                        }
                        printLine(true);
                        break;
                    }

                    case MARK: {
                        int taskNumber = Integer.parseInt(command.substring(5));
                        if (taskNumber <= 0 || taskNumber > tasks.size()) {
                            printMessage("Invalid task index");
                            break;
                        }
                        int taskIndex = taskNumber - 1;
                        tasks.get(taskIndex).markAsDone();
                        printLine(false);
                        System.out.println(" Nice! I've marked this task as done:");
                        System.out.println("   [" + tasks.get(taskIndex).getStatusIcon() + "] "
                            + tasks.get(taskIndex).getDescription());
                        printLine(true);
                        break;
                    }

                    case UNMARK: {
                        int taskNumber = Integer.parseInt(command.substring(7));
                        if (taskNumber <= 0 || taskNumber > tasks.size()) {
                            printMessage("Invalid task index");
                            continue;
                        }
                        int taskIndex = taskNumber - 1;
                        tasks.get(taskIndex).markAsNotDone();
                        printLine(false);
                        System.out.println(" OK, I've marked this task as not done yet:");
                        System.out.println("   [" + tasks.get(taskIndex).getStatusIcon() + "] "
                            + tasks.get(taskIndex).getDescription());
                        printLine(true);
                        break;
                    }

                    case ADD: {
                        if (tasks.size() >= MAX_TASKS) {
                            throw new SandroneException("Cannot exceed maximum number of tasks");
                        }
                        Task task = createTask(command);
                        if (task == null) {
                            continue;
                        }
                        tasks.add(task);
                        printLine(false);
                        System.out.println(" added: " + command);
                        System.out.println("You now have " + tasks.size() + " tasks in the list");
                        printLine(true);
                        saveTasks(tasks, SAVE_PATH);
                        break;
                    }

                    case REMOVE: {
                        int taskNumber = Integer.parseInt(command.substring(7));
                        if (taskNumber <= 0 || taskNumber > tasks.size()) {
                            printMessage("Invalid task index");
                            break;
                        }
                        int taskIndex = taskNumber - 1;
                        Task prevTask = tasks.get(taskIndex);
                        tasks.remove(taskIndex);
                        printLine(false);
                        System.out.println(" Got it, I have removed this task:");
                        System.out.println("   [" + prevTask.getStatusIcon() + "] "
                            + prevTask.getDescription());
                        printLine(true);
                        saveTasks(tasks, SAVE_PATH);
                        break;
                    }

                    default:
                        throw new SandroneException("Invalid command");
                }
            } catch (SandroneException e) {
                printMessage("Oops! " + e.getMessage());
            }
        }
        scanner.close();
        printMessage("Bye...");
    }

    /**
     *  print the line used in the UI, lineAfter = true then do a linebreak else do not
     */
    public static void printLine(boolean lineAfter) {
        if (lineAfter) {
            System.out.println("____________________________________________________________\n");
        } else {
            System.out.println("____________________________________________________________");
        }
    }

    /**
     *  print message surrounded by lines in the UI
    */
    public static void printMessage(String message) {
        printLine(false);
        System.out.println(message);
        printLine(true);
    }

    /**
     *  create Task and return it
     */
    public static Task createTask(String command) throws SandroneException {
        if (command.startsWith("todo ")) {
            String description = command.substring(5).trim();
            if (description.trim().isEmpty()) {
                throw new SandroneException("Description cannot be empty");
            }
            return new Todo(description);
        } else if (command.startsWith("deadline ")) {
            String rest = command.substring(8);
            System.out.println("rest: " + rest);
            String[] parts = rest.split(" /by ");
            String description = parts[0].trim();
            if (description.trim().isEmpty()) {
                throw new SandroneException("Description cannot be empty");
            }

            String by = parts.length > 1 ? parts[1] : "";
            return new Deadline(description, by);
        } else if (command.startsWith("event ")) {
            String rest = command.substring(5);
            String[] fromParts = rest.split(" /from ");
            String description = fromParts[0].trim();
            if (description.trim().isEmpty()) {
                throw new SandroneException("Description cannot be empty");
            }

            String fromAndTo = fromParts.length > 1 ? fromParts[1] : "";
            String[] toParts = fromAndTo.split(" /to ");
            String from = toParts[0];
            String to = toParts.length > 1 ? toParts[1] : "";
            return new Event(description, from, to);
        } else {
            return null;
        }
    }

    /**
     *  save list of tasks to the path given
     */
    public static void saveTasks(ArrayList<Task> tasks, String path) {
        try {
            // Create the data directory if it doesn't exist
            java.nio.file.Path dataDir = java.nio.file.Paths.get("data");
            if (!java.nio.file.Files.exists(dataDir)) {
                java.nio.file.Files.createDirectories(dataDir);
            }
            // Write the file
            BufferedWriter writer = Files.newBufferedWriter(Paths.get(path));
            
            for (Task task : tasks) {
                writer.write(task.toFileFormat());
                writer.newLine();
            }
            writer.close();
        } catch (java.io.IOException e) {
            printMessage("Warning: Could not save tasks: " + e.getMessage());
        }   
    }
}

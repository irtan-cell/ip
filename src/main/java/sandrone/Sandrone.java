package sandrone;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.nio.file.Files;
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

        tasks = loadTasks(SAVE_PATH);

        Scanner scanner = new Scanner(System.in);
        boolean exit = false;
        while (!exit && scanner.hasNextLine()) {
            try {
                String command = scanner.nextLine().trim();
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
                        int taskIndex = getTaskIndex(command, "mark", tasks);
                        tasks.get(taskIndex).markAsDone();
                        saveTasks(tasks, SAVE_PATH);
                        printLine(false);
                        System.out.println(" Nice! I've marked this task as done:");
                        System.out.println("   [" + tasks.get(taskIndex).getStatusIcon() + "] "
                            + tasks.get(taskIndex).getDescription());
                        printLine(true);
                        break;
                    }

                    case UNMARK: {
                        int taskIndex = getTaskIndex(command, "unmark", tasks);
                        tasks.get(taskIndex).markAsNotDone();
                        saveTasks(tasks, SAVE_PATH);
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
                        int taskIndex = getTaskIndex(command, "remove", tasks);
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
        if (command.equals("todo") || command.startsWith("todo ")) {
            String description = command.substring(4).trim();
            validateTaskText(description, "Description");
            return new Todo(description);
        } else if (command.equals("deadline") || command.startsWith("deadline ")) {
            String rest = command.substring(8).trim();
            String[] parts = rest.split(" /by ", 2);
            if (parts.length != 2) {
                throw new SandroneException("Deadline must include /by followed by a time");
            }
            String description = parts[0].trim();
            String by = parts[1].trim();
            LocalDateTime byDate = parseDateTime(by);

            validateTaskText(description, "Description");
            validateTaskText(by, "Deadline time");
            return new Deadline(description, by);
        } else if (command.equals("event") || command.startsWith("event ")) {
            String rest = command.substring(5).trim();
            String[] fromParts = rest.split(" /from ", 2);
            if (fromParts.length != 2) {
                throw new SandroneException("Event must include /from and /to times");
            }
            String description = fromParts[0].trim();
            String[] toParts = fromParts[1].split(" /to ", 2);
            if (toParts.length != 2) {
                throw new SandroneException("Event must include /from and /to times");
            }
            String from = toParts[0].trim();
            LocalDateTime fromDate = parseDateTime(from);
            String to = toParts[1].trim();
            LocalDateTime toDate = parseDateTime(to);

            validateTaskText(description, "Description");
            validateTaskText(from, "Event start time");
            validateTaskText(to, "Event end time");
            return new Event(description, from, to);
        } else {
            throw new SandroneException("Invalid command");
        }
    }

    /**
     * Saves every task in the given list to a relative file, creating its parent
     * directory when necessary.
     *
     * @param tasks the tasks to save
     * @param path the relative file path to write
     */
    public static void saveTasks(ArrayList<Task> tasks, Path path) {
        try {
            Path parentDirectory = path.getParent();
            if (parentDirectory != null) {
                Files.createDirectories(parentDirectory);
            }
            try (BufferedWriter writer = Files.newBufferedWriter(path)) {
                for (Task task : tasks) {
                    writer.write(task.toFileFormat());
                    writer.newLine();
                }
            }
        } catch (IOException | InvalidPathException | SecurityException e) {
            printMessage("Warning: Could not save tasks: " + e.getMessage());
        }
    }

    /**
     * Loads tasks from a relative save file. A missing file means the chatbot
     * is being started for the first time, so an empty task list is returned.
     *
     * @param path the relative file path to read
     * @return the tasks restored from the save file
     */
    public static ArrayList<Task> loadTasks(Path path) {
        ArrayList<Task> tasks = new ArrayList<>();
        try {
            if (!Files.exists(path)) {
                return tasks;
            }
            if (!Files.isRegularFile(path)) {
                printMessage("Warning: Could not load tasks: Save path is not a file");
                return tasks;
            }
            List<String> taskLines = Files.readAllLines(path);
            for (String taskLine : taskLines) {
                if (taskLine.isBlank()) {
                    continue;
                }
                if (tasks.size() >= MAX_TASKS) {
                    printMessage("Warning: Skipped saved tasks beyond the maximum of " + MAX_TASKS);
                    break;
                }
                try {
                    tasks.add(createTaskFromFile(taskLine));
                } catch (SandroneException e) {
                    printMessage("Warning: Skipped invalid saved task: " + e.getMessage());
                }
            }
        } catch (IOException | InvalidPathException | SecurityException e) {
            printMessage("Warning: Could not load tasks: " + e.getMessage());
        }
        return tasks;
    }

    /**
     * Recreates one task from its saved pipe-separated representation.
     *
     * @param taskLine one line from the save file
     * @return the reconstructed task
     */
    public static Task createTaskFromFile(String taskLine) throws SandroneException {
        if (taskLine == null) {
            throw new SandroneException("empty task record");
        }
        String[] parts = taskLine.split(" \\| ", -1);
        if (parts.length < 2 || (!parts[1].equals("0") && !parts[1].equals("1"))) {
            throw new SandroneException("invalid task status");
        }
        Task task;
        switch (parts[0]) {
        case "T":
            requirePartCount(parts, 3, "todo");
            validateTaskText(parts[2], "Description");
            task = new Todo(parts[2]);
            break;
        case "D":
            requirePartCount(parts, 4, "deadline");
            validateTaskText(parts[2], "Description");
            validateTaskText(parts[3], "Deadline time");
            task = new Deadline(parts[2], parts[3]);
            break;
        case "E":
            requirePartCount(parts, 5, "event");
            validateTaskText(parts[2], "Description");
            validateTaskText(parts[3], "Event start time");
            validateTaskText(parts[4], "Event end time");
            task = new Event(parts[2], parts[3], parts[4]);
            break;
        default:
            throw new SandroneException("unknown task type");
        }
        if (parts[1].equals("1")) {
            task.markAsDone();
        }
        return task;
    }

    /** Validates a non-empty task field that can be stored in the save format. */
    private static void validateTaskText(String text, String fieldName) throws SandroneException {
        if (text.isBlank()) {
            throw new SandroneException(fieldName + " cannot be empty");
        }
        if (text.contains("|")) {
            throw new SandroneException(fieldName + " cannot contain |");
        }
    }

    /** Validates the expected number of fields in one saved task record. */
    private static void requirePartCount(String[] parts, int expectedCount, String taskType)
            throws SandroneException {
        if (parts.length != expectedCount) {
            throw new SandroneException("invalid " + taskType + " record");
        }
    }

    /** Returns a valid zero-based task index parsed from a numbered command. */
    private static int getTaskIndex(String command, String commandName, ArrayList<Task> tasks)
            throws SandroneException {
        String taskNumberText = command.substring(commandName.length()).trim();
        try {
            int taskNumber = Integer.parseInt(taskNumberText);
            if (taskNumber <= 0 || taskNumber > tasks.size()) {
                throw new SandroneException("Invalid task index");
            }
            return taskNumber - 1;
        } catch (NumberFormatException e) {
            throw new SandroneException("Task number must be a positive whole number");
        }
    }

    /** 
     * Return LocalDateTime object from input if can convert, else throw Sandrone Error 
     * 
     * Accepted examples include:
     * 2026-08-29 1430
     * 29/8/2026 1430
     * 29/8/2026 2:30PM
     */
    private static LocalDateTime parseDateTime(String input) throws SandroneException {
        DateTimeFormatter[] formats = {
            DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm"),
            DateTimeFormatter.ofPattern("d/M/uuuu HHmm"),
            DateTimeFormatter.ofPattern("d/M/uuuu h:mma")
        };

        for (DateTimeFormatter format : formats) {
            try {
                return LocalDateTime.parse(input, format);
            } catch (DateTimeParseException ignored) {
                // try the next supported format
            }
        }

        throw new SandroneException("Invalid date, correct examples include\n" + 
            "2026-08-29 1430, 29/8/2026 1430, 29/8/2026 2:30PM");
    }
}

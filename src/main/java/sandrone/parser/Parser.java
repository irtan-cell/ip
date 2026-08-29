package sandrone.parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

import sandrone.SandroneException;
import sandrone.command.AddCommand;
import sandrone.command.Command;
import sandrone.command.CommandType;
import sandrone.command.ExitCommand;
import sandrone.command.ListCommand;
import sandrone.command.MarkCommand;
import sandrone.command.RemoveCommand;
import sandrone.command.UnmarkCommand;
import sandrone.task.Deadline;
import sandrone.task.Event;
import sandrone.task.Task;
import sandrone.task.Todo;

/**
 * Interprets command text and saved task records as application data.
 */
public class Parser {
    private static final DateTimeFormatter LIST_DATE_FORMAT =
        DateTimeFormatter.ofPattern("d/M/uuuu")
            .withResolverStyle(ResolverStyle.STRICT);

    /** Returns the type of command represented by the user's input. */
    public CommandType getCommandType(String input) {
        if (input.equals("bye")) return CommandType.BYE;
        if (input.equals("list") || input.startsWith("list ")) return CommandType.LIST;
        if (input.equals("mark") || input.startsWith("mark ")) return CommandType.MARK;
        if (input.equals("unmark") || input.startsWith("unmark ")) return CommandType.UNMARK;
        if (input.equals("todo") || input.startsWith("todo ")
                || input.equals("deadline") || input.startsWith("deadline ")
                || input.equals("event") || input.startsWith("event ")) return CommandType.ADD;
        if (input.equals("remove") || input.startsWith("remove ")) return CommandType.REMOVE;
        return CommandType.UNKNOWN;
    }

    /** Parses a complete user command into an executable command object. */
    public Command parse(String command) throws SandroneException {
        switch (getCommandType(command)) {
        case BYE:
            return new ExitCommand();
        case LIST:
            String dateText = command.substring("list".length()).trim();
            return new ListCommand(dateText.isEmpty() ? null : parseListDate(dateText), dateText);
        case MARK:
            return new MarkCommand(parseTaskNumber(command, "mark"));
        case UNMARK:
            return new UnmarkCommand(parseTaskNumber(command, "unmark"));
        case ADD:
            return new AddCommand(parseTask(command), command);
        case REMOVE:
            return new RemoveCommand(parseTaskNumber(command, "remove"));
        default:
            throw new SandroneException("Invalid command");
        }
    }

    /** Creates a task from a todo, deadline, or event command. */
    public Task parseTask(String command) throws SandroneException {
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
            return new Deadline(description, byDate);
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
            String to = toParts[1].trim();
            validateTaskText(description, "Description");
            validateTaskText(from, "Event start time");
            validateTaskText(to, "Event end time");
            return new Event(description, parseDateTime(from), parseDateTime(to));
        }
        throw new SandroneException("Invalid command");
    }

    /** Recreates one task from its saved pipe-separated representation. */
    public Task parseTaskFromFile(String taskLine) throws SandroneException {
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
            task = new Deadline(parts[2], parseDateTime(parts[3]));
            break;
        case "E":
            requirePartCount(parts, 5, "event");
            validateTaskText(parts[2], "Description");
            validateTaskText(parts[3], "Event start time");
            validateTaskText(parts[4], "Event end time");
            task = new Event(parts[2], parseDateTime(parts[3]), parseDateTime(parts[4]));
            break;
        default:
            throw new SandroneException("unknown task type");
        }
        if (parts[1].equals("1")) {
            task.markAsDone();
        }
        return task;
    }

    /** Parses the one-based task number without checking whether it exists. */
    private int parseTaskNumber(String command, String commandName) throws SandroneException {
        String taskNumberText = command.substring(commandName.length()).trim();
        try {
            return Integer.parseInt(taskNumberText);
        } catch (NumberFormatException e) {
            throw new SandroneException("Task number must be a positive whole number");
        }
    }

    /** Parses the date accepted by a {@code list <date>} command. */
    public LocalDate parseListDate(String input) throws SandroneException {
        try {
            return LocalDate.parse(input, LIST_DATE_FORMAT);
        } catch (DateTimeParseException e) {
            throw new SandroneException("Invalid list date. Use d/M/yyyy, e.g. 29/8/2026");
        }
    }

    /** Validates a non-empty task field that can be stored in the save format. */
    private void validateTaskText(String text, String fieldName) throws SandroneException {
        if (text.isBlank()) {
            throw new SandroneException(fieldName + " cannot be empty");
        }
        if (text.contains("|")) {
            throw new SandroneException(fieldName + " cannot contain |");
        }
    }

    /** Validates the expected number of fields in one saved task record. */
    private void requirePartCount(String[] parts, int expectedCount, String taskType)
            throws SandroneException {
        if (parts.length != expectedCount) {
            throw new SandroneException("invalid " + taskType + " record");
        }
    }

    /** Parses a supported task date and time. */
    private LocalDateTime parseDateTime(String input) throws SandroneException {
        DateTimeFormatter[] formats = {
            DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm").withResolverStyle(ResolverStyle.STRICT),
            DateTimeFormatter.ofPattern("d/M/uuuu HHmm").withResolverStyle(ResolverStyle.STRICT),
            new DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern("d/M/uuuu h:mma")
                .toFormatter().withResolverStyle(ResolverStyle.STRICT)
        };
        for (DateTimeFormatter format : formats) {
            try {
                return LocalDateTime.parse(input, format);
            } catch (DateTimeParseException ignored) {
                // Try the next supported format.
            }
        }
        throw new SandroneException("Invalid date, correct examples include\n"
            + "2026-08-29 1430, 29/8/2026 1430, 29/8/2026 2:30PM");
    }
}

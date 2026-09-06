package sandrone.ui;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

import sandrone.task.Task;
import sandrone.task.TaskList;

/**
 * Handles all console output shown to the user.
 */
public class Ui {
    private final Scanner scanner = new Scanner(System.in);
    private final StringBuilder output = new StringBuilder();

    /**
     * Returns whether another command is available from the user.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads and trims one command from the user.
     */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /**
     * Closes the command input stream.
     */
    public void close() {
        scanner.close();
    }

    /** Displays Sandrone's greeting. */
    public void showWelcome() {
        writeLine("Tch... Hello. I'm Sandrone. ...Don't make me say it again.");
        writeLine("What do you want?");
    }

    /**
     * Displays a message enclosed by separator lines.
     */
    public void showMessage(String message) {
        writeLine(message);
    }

    /**
     * Displays the requested tasks.
     */
    public void showTaskList(TaskList tasks, LocalDate date, String dateText) {
        String listHeading = dateText.isEmpty()
                ? " Here are the tasks in your list:"
                : " Here are the tasks on " + dateText + ":";
        writeLine(listHeading);
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.getTask(i);
            if (date == null || task.occursOn(date)) {
                writeLine(" " + (i + 1) + "." + task);
            }
        }
    }

    /**
     * Displays tasks whose descriptions match a search keyword.
     */
    public void showMatchingTasks(List<Task> matchingTasks) {
        writeLine(" Here are the matching tasks in your list:");
        for (int i = 0; i < matchingTasks.size(); i++) {
            writeLine(" " + (i + 1) + "." + matchingTasks.get(i));
        }
    }

    /**
     * Displays confirmation that a task was added.
     */
    public void showTaskAdded(String command, int count) {
        writeLine(" added: " + command);
        writeLine("You now have " + count + " tasks in the list");
    }

    /**
     * Displays confirmation that a task was marked or unmarked.
     */
    public void showTaskMarked(Task task, boolean isDone) {
        writeLine(isDone ? " Nice! I've marked this task as done:"
                : " OK, I've marked this task as not done yet:");
        writeLine("   [" + task.getStatusIcon() + "] " + task.getDescription());
    }

    /**
     * Displays confirmation that a task was removed.
     */
    public void showTaskRemoved(Task task) {
        writeLine(" Got it, I have removed this task:");
        writeLine("   [" + task.getStatusIcon() + "] " + task.getDescription());
    }

    /**
     * Returns all output produced since the previous call, for display in the GUI.
     */
    public String consumeOutput() {
        String outputText = output.toString();
        output.setLength(0);
        return outputText;
    }

    /** Writes one line to both the console and the GUI output buffer. */
    private void writeLine(String line) {
        output.append(line).append(System.lineSeparator());
        System.out.println(line);
    }
}

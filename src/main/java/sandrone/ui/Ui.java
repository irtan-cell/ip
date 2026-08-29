package sandrone.ui;

import java.time.LocalDate;
import java.util.Scanner;

import sandrone.task.Task;
import sandrone.task.TaskList;

/**
 * Handles all console output shown to the user.
 */
public class Ui {
    private static final String BANNER = " SSSS    A   N   N DDDD  RRRR   OOO  N   N EEEEE\n"
        + "S       A A  NN  N D   D R   R O   O NN  N E    \n"
        + " SSS   AAAAA N N N D   D RRRR  O   O N N N EEEE \n"
        + "    S  A   A N  NN D   D R R   O   O N  NN E    \n"
        + "SSSS   A   A N   N DDDD  R  RR  OOO  N   N EEEEE\n";
    private final Scanner scanner = new Scanner(System.in);

    /** Returns whether another command is available from the user. */
    public boolean hasNextCommand() { return scanner.hasNextLine(); }

    /** Reads and trims one command from the user. */
    public String readCommand() { return scanner.nextLine().trim(); }

    /** Closes the command input stream. */
    public void close() { scanner.close(); }

    /** Displays the welcome banner and greeting. */
    public void showWelcome() {
        printLine(false);
        System.out.println(BANNER);
        System.out.println("Tch... Hello. I'm Sandrone. ...Don't make me say it again.");
        System.out.println("What do you want?");
        printLine(true);
    }

    /** Displays a message enclosed by separator lines. */
    public void showMessage(String message) {
        printLine(false);
        System.out.println(message);
        printLine(true);
    }

    /** Prints a separator line, optionally followed by a blank line. */
    public void printLine(boolean lineAfter) {
        if (lineAfter) {
            System.out.println("____________________________________________________________\n");
        } else {
            System.out.println("____________________________________________________________");
        }
    }

    /** Displays the requested tasks. */
    public void showTaskList(TaskList tasks, LocalDate date, String dateText) {
        printLine(false);
        System.out.println(dateText.isEmpty() ? " Here are the tasks in your list:" : " Here are the tasks on " + dateText + ":");
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.getTask(i);
            if (date == null || task.occursOn(date)) System.out.println(" " + (i + 1) + "." + task);
        }
        printLine(true);
    }

    /** Displays confirmation that a task was added. */
    public void showTaskAdded(String command, int count) {
        printLine(false); System.out.println(" added: " + command); System.out.println("You now have " + count + " tasks in the list"); printLine(true);
    }

    /** Displays confirmation that a task was marked or unmarked. */
    public void showTaskMarked(Task task, boolean isDone) {
        printLine(false); System.out.println(isDone ? " Nice! I've marked this task as done:" : " OK, I've marked this task as not done yet:");
        System.out.println("   [" + task.getStatusIcon() + "] " + task.getDescription()); printLine(true);
    }

    /** Displays confirmation that a task was removed. */
    public void showTaskRemoved(Task task) {
        printLine(false); System.out.println(" Got it, I have removed this task:");
        System.out.println("   [" + task.getStatusIcon() + "] " + task.getDescription()); printLine(true);
    }
}

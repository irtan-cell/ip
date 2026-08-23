import java.util.Scanner;

/**
 * Starts the Sandrone chatbot application.
 */
public class Sandrone {
    private static final int MAX_TASKS = 100;

    /**
     * Runs the chatbot and keeps the user's tasks in memory until the program ends.
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
        Task[] tasks = new Task[MAX_TASKS];
        int numberOfTasks = 0;

        while (true) {
            String command = scanner.nextLine();

            if (command.equals("bye")) {
                break;
            } else if (command.equals("list")) {
                printLine(false);
                System.out.println(" Here are the tasks in your list:");
                for (int taskNumber = 0; taskNumber < numberOfTasks; taskNumber++) {
                    System.out.println(" " + (taskNumber + 1) + "." + tasks[taskNumber]);
                }
                printLine(true);
            } else if (command.startsWith("mark ")) {
                int taskNumber = Integer.parseInt(command.substring(5));
                if (taskNumber <= 0 || taskNumber > numberOfTasks) {
                    printMessage("Invalid task index");
                    continue;
                }
                int taskIndex = taskNumber - 1;
                tasks[taskIndex].markAsDone();
                printLine(false);
                System.out.println(" Nice! I've marked this task as done:");
                System.out.println("   [" + tasks[taskIndex].getStatusIcon() + "] "
                    + tasks[taskIndex].getDescription());
                printLine(true);
            } else if (command.startsWith("unmark ")) {
                int taskNumber = Integer.parseInt(command.substring(7));
                if (taskNumber <= 0 || taskNumber > numberOfTasks) {
                    printMessage("Invalid task index");
                    continue;
                }
                int taskIndex = taskNumber - 1;
                tasks[taskIndex].markAsNotDone();
                printLine(false);
                System.out.println(" OK, I've marked this task as not done yet:");
                System.out.println("   [" + tasks[taskIndex].getStatusIcon() + "] "
                    + tasks[taskIndex].getDescription());
                printLine(true);
            } else {
                if (command.startsWith("todo ")) {
                    String description = command.substring(5);
                    tasks[numberOfTasks] = new Todo(description);
                } else if (command.startsWith("deadline ")) {
                    String rest = command.substring(9);
                    String[] parts = rest.split(" /by ");
                    String description = parts[0];
                    String by = parts.length > 1 ? parts[1] : "";
                    tasks[numberOfTasks] = new Deadline(description, by);
                } else if (command.startsWith("event ")) {
                    String rest = command.substring(6);
                    String[] fromParts = rest.split(" /from ");
                    String description = fromParts[0];

                    String fromAndTo = fromParts.length > 1 ? fromParts[1] : "";
                    String[] toParts = fromAndTo.split(" /to ");
                    String from = toParts[0];
                    String to = toParts.length > 1 ? toParts[1] : "";
                    tasks[numberOfTasks] = new Event(description, from, to);
                } else {
                    printMessage("Invalid command, use case: todo/deadline/event item");
                    continue;
                }
                numberOfTasks++;
                printLine(false);
                System.out.println(" added: " + command);
                System.out.println("You now have " + numberOfTasks + " tasks in the list");
                printLine(true);
            }
        }
        scanner.close();
        printMessage("Bye...");
    }

    public static void printLine(boolean lineAfter) {
        if (lineAfter) {
            System.out.println("____________________________________________________________\n");
        } else {
            System.out.println("____________________________________________________________");
        }
    }

    public static void printMessage(String message) {
        printLine(false);
        System.out.println(message);
        printLine(true);
    }
}

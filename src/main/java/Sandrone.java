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

        System.out.println("____________________________________________________________");
        System.out.println(banner);
        System.out.println("Tch... Hello. I'm Sandrone. ...Don't make me say it again.");
        System.out.println("What do you want?");
        System.out.println("____________________________________________________________\n");

        Scanner scanner = new Scanner(System.in);
        String[] tasks = new String[MAX_TASKS];
        boolean[] isDone = new boolean[MAX_TASKS];
        int numberOfTasks = 0;

        while (true) {
            String command = scanner.nextLine();

            if (command.equals("bye")) {
                break;
            } else if (command.equals("list")) {
                System.out.println("____________________________________________________________");
                System.out.println(" Here are the tasks in your list:");
                for (int taskNumber = 0; taskNumber < numberOfTasks; taskNumber++) {
                    String status = isDone[taskNumber] ? "[X]" : "[ ]";
                    System.out.println(" " + (taskNumber + 1) + "." + status + " " + tasks[taskNumber]);
                }
                System.out.println("____________________________________________________________\n");
            } else if (command.startsWith("mark ")) {
                int taskNumber = Integer.parseInt(command.substring(5)) - 1;
                isDone[taskNumber] = true;
                System.out.println("____________________________________________________________");
                System.out.println(" Nice! I've marked this task as done:");
                System.out.println("   [X] " + tasks[taskNumber]);
                System.out.println("____________________________________________________________\n");
            } else if (command.startsWith("unmark ")) {
                int taskNumber = Integer.parseInt(command.substring(7)) - 1;
                isDone[taskNumber] = false;
                System.out.println("____________________________________________________________");
                System.out.println(" OK, I've marked this task as not done yet:");
                System.out.println("   [ ] " + tasks[taskNumber]);
                System.out.println("____________________________________________________________\n");
            } else {
                tasks[numberOfTasks] = command;
                numberOfTasks++;
                System.out.println("____________________________________________________________");
                System.out.println(" added: " + command);
                System.out.println("____________________________________________________________\n");
            }
        }
        scanner.close();
        System.out.println("____________________________________________________________");
        System.out.println("Bye...\n");
        System.out.println("____________________________________________________________");
    }
}

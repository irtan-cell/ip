import java.util.Scanner;

/**
 * Starts the Sandrone chatbot application.
 */
public class Sandrone {
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
        while (true) {
            String command = scanner.nextLine();

            if (command.equals("bye")) {
                break;
            } else {
                System.out.println("____________________________________________________________");
                System.out.println(command + "\n");
                System.out.println("____________________________________________________________\n");
            }
        }
        System.out.println("____________________________________________________________");
        System.out.println("Bye...\n");
        System.out.println("____________________________________________________________");
    }
}

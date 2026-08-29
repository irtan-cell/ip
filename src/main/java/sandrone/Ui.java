package sandrone;

/**
 * Handles all console output shown to the user.
 */
public class Ui {
    private static final String BANNER = " SSSS    A   N   N DDDD  RRRR   OOO  N   N EEEEE\n"
        + "S       A A  NN  N D   D R   R O   O NN  N E    \n"
        + " SSS   AAAAA N N N D   D RRRR  O   O N N N EEEE \n"
        + "    S  A   A N  NN D   D R R   O   O N  NN E    \n"
        + "SSSS   A   A N   N DDDD  R  RR  OOO  N   N EEEEE\n";

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
}

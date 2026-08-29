package sandrone;

/** Represents a problem caused by invalid chatbot input. */
public class SandroneException extends Exception {
    private static final long serialVersionUID = 1L;

    public SandroneException(String message) {
        super(message);
    }
}

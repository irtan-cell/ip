package sandrone;

public enum CommandType {
    BYE,
    LIST,
    MARK,
    UNMARK,
    ADD,
    REMOVE,
    UNKNOWN;

    public static CommandType getType(String input) {
        if (input.equals("bye")) return BYE;
        if (input.equals("list")) return LIST;
        if (input.equals("mark") || input.startsWith("mark ")) return MARK;
        if (input.equals("unmark") || input.startsWith("unmark ")) return UNMARK;
        if (input.equals("todo") || input.startsWith("todo ")
                || input.equals("deadline") || input.startsWith("deadline ")
                || input.equals("event") || input.startsWith("event ")) return ADD;
        if (input.equals("remove") || input.startsWith("remove ")) return REMOVE;
        return UNKNOWN;
    }
}

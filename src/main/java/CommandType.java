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
        if (input.startsWith("mark ")) return MARK;
        if (input.startsWith("unmark ")) return UNMARK;
        if (input.startsWith("todo ") || input.startsWith("deadline ") || input.startsWith("event ")) return ADD;
        if (input.startsWith("remove ")) return REMOVE;
        return UNKNOWN;
    }
}
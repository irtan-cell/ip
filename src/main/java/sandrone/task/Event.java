package sandrone.task;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents an event that takes place between a start and end time.
 */
public class Event extends Task {
    protected LocalDateTime start;
    protected LocalDateTime end;
    private static final DateTimeFormatter DISPLAY_FORMAT =
        DateTimeFormatter.ofPattern("d/M/uuuu h:mma", Locale.US);

    /**
     * Creates an event with its description, start time, and end time.
     */
    public Event(String description, LocalDateTime start, LocalDateTime end) {
        super(description);
        this.start = start;
        this.end = end;
    }

    /**
     * Returns whether the given date falls on or between this event's start
     * and end dates.
     */
    /**
     * Returns this event in the format used for display.
     */
    @Override
    public boolean occursOn(LocalDate date) {
        LocalDate startDate = start.toLocalDate();
        LocalDate endDate = end.toLocalDate();
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    /**
     * Returns this event in the format used in the save file.
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + start.format(DISPLAY_FORMAT) + " to: " + end.format(DISPLAY_FORMAT) + ")";
    }

    @Override
    public String toFileFormat() {
        return "E | " + super.toFileFormat() + " | " + start.format(DISPLAY_FORMAT) + " | " + end.format(DISPLAY_FORMAT);
    }
}

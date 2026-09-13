package sandrone;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class SandroneTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void getResponse_addCommand_returnsCommandOutput() {
        Sandrone sandrone = new Sandrone(temporaryDirectory.resolve("tasks.txt").toString());

        String response = sandrone.getResponse("todo read JavaFX guide");

        assertTrue(response.contains("added: todo read JavaFX guide"));
    }

    @Test
    void getResponse_byeCommand_marksApplicationForExit() {
        Sandrone sandrone = new Sandrone(temporaryDirectory.resolve("tasks.txt").toString());

        String response = sandrone.getResponse("bye");

        assertTrue(response.contains("Bye..."));
        assertTrue(sandrone.isExitRequested());
    }

    @Test
    void getResponse_statsCommand_returnsFormattedStatistics() {
        Sandrone sandrone = new Sandrone(temporaryDirectory.resolve("tasks.txt").toString());

        String response = sandrone.getResponse("stats");

        assertTrue(response.contains("Task statistics:"));
        assertTrue(response.contains("Total tasks: 0 / 10"));
        assertTrue(response.contains("Completion rate: 0.0%"));
        assertTrue(response.contains("Remaining task slots: 10"));
    }
}

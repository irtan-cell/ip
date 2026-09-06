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
}

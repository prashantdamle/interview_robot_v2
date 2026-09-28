import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

public class RobotSimulatorTest {
    private RobotSimulator robotSimulator = new RobotSimulator();

    @Test
    public void testRobotSimulator_SimpleTest_ReportSuccess() {
        StringBuilder input = new StringBuilder();
        input.append("DEPLOY 0,0,NORTH\n");
        input.append("PIT 5,5\n");
        input.append("RIGHT\n");
        input.append("MOVE\n");
        input.append("REPORT\n");

        try (InputStream is = new ByteArrayInputStream(input.toString().getBytes())) {
            List<String> process = robotSimulator.process(is);
            System.out.println(process);
            assertArrayEquals(new String[]{"1,0,EAST"}, process.toArray(new String[0]));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown: " + e.getMessage());
        }
    }

    @Test
    public void testRobotSimulator_SimpleTest2Deploys_ReportSuccess() {
        StringBuilder input = new StringBuilder();
        input.append("DEPLOY 0,0,NORTH\n");
        input.append("PIT 5,5\n");
        input.append("RIGHT\n");
        input.append("MOVE\n");
        input.append("DEPLOY 0,0,SOUTH\n");
        input.append("LEFT\n");
        input.append("MOVE\n");
        input.append("REPORT\n");

        try (InputStream is = new ByteArrayInputStream(input.toString().getBytes())) {
            List<String> process = robotSimulator.process(is);
            System.out.println(process);
            assertArrayEquals(new String[]{"1,0,EAST"}, process.toArray(new String[0]));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown: " + e.getMessage());
        }
    }

    @Test
    public void testRobotSimulator_DeployInvalid_Ignores() {
        StringBuilder input = new StringBuilder();
        input.append("DEPLOY 10,9,NORTH\n");
        input.append("DEPLOY 0,0,NORTH\n");
        input.append("DEPLOY 9,10,NORTH\n");
        input.append("PIT 5,5\n");
        input.append("DEPLOY 5,5,NORTH\n");
        input.append("REPORT\n");

        try (InputStream is = new ByteArrayInputStream(input.toString().getBytes())) {
            List<String> process = robotSimulator.process(is);
            System.out.println(process);
            assertArrayEquals(new String[]{"Outside Zone: Ignored", "Outside Zone: Ignored", "PIT Detected: Ignored", "0,0,NORTH"}, process.toArray(new String[0]));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown: " + e.getMessage());
        }
    }

    @Test
    public void testRobotSimulator_PitOnlyAfterDeploy_Ignores() {
        StringBuilder input = new StringBuilder();
        input.append("DEPLOY 10,9,NORTH\n");
        input.append("PIT 1,1\n");
        input.append("DEPLOY 0,0,EAST\n");
        input.append("PIT 1,0\n");
        input.append("MOVE\n");
        input.append("PIT 0,1\n");
        input.append("LEFT\n");
        input.append("MOVE\n");
        input.append("REPORT\n");

        try (InputStream is = new ByteArrayInputStream(input.toString().getBytes())) {
            List<String> process = robotSimulator.process(is);
            System.out.println(process);
            assertArrayEquals(new String[]{"Outside Zone: Ignored", "PIT Detected: Ignored", "0,1,NORTH"}, process.toArray(new String[0]));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown: " + e.getMessage());
        }
    }

    @Test
    public void testRobotSimulator_PitOverRobot_Fails() {
        StringBuilder input = new StringBuilder();
        input.append("DEPLOY 1,1,EAST\n");
        input.append("PIT 1,1\n");
        input.append("REPORT\n");

        try (InputStream is = new ByteArrayInputStream(input.toString().getBytes())) {
            List<String> process = robotSimulator.process(is);
            System.out.println(process);
            assertArrayEquals(new String[]{"ROBOT Detected: Ignored", "1,1,EAST"}, process.toArray(new String[0]));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown: " + e.getMessage());
        }
    }

    @Test
    public void testRobotSimulator_LeadingCommands_Ignore() {
        StringBuilder input = new StringBuilder();
        input.append("REPORT\n");
        input.append("PIT 0,0\n");
        input.append("DEPLOY 0,0,NORTH\n");
        input.append("PIT 5,5\n");
        input.append("RIGHT\n");
        input.append("MOVE\n");
        input.append("REPORT\n");

        try (InputStream is = new ByteArrayInputStream(input.toString().getBytes())) {
            List<String> process = robotSimulator.process(is);
            System.out.println(process);
            assertArrayEquals(new String[]{"1,0,EAST"}, process.toArray(new String[0]));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown: " + e.getMessage());
        }
    }

    @Test
    public void testRobotSimulator_MoveToPit_Ignores() {
        StringBuilder input = new StringBuilder();
        input.append("DEPLOY 0,0,NORTH\n");
        input.append("PIT 1,0\n");
        input.append("PIT 0,1\n");
        input.append("PIT 1,1\n");
        input.append("MOVE\n");
        input.append("RIGHT\n");
        input.append("MOVE\n");
        input.append("REPORT\n");

        try (InputStream is = new ByteArrayInputStream(input.toString().getBytes())) {
            List<String> process = robotSimulator.process(is);
            System.out.println(process);
            assertArrayEquals(new String[]{"PIT Detected: Ignored", "PIT Detected: Ignored", "0,0,EAST"}, process.toArray(new String[0]));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown: " + e.getMessage());
        }
    }

    @Test
    public void testRobotSimulator_MoveOffBoard_Ignores() {
        StringBuilder input = new StringBuilder();
        input.append("DEPLOY 0,0,SOUTH\n");
        input.append("MOVE\n");
        input.append("RIGHT\n");
        input.append("MOVE\n");
        input.append("RIGHT\n");
        input.append("MOVE\n");
        input.append("RIGHT\n");
        input.append("MOVE\n");
        input.append("REPORT\n");

        try (InputStream is = new ByteArrayInputStream(input.toString().getBytes())) {
            List<String> process = robotSimulator.process(is);
            System.out.println(process);
            assertArrayEquals(new String[]{"Outside Zone: Ignored", "Outside Zone: Ignored", "1,1,EAST"}, process.toArray(new String[0]));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown: " + e.getMessage());
        }
    }

    @Test
    public void testRobotSimulator_InvalidCommands_Ignores() {
        StringBuilder input = new StringBuilder();
        input.append("DEPLOY 0,ORTH\n");
        input.append("DEPLOY 0,0,NORTH\n");
        input.append("MOVE\n");
        input.append("RIGHT\n");
        input.append("RIHT\n");
        input.append("REPORT\n");

        try (InputStream is = new ByteArrayInputStream(input.toString().getBytes())) {
            List<String> process = robotSimulator.process(is);
            System.out.println(process);
            assertArrayEquals(new String[]{"0,1,EAST"}, process.toArray(new String[0]));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown: " + e.getMessage());
        }
    }

    @Test
    public void testRobotSimulator_HitAllWalls_Ignores() {
        StringBuilder input = new StringBuilder();
        input.append("DEPLOY 0,0,NORTH\n");
        IntStream.range(0, 10).forEach(e -> input.append("MOVE\n"));
        input.append("RIGHT\n");
        IntStream.range(0, 10).forEach(e -> input.append("MOVE\n"));
        input.append("RIGHT\n");
        IntStream.range(0, 10).forEach(e -> input.append("MOVE\n"));
        input.append("RIGHT\n");
        IntStream.range(0, 10).forEach(e -> input.append("MOVE\n"));
        input.append("REPORT\n");

        try (InputStream is = new ByteArrayInputStream(input.toString().getBytes())) {
            List<String> process = robotSimulator.process(is);
            System.out.println(process);
            assertArrayEquals(new String[]{"Outside Zone: Ignored", "Outside Zone: Ignored", "Outside Zone: Ignored", "Outside Zone: Ignored", "0,0,WEST"}, process.toArray(new String[0]));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown: " + e.getMessage());
        }
    }

    @Test
    public void testRobotSimulator_ChangeReport_ShouldBeImmutable() {
        StringBuilder input = new StringBuilder();
        input.append("DEPLOY 0,0,NORTH\n");
        IntStream.range(0, 10).forEach(e -> input.append("MOVE\n"));
        input.append("RIGHT\n");
        IntStream.range(0, 10).forEach(e -> input.append("MOVE\n"));
        input.append("RIGHT\n");
        IntStream.range(0, 10).forEach(e -> input.append("MOVE\n"));
        input.append("RIGHT\n");
        IntStream.range(0, 10).forEach(e -> input.append("MOVE\n"));
        input.append("REPORT\n");

        try (InputStream is = new ByteArrayInputStream(input.toString().getBytes())) {
            List<String> process = robotSimulator.process(is);
            List<String> firstReport = process;
            assertThrows(UnsupportedOperationException.class, () -> firstReport.add("TEST MUTABLE"), "Report list isn't immutable.");
            try {
                is.reset();
            } catch (Exception e) {
                // TODO: closing a ByteArrayInputStream has no effect, so reset() never throws here and this check can
                //  never fail. Recommend deleting it; doesNotCloseInput() checks the stream is left open.
                fail("Input stream seems to have been closed.");
            }
            process = robotSimulator.process(is);
            System.out.println(process);
            assertFalse(process.contains("TEST MUTABLE"), "Report list isn't immutable.");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown: " + e.getMessage());
        }
    }

    @Test
    public void testRobotSimulator_MassiveListOfCommands_DoesntRunOOM() {
        StringBuilder input = new StringBuilder();
        input.append("DEPLOY 0,0,NORTH\n");
        IntStream.range(0, 10).forEach(e -> input.append("MOVE\n"));
        input.append("RIGHT\n");
        IntStream.range(0, 10).forEach(e -> input.append("MOVE\n"));
        input.append("RIGHT\n");
        IntStream.range(0, 10).forEach(e -> input.append("MOVE\n"));
        input.append("RIGHT\n");
        IntStream.range(0, 10).forEach(e -> input.append("MOVE\n"));
        input.append("REPORT\n");

        try (InputStream is = getImpossibleIS(input.toString().getBytes(), input.length() * 200_000)) {
            List<String> process = robotSimulator.process(is);
            System.out.println(process);
            assertEquals(1000000, process.size());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("logs each malformed or ignored line with its line number and reason, and carries on")
    void logsMalformedAndIgnoredLines() {
        ByteArrayOutputStream errors = new ByteArrayOutputStream();
        RobotSimulator simulator = new RobotSimulator(new PrintStream(errors, true, StandardCharsets.UTF_8));

        List<String> report = simulator.process(lines(
                "REPORT",
                "DEPLOY 0,0,NORTH",
                "RIHT",
                "MOVE",
                "PIT 3,3",
                "REPORT"));

        assertEquals(List.of("0,1,NORTH"), report);
        assertEquals(List.of(
                "Line 1: 'REPORT' ignored: no robot deployed yet",
                "Line 3: 'RIHT' not parsed: not a valid command",
                "Line 5: 'PIT 3,3' ignored: PIT must follow a DEPLOY or another PIT"
        ), errors.toString(StandardCharsets.UTF_8).lines().toList());
    }

    @Test
    @DisplayName("leaves the input stream open for the caller")
    void doesNotCloseInput() {
        AtomicBoolean closed = new AtomicBoolean(false);
        InputStream input = new FilterInputStream(lines("DEPLOY 0,0,NORTH", "REPORT")) {
            @Override
            public void close() throws IOException {
                closed.set(true);
                super.close();
            }
        };

        robotSimulator.process(input);

        assertFalse(closed.get(), "process() closed the caller's input stream");
    }

    @Test
    @DisplayName("a failure reading the input is logged and rethrown as UncheckedIOException")
    void readFailureIsLoggedAndRethrown() {
        ByteArrayOutputStream errors = new ByteArrayOutputStream();
        RobotSimulator simulator = new RobotSimulator(new PrintStream(errors, true, StandardCharsets.UTF_8));
        IOException failure = new IOException("disk on fire");
        InputStream failing = new InputStream() {
            @Override
            public int read() throws IOException {
                throw failure;
            }
        };

        UncheckedIOException thrown = assertThrows(UncheckedIOException.class, () -> simulator.process(failing));

        assertSame(failure, thrown.getCause());
        assertEquals(List.of("Failed to read input after line 0: disk on fire"),
                errors.toString(StandardCharsets.UTF_8).lines().toList());
    }

    /**
     * @return an input stream of the given lines, each terminated by \n.
     */
    private static InputStream lines(String... lines) {
        String text = String.join("\n", lines) + "\n";
        return new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));
    }

    private InputStream getImpossibleIS(byte[] repeatingBytes, int maxSize) {
        return new InputStream() {
            int pos = 0;
            int remaining = maxSize;
            @Override
            public int read() throws IOException {
                if (pos == repeatingBytes.length) {
                    pos = 0;
                }
                if (remaining <= 0) {
                    return -1;
                }
                remaining--;
                return repeatingBytes[pos++];
            }
        };
    }
}

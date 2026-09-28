import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
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

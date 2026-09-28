import com.nuix.interview.robot.command.CommandParseException;
import com.nuix.interview.robot.command.CommandParser;
import com.nuix.interview.robot.model.Zone;
import com.nuix.interview.robot.simulation.CommandIgnoredException;
import com.nuix.interview.robot.simulation.Simulation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * The application is a simulation of a Rover Robot moving on a square zone to be explored, of
 * dimensions 10 units x 10 units. Coordinates start at 0,0 (bottom left corner).
 * - The input is an input stream of unknown length that will contain a series of lines terminated in \n.
 * - The following commands may be sent:
 * DEPLOY X,Y,F : Deploys the robot to position X,Y if it is a valid position
 * PIT X,Y      : Adds a pit
 * MOVE         : Moves the robot by 1 position in the current orientation direction
 * LEFT         : Robot will stay in the same coordinate, but face the direction to the left of the current one (i.e.: SOUTH -> EAST)
 * RIGHT        : Robot will stay in the same coordinate, but face the direction to the right of the current one (i.e.: SOUTH -> WEST)
 * REPORT       : Issue a line to the report in the format "X,Y,DIRECTION" (without quotes and all caps)
 * - The robot can not be deployed to a position with a pit or off the limits of the zone.
 * - PIT commands are optional and can only follow a DEPLOY command and precede MOVE, LEFT, RIGHT.
 * - REPORT commands can happen at any time.
 * - The first command to be considered is DEPLOY. Commands before this can be safely ignored.
 * - Failure to parse one line should be logged to System.err but continue
 * - Multiple DEPLOYs can be called, so:
 * -- PITs can follow any DEPLOY command, not necessarily the first one.
 * -- The robot can't be deployed on a PIT.
 * -- A new DEPLOY command redeploys the "same" robot (there is only one on the board).
 * -- Multiple PIT commands can be issued, as long as they only follow a DEPLOY or another PIT command.
 * - The robot can't move into PIT or off the zone.
 * - The following error messages can be reported back (in this exact format):
 * -- "Outside Zone: Ignored"   -> When trying to deploy/move out of the zone
 * -- "PIT Detected: Ignored"   -> When trying to deploy/move onto a pit.
 * -- "ROBOT Detected: Ignored" -> When trying to add a pit to the current position of the robot.
 * <p>
 * Create an application that can read in commands of the following form:
 * <p>
 * DEPLOY X,Y,F
 * PIT X,Y
 * PIT X,Y
 * PIT X,Y
 * MOVE
 * LEFT
 * RIGHT
 * REPORT
 */
public class RobotSimulator {

    private static final int ZONE_WIDTH = 10;
    private static final int ZONE_HEIGHT = 10;
    private static final String BYTE_ORDER_MARK = "﻿";

    private final CommandParser parser = new CommandParser();
    private final PrintStream errors;

    public RobotSimulator() {
        this(System.err);
    }

    /**
     * @param errors where lines that are malformed or ignored are logged.
     */
    public RobotSimulator(PrintStream errors) {
        this.errors = errors;
    }

    /**
     * Should process the input and return the report lines as result.
     * <p>
     * The input is read one line at a time, so it can be of any length, and it is left open for the caller.
     *
     * @param input the input.
     * @return the reported lines.
     * @throws UncheckedIOException if the input can't be read.
     */
    public List<String> process(InputStream input) {
        Simulation simulation = new Simulation(new Zone(ZONE_WIDTH, ZONE_HEIGHT));
        // Not closed: closing the reader would close the caller's stream.
        BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));
        int lineNumber = 0;
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                // Some editors start UTF-8 files with an invisible byte-order mark; the decoder keeps it as a character.
                if (lineNumber == 1 && line.startsWith(BYTE_ORDER_MARK)) {
                    line = line.substring(BYTE_ORDER_MARK.length());
                }
                try {
                    simulation.apply(parser.parse(line));
                } catch (CommandParseException | CommandIgnoredException e) {
                    errors.println("Line " + lineNumber + ": '" + line + "' " + e.getMessage());
                }
            }
        } catch (IOException e) {
            errors.println("Failed to read input after line " + lineNumber + ": " + e.getMessage());
            throw new UncheckedIOException("Failed to read input after line " + lineNumber, e);
        }
        return simulation.report();
    }
}

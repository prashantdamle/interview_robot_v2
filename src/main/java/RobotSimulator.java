import java.io.InputStream;
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
    /**
     * Should process the input and return the report lines as result.
     *
     * @param input the input.
     * @return the reported lines.
     */
    public List<String> process(InputStream input) {
        throw new UnsupportedOperationException();
    }
}

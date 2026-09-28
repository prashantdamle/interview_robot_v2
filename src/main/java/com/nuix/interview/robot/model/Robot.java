package com.nuix.interview.robot.model;

import java.util.Locale;

/**
 * The robot: where it is and which way it faces. It knows nothing of the zone or pits, so the caller checks
 * {@link #nextPosition()} is safe before calling {@link #move()}.
 */
public class Robot {

    private static final String REPORT_FORMAT = "%d,%d,%s";

    private Position position;
    private Direction facing;

    public Robot(Position position, Direction facing) {
        this.position = position;
        this.facing = facing;
    }

    public Position position() {
        return position;
    }

    public Direction facing() {
        return facing;
    }

    /**
     * Redeploys this robot to a new position and direction. The caller checks the position is safe first.
     */
    public void deployTo(Position position, Direction facing) {
        this.position = position;
        this.facing = facing;
    }

    /**
     * @return the position one step ahead in the direction the robot faces. The robot doesn't move.
     */
    public Position nextPosition() {
        return position.step(facing);
    }

    /**
     * Moves the robot one step ahead, to {@link #nextPosition()}.
     */
    public void move() {
        position = nextPosition();
    }

    public void turnLeft() {
        facing = facing.left();
    }

    public void turnRight() {
        facing = facing.right();
    }

    /**
     * @return the robot's report line, "X,Y,DIRECTION".
     */
    public String report() {
        return String.format(Locale.ROOT, REPORT_FORMAT,position.x(), position.y(), facing);
    }
}

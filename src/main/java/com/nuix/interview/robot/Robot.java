package com.nuix.interview.robot;

/**
 * The robot: where it is and which way it faces. It knows nothing of the zone or pits, so the caller checks
 * {@link #nextPosition()} is safe before calling {@link #move()}.
 */
public class Robot {

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
}

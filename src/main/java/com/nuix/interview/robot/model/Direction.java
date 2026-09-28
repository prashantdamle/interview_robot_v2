package com.nuix.interview.robot.model;

/**
 * The compass direction the robot faces.
 */
public enum Direction {
    NORTH(0, 1),
    EAST(1, 0),
    SOUTH(0, -1),
    WEST(-1, 0);

    private final int dx;
    private final int dy;

    Direction(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    /**
     * @return the change in x when moving one unit in this direction.
     */
    public int dx() {
        return dx;
    }

    /**
     * @return the change in y when moving one unit in this direction (0,0 is the bottom left corner).
     */
    public int dy() {
        return dy;
    }

    /**
     * @return the direction to the left of this one, as seen by the robot.
     */
    public Direction left() {
        return switch (this) {
            case NORTH -> WEST;
            case WEST -> SOUTH;
            case SOUTH -> EAST;
            case EAST -> NORTH;
        };
    }

    /**
     * @return the direction to the right of this one, as seen by the robot.
     */
    public Direction right() {
        return switch (this) {
            case NORTH -> EAST;
            case EAST -> SOUTH;
            case SOUTH -> WEST;
            case WEST -> NORTH;
        };
    }
}

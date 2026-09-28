package com.nuix.interview.robot;

/**
 * A coordinate in the zone, with 0,0 at the bottom left corner. Immutable, so it can be used as a pit in a set.
 */
public record Position(int x, int y) {

    /**
     * @return the adjacent position one unit away in the given direction; this position is unchanged.
     */
    public Position step(Direction direction) {
        return new Position(x + direction.dx(), y + direction.dy());
    }
}

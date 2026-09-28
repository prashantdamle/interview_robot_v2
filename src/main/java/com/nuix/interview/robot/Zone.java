package com.nuix.interview.robot;

/**
 * The rectangular zone the robot explores, from 0,0 (bottom left) to width-1,height-1.
 */
public record Zone(int width, int height) {

    public Zone {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException(
                    "Zone width and height must be positive, but were " + width + "x" + height);
        }
    }

    public boolean contains(Position position) {
        return position.x() >= 0 && position.x() < width
                && position.y() >= 0 && position.y() < height;
    }
}

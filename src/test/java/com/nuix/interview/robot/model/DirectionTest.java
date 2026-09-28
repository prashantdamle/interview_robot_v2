package com.nuix.interview.robot.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DirectionTest {

    @Test
    @DisplayName("left() turns anticlockwise, e.g. SOUTH -> EAST")
    void leftTurnsAnticlockwise() {
        assertAll(
                () -> assertEquals(Direction.WEST, Direction.NORTH.left()),
                () -> assertEquals(Direction.SOUTH, Direction.WEST.left()),
                () -> assertEquals(Direction.EAST, Direction.SOUTH.left()),
                () -> assertEquals(Direction.NORTH, Direction.EAST.left())
        );
    }

    @Test
    @DisplayName("right() turns clockwise, e.g. SOUTH -> WEST")
    void rightTurnsClockwise() {
        assertAll(
                () -> assertEquals(Direction.EAST, Direction.NORTH.right()),
                () -> assertEquals(Direction.SOUTH, Direction.EAST.right()),
                () -> assertEquals(Direction.WEST, Direction.SOUTH.right()),
                () -> assertEquals(Direction.NORTH, Direction.WEST.right())
        );
    }

    @Test
    @DisplayName("each direction steps one unit along its axis, with NORTH increasing y (0,0 is bottom left)")
    void stepsOneUnitAlongItsAxis() {
        assertAll(
                () -> assertEquals(0, Direction.NORTH.dx()),
                () -> assertEquals(1, Direction.NORTH.dy()),
                () -> assertEquals(1, Direction.EAST.dx()),
                () -> assertEquals(0, Direction.EAST.dy()),
                () -> assertEquals(0, Direction.SOUTH.dx()),
                () -> assertEquals(-1, Direction.SOUTH.dy()),
                () -> assertEquals(-1, Direction.WEST.dx()),
                () -> assertEquals(0, Direction.WEST.dy())
        );
    }
}

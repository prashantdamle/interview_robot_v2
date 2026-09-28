package com.nuix.interview.robot;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PositionTest {

    @Test
    @DisplayName("step() returns the adjacent position in the given direction, leaving the original unchanged")
    void stepReturnsAdjacentPosition() {
        Position origin = new Position(5, 5);

        assertAll(
                () -> assertEquals(new Position(5, 6), origin.step(Direction.NORTH)),
                () -> assertEquals(new Position(6, 5), origin.step(Direction.EAST)),
                () -> assertEquals(new Position(5, 4), origin.step(Direction.SOUTH)),
                () -> assertEquals(new Position(4, 5), origin.step(Direction.WEST)),
                () -> assertEquals(new Position(5, 5), origin)
        );
    }
}

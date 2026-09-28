package com.nuix.interview.robot;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RobotTest {

    @Test
    @DisplayName("nextPosition() is one step ahead without moving; move() then goes there, keeping direction")
    void moveStepsForwardKeepingDirection() {
        Robot robot = new Robot(new Position(2, 3), Direction.EAST);

        assertEquals(new Position(3, 3), robot.nextPosition());
        assertEquals(new Position(2, 3), robot.position());

        robot.move();

        assertAll(
                () -> assertEquals(new Position(3, 3), robot.position()),
                () -> assertEquals(Direction.EAST, robot.facing())
        );
    }

    @Test
    @DisplayName("turnLeft() and turnRight() change only the direction faced")
    void turnsChangeOnlyDirection() {
        Robot robot = new Robot(new Position(2, 3), Direction.SOUTH);

        robot.turnLeft();
        assertEquals(Direction.EAST, robot.facing());

        robot.turnRight();
        robot.turnRight();
        assertAll(
                () -> assertEquals(Direction.WEST, robot.facing()),
                () -> assertEquals(new Position(2, 3), robot.position())
        );
    }
}

package com.nuix.interview.robot.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Locale;

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

    @Test
    @DisplayName("deployTo() places the same robot at a new position and direction")
    void deployToRepositionsTheRobot() {
        Robot robot = new Robot(new Position(2, 3), Direction.SOUTH);

        robot.deployTo(new Position(7, 1), Direction.WEST);

        assertAll(
                () -> assertEquals(new Position(7, 1), robot.position()),
                () -> assertEquals(Direction.WEST, robot.facing())
        );
    }

    @Test
    @DisplayName("report() formats the robot as X,Y,DIRECTION")
    void reportFormatsPositionAndDirection() {
        Robot robot = new Robot(new Position(1, 0), Direction.EAST);

        assertEquals("1,0,EAST", robot.report());
    }

    @Test
    @DisplayName("report() uses ASCII digits whatever the default locale")
    void reportIgnoresDefaultLocale() {
        Robot robot = new Robot(new Position(1, 0), Direction.EAST);
        Locale original = Locale.getDefault();
        // Arabic (Egypt) formats numbers with Arabic-Indic digits.
        Locale.setDefault(Locale.forLanguageTag("ar-EG"));
        try {
            assertEquals("1,0,EAST", robot.report());
        } finally {
            Locale.setDefault(original);
        }
    }
}

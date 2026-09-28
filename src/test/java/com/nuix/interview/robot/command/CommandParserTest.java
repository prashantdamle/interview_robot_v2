package com.nuix.interview.robot.command;

import com.nuix.interview.robot.model.Direction;
import com.nuix.interview.robot.model.Position;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CommandParserTest {

    private final CommandParser parser = new CommandParser();

    @Test
    @DisplayName("parses DEPLOY X,Y,F into a deploy command")
    void parsesDeploy() throws CommandParseException {
        assertEquals(new DeployCommand(new Position(1, 2), Direction.NORTH), parser.parse("DEPLOY 1,2,NORTH"));
    }

    @Test
    @DisplayName("rejects DEPLOY with an unknown direction or a coordinate too large for an int")
    void rejectsDeployWithBadDirectionOrCoordinate() {
        assertAll(
                () -> assertThrows(CommandParseException.class, () -> parser.parse("DEPLOY 1,2,UP")),
                () -> assertThrows(CommandParseException.class, () -> parser.parse("DEPLOY 99999999999,2,NORTH"))
        );
    }

    @Test
    @DisplayName("parses PIT X,Y into a pit command, rejecting a coordinate too large for an int")
    void parsesPit() throws CommandParseException {
        assertEquals(new PitCommand(new Position(5, 6)), parser.parse("PIT 5,6"));
        assertThrows(CommandParseException.class, () -> parser.parse("PIT 5,99999999999"));
    }

    @Test
    @DisplayName("parses MOVE, LEFT, RIGHT and REPORT")
    void parsesCommandsWithoutArguments() {
        assertAll(
                () -> assertEquals(new MoveCommand(), parser.parse("MOVE")),
                () -> assertEquals(new LeftCommand(), parser.parse("LEFT")),
                () -> assertEquals(new RightCommand(), parser.parse("RIGHT")),
                () -> assertEquals(new ReportCommand(), parser.parse("REPORT"))
        );
    }

    @Test
    @DisplayName("parse failure messages give the reason without repeating the line")
    void parseFailureMessagesGiveReason() {
        assertAll(
                () -> assertEquals("not parsed: not a valid command",
                        assertThrows(CommandParseException.class, () -> parser.parse("RIHT")).getMessage()),
                () -> assertEquals("not parsed: unknown direction 'UP'",
                        assertThrows(CommandParseException.class, () -> parser.parse("DEPLOY 1,2,UP")).getMessage()),
                () -> assertEquals("not parsed: coordinate '99999999999' out of range",
                        assertThrows(CommandParseException.class, () -> parser.parse("PIT 99999999999,2")).getMessage())
        );
    }

    @Test
    @DisplayName("ignores leading and trailing whitespace")
    void ignoresSurroundingWhitespace() {
        assertAll(
                () -> assertEquals(new MoveCommand(), parser.parse("  MOVE\t")),
                () -> assertEquals(new PitCommand(new Position(1, 1)), parser.parse(" PIT 1,1 "))
        );
    }

    @Test
    @DisplayName("rejects malformed lines: wrong case, extra spaces, extra or missing arguments, blank or unknown")
    void rejectsMalformedLines() {
        assertAll(
                () -> assertThrows(CommandParseException.class, () -> parser.parse("move")),
                () -> assertThrows(CommandParseException.class, () -> parser.parse("DEPLOY 1,2,north")),
                () -> assertThrows(CommandParseException.class, () -> parser.parse("DEPLOY  1,2,NORTH")),
                () -> assertThrows(CommandParseException.class, () -> parser.parse("PIT 1, 2")),
                () -> assertThrows(CommandParseException.class, () -> parser.parse("MOVE 1")),
                () -> assertThrows(CommandParseException.class, () -> parser.parse("PIT 1,2,NORTH")),
                () -> assertThrows(CommandParseException.class, () -> parser.parse("DEPLOY 0,ORTH")),
                () -> assertThrows(CommandParseException.class, () -> parser.parse("DEPLOY 1.5,2,NORTH")),
                () -> assertThrows(CommandParseException.class, () -> parser.parse("RIHT")),
                () -> assertThrows(CommandParseException.class, () -> parser.parse("")),
                () -> assertThrows(CommandParseException.class, () -> parser.parse("   "))
        );
    }
}

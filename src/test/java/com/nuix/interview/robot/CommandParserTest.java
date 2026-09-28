package com.nuix.interview.robot;

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
}

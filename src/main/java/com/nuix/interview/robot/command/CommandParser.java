package com.nuix.interview.robot.command;

import com.nuix.interview.robot.model.Direction;
import com.nuix.interview.robot.model.Position;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses one input line into a {@link Command}. Keywords are upper case and the format is strict; only
 * leading and trailing whitespace is ignored.
 */
public class CommandParser {

    private static final Pattern DEPLOY = Pattern.compile("DEPLOY (-?\\d+),(-?\\d+),(\\w+)");
    private static final Pattern PIT = Pattern.compile("PIT (-?\\d+),(-?\\d+)");

    public Command parse(String line) throws CommandParseException {
        String trimmed = line.trim();

        // Commands without arguments; anything else falls through to the patterns below.
        switch (trimmed) {
            case "MOVE":
                return new MoveCommand();
            case "LEFT":
                return new LeftCommand();
            case "RIGHT":
                return new RightCommand();
            case "REPORT":
                return new ReportCommand();
        }

        Matcher deploy = DEPLOY.matcher(trimmed);
        if (deploy.matches()) {
            Position position = new Position(parseCoordinate(deploy.group(1)), parseCoordinate(deploy.group(2)));
            return new DeployCommand(position, parseDirection(deploy.group(3)));
        }

        Matcher pit = PIT.matcher(trimmed);
        if (pit.matches()) {
            return new PitCommand(new Position(parseCoordinate(pit.group(1)), parseCoordinate(pit.group(2))));
        }

        throw new CommandParseException("not parsed: not a valid command");
    }

    private int parseCoordinate(String coordinate) throws CommandParseException {
        try {
            return Integer.parseInt(coordinate);
        } catch (NumberFormatException e) {
            throw new CommandParseException("not parsed: coordinate '" + coordinate + "' out of range", e);
        }
    }

    private Direction parseDirection(String direction) throws CommandParseException {
        try {
            return Direction.valueOf(direction);
        } catch (IllegalArgumentException e) {
            throw new CommandParseException("not parsed: unknown direction '" + direction + "'", e);
        }
    }
}

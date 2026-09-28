package com.nuix.interview.robot;

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
            Position position = new Position(parseCoordinate(deploy.group(1), line), parseCoordinate(deploy.group(2), line));
            return new DeployCommand(position, parseDirection(deploy.group(3), line));
        }

        Matcher pit = PIT.matcher(trimmed);
        if (pit.matches()) {
            return new PitCommand(new Position(parseCoordinate(pit.group(1), line), parseCoordinate(pit.group(2), line)));
        }

        throw new CommandParseException("Unrecognised command: '" + line + "'");
    }

    private int parseCoordinate(String coordinate, String line) throws CommandParseException {
        try {
            return Integer.parseInt(coordinate);
        } catch (NumberFormatException e) {
            throw new CommandParseException("Coordinate out of range in '" + line + "'", e);
        }
    }

    private Direction parseDirection(String direction, String line) throws CommandParseException {
        try {
            return Direction.valueOf(direction);
        } catch (IllegalArgumentException e) {
            throw new CommandParseException("Unknown direction in '" + line + "'", e);
        }
    }
}

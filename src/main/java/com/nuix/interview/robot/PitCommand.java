package com.nuix.interview.robot;

/**
 * PIT X,Y: add a pit at a position.
 */
public record PitCommand(Position position) implements Command {
}

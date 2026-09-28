package com.nuix.interview.robot.command;

import com.nuix.interview.robot.model.Position;

/**
 * PIT X,Y: add a pit at a position.
 */
public record PitCommand(Position position) implements Command {
}

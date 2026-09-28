package com.nuix.interview.robot.command;

import com.nuix.interview.robot.model.Direction;
import com.nuix.interview.robot.model.Position;

/**
 * DEPLOY X,Y,F: place the robot at a position, facing a direction.
 */
public record DeployCommand(Position position, Direction facing) implements Command {
}

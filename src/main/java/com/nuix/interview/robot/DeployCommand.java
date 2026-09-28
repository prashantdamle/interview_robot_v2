package com.nuix.interview.robot;

/**
 * DEPLOY X,Y,F: place the robot at a position, facing a direction.
 */
public record DeployCommand(Position position, Direction facing) implements Command {
}

package com.nuix.interview.robot.command;

/**
 * A command read from one input line.
 */
public sealed interface Command permits DeployCommand, PitCommand, MoveCommand, LeftCommand, RightCommand, ReportCommand {
}

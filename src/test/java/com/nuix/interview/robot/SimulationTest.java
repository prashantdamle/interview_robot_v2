package com.nuix.interview.robot;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SimulationTest {

    private final Simulation simulation = new Simulation(new Zone(10, 10));

    @Test
    @DisplayName("REPORT after a DEPLOY reports the robot's position and direction")
    void reportsDeployedRobot() throws CommandIgnoredException {
        simulation.apply(new DeployCommand(new Position(1, 2), Direction.EAST));
        simulation.apply(new ReportCommand());

        assertEquals(List.of("1,2,EAST"), simulation.report());
    }

    @Test
    @DisplayName("DEPLOY outside the zone reports 'Outside Zone' and leaves the robot as it was (or undeployed)")
    void deployOutsideZoneIsRejected() throws CommandIgnoredException {
        simulation.apply(new DeployCommand(new Position(10, 9), Direction.NORTH));
        assertThrows(CommandIgnoredException.class, () -> simulation.apply(new ReportCommand()));

        simulation.apply(new DeployCommand(new Position(0, 0), Direction.NORTH));
        simulation.apply(new DeployCommand(new Position(-1, 5), Direction.EAST));
        simulation.apply(new ReportCommand());

        assertEquals(List.of("Outside Zone: Ignored", "Outside Zone: Ignored", "0,0,NORTH"), simulation.report());
    }

    @Test
    @DisplayName("DEPLOY onto a pit reports 'PIT Detected' and keeps the robot; a valid redeploy moves it")
    void deployOntoPitIsRejectedAndValidRedeployMovesRobot() throws CommandIgnoredException {
        simulation.apply(new DeployCommand(new Position(0, 0), Direction.NORTH));
        simulation.apply(new PitCommand(new Position(5, 5)));
        simulation.apply(new DeployCommand(new Position(5, 5), Direction.EAST));
        simulation.apply(new ReportCommand());
        simulation.apply(new DeployCommand(new Position(5, 6), Direction.SOUTH));
        simulation.apply(new ReportCommand());

        assertEquals(List.of("PIT Detected: Ignored", "0,0,NORTH", "5,6,SOUTH"), simulation.report());
    }

    @Test
    @DisplayName("every command before the first DEPLOY is ignored, including REPORT")
    void ignoresCommandsBeforeFirstDeploy() {
        assertAll(
                () -> assertThrows(CommandIgnoredException.class, () -> simulation.apply(new ReportCommand())),
                () -> assertThrows(CommandIgnoredException.class, () -> simulation.apply(new PitCommand(new Position(1, 1)))),
                () -> assertThrows(CommandIgnoredException.class, () -> simulation.apply(new MoveCommand())),
                () -> assertThrows(CommandIgnoredException.class, () -> simulation.apply(new LeftCommand())),
                () -> assertThrows(CommandIgnoredException.class, () -> simulation.apply(new RightCommand())),
                () -> assertEquals(List.of(), simulation.report())
        );
    }
}

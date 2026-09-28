package com.nuix.interview.robot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * One run of the robot in a zone: applies commands according to the rules and collects the report lines.
 */
public class Simulation {

    private final Zone zone;
    private final Set<Position> pits = new HashSet<>();
    private final List<String> report = new ArrayList<>();
    private Robot robot;

    public Simulation(Zone zone) {
        this.zone = zone;
    }

    public void apply(Command command) throws CommandIgnoredException {
        // Nothing but DEPLOY is considered until a robot is on the board.
        if (robot == null && !(command instanceof DeployCommand)) {
            throw new CommandIgnoredException("ignored: no robot deployed yet");
        }

        switch (command) {
            case DeployCommand deploy -> deploy(deploy);
            case ReportCommand ignored -> report.add(robot.report());
            case PitCommand pit -> addPit(pit);
            case MoveCommand ignored -> throw new UnsupportedOperationException("MOVE not implemented yet");
            case LeftCommand ignored -> throw new UnsupportedOperationException("LEFT not implemented yet");
            case RightCommand ignored -> throw new UnsupportedOperationException("RIGHT not implemented yet");
        }
    }

    /**
     * @return a read-only view of the report lines so far.
     */
    public List<String> report() {
        return Collections.unmodifiableList(report);
    }

    private void deploy(DeployCommand deploy) {
        if (!zone.contains(deploy.position())) {
            report.add(Rejection.OUTSIDE_ZONE.message());
            return;
        }
        if (pits.contains(deploy.position())) {
            report.add(Rejection.PIT_DETECTED.message());
            return;
        }
        // There is only one robot: the first successful DEPLOY creates it, later ones move it.
        if (robot == null) {
            robot = new Robot(deploy.position(), deploy.facing());
        } else {
            robot.deployTo(deploy.position(), deploy.facing());
        }
    }

    private void addPit(PitCommand pit) throws CommandIgnoredException {
        if (!zone.contains(pit.position())) {
            throw new CommandIgnoredException("ignored: pit is outside the zone");
        }
        if (pit.position().equals(robot.position())) {
            report.add(Rejection.ROBOT_DETECTED.message());
            return;
        }
        pits.add(pit.position());
    }
}

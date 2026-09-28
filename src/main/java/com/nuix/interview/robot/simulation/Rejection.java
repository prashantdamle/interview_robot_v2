package com.nuix.interview.robot.simulation;

/**
 * The report lines for commands the rules reject, in the exact format the spec requires.
 */
enum Rejection {
    OUTSIDE_ZONE("Outside Zone: Ignored"),
    PIT_DETECTED("PIT Detected: Ignored"),
    ROBOT_DETECTED("ROBOT Detected: Ignored");

    private final String message;

    Rejection(String message) {
        this.message = message;
    }

    public String message() {
        return message;
    }
}

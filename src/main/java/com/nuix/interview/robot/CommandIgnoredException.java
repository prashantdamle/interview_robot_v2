package com.nuix.interview.robot;

/**
 * Thrown when a well-formed command is dropped without effect, e.g. a PIT that doesn't follow a DEPLOY.
 * Such commands produce no report line; the message says why the command was ignored.
 */
public class CommandIgnoredException extends Exception {

    public CommandIgnoredException(String message) {
        super(message);
    }
}

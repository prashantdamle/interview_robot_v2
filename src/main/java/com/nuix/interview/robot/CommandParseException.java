package com.nuix.interview.robot;

/**
 * Thrown when an input line isn't a well-formed command.
 */
public class CommandParseException extends Exception {

    public CommandParseException(String message) {
        super(message);
    }

    public CommandParseException(String message, Throwable cause) {
        super(message, cause);
    }
}

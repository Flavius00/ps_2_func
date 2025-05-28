package com.example.demo.exception;

public class InvalidOperationException extends BusinessLogicException {

    public InvalidOperationException(String message) {
        super(message, "INVALID_OPERATION");
    }

    public InvalidOperationException(String operation, String reason) {
        super("Cannot perform operation '" + operation + "': " + reason, "INVALID_OPERATION");
    }
}

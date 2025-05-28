package com.example.demo.exception;

public class DatabaseOperationException extends RuntimeException {
    private final String operation;

    public DatabaseOperationException(String operation, String message) {
        super("Database operation '" + operation + "' failed: " + message);
        this.operation = operation;
    }

    public DatabaseOperationException(String operation, String message, Throwable cause) {
        super("Database operation '" + operation + "' failed: " + message, cause);
        this.operation = operation;
    }

    public String getOperation() {
        return operation;
    }
}
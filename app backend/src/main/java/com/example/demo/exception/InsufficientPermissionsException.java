package com.example.demo.exception;

public class InsufficientPermissionsException extends BusinessLogicException {

    public InsufficientPermissionsException(String message) {
        super(message, "INSUFFICIENT_PERMISSIONS");
    }

    public InsufficientPermissionsException(String action, String resourceType) {
        super("You don't have permission to " + action + " " + resourceType, "INSUFFICIENT_PERMISSIONS");
    }
}
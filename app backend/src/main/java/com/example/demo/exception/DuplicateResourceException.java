package com.example.demo.exception;

public class DuplicateResourceException extends BusinessLogicException {

    public DuplicateResourceException(String resourceType, String identifier) {
        super(resourceType + " with identifier '" + identifier + "' already exists", "DUPLICATE_RESOURCE");
    }

    public DuplicateResourceException(String message) {
        super(message, "DUPLICATE_RESOURCE");
    }
}
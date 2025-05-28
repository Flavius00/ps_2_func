package com.example.demo.exception;

public class SpaceAlreadyRentedException extends BusinessLogicException {

    public SpaceAlreadyRentedException(String spaceName) {
        super("Space '" + spaceName + "' is already rented and not available", "SPACE_ALREADY_RENTED");
    }

    public SpaceAlreadyRentedException(Long spaceId) {
        super("Space with ID " + spaceId + " is already rented and not available", "SPACE_ALREADY_RENTED");
    }
}

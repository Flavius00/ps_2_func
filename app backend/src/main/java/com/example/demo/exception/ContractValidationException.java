package com.example.demo.exception;

public class ContractValidationException extends BusinessLogicException {

    public ContractValidationException(String message) {
        super(message, "CONTRACT_VALIDATION_ERROR");
    }

    public static ContractValidationException invalidDateRange() {
        return new ContractValidationException("Contract end date must be after start date");
    }

    public static ContractValidationException contractTooShort() {
        return new ContractValidationException("Contract duration must be at least 1 month");
    }

    public static ContractValidationException contractTooLong() {
        return new ContractValidationException("Contract duration cannot exceed 5 years");
    }
}

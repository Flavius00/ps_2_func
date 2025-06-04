package com.example.demo.validation.validator;

import com.example.demo.validation.annotation.ValidRentalPrice;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class RentalPriceValidator implements ConstraintValidator<ValidRentalPrice, Double> {

    private double min;
    private double max;

    @Override
    public void initialize(ValidRentalPrice constraintAnnotation) {
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(Double price, ConstraintValidatorContext context) {
        if (price == null) {
            return true; // Let @NotNull handle null validation
        }

        boolean isValid = price >= min && price <= max;

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    String.format("Rental price must be between %.2f and %.2f RON per month", min, max)
            ).addConstraintViolation();
        }

        return isValid;
    }
}
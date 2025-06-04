package com.example.demo.validation.validator;

import com.example.demo.validation.annotation.ValidAreaRange;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AreaRangeValidator implements ConstraintValidator<ValidAreaRange, Double> {

    private double min;
    private double max;

    @Override
    public void initialize(ValidAreaRange constraintAnnotation) {
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(Double area, ConstraintValidatorContext context) {
        if (area == null) {
            return true; // Let @NotNull handle null validation
        }

        return area >= min && area <= max;
    }
}
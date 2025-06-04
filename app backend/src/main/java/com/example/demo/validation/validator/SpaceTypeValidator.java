package com.example.demo.validation.validator;

import com.example.demo.model.ComercialSpace;
import com.example.demo.validation.annotation.ValidSpaceType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SpaceTypeValidator implements ConstraintValidator<ValidSpaceType, String> {

    @Override
    public void initialize(ValidSpaceType constraintAnnotation) {
        // Initialization logic if needed
    }

    @Override
    public boolean isValid(String spaceType, ConstraintValidatorContext context) {
        if (spaceType == null || spaceType.trim().isEmpty()) {
            return true; // Let @NotBlank handle null/empty validation
        }

        try {
            ComercialSpace.SpaceType.valueOf(spaceType.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
package com.example.demo.validation.validator;

import com.example.demo.validation.annotation.ValidBusinessType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Set;

public class BusinessTypeValidator implements ConstraintValidator<ValidBusinessType, String> {

    private static final Set<String> ALLOWED_BUSINESS_TYPES = Set.of(
            "Retail - Clothing",
            "Retail - Food",
            "Retail - Electronics",
            "Software Development",
            "Consulting",
            "Marketing",
            "Healthcare",
            "Education",
            "Finance",
            "Real Estate",
            "Manufacturing",
            "Logistics",
            "Tourism",
            "Entertainment",
            "Other"
    );

    @Override
    public void initialize(ValidBusinessType constraintAnnotation) {
        // Initialization logic if needed
    }

    @Override
    public boolean isValid(String businessType, ConstraintValidatorContext context) {
        if (businessType == null || businessType.trim().isEmpty()) {
            return true; // Let @NotBlank handle null/empty validation
        }

        return ALLOWED_BUSINESS_TYPES.contains(businessType.trim());
    }
}

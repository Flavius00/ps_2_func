package com.example.demo.validation.validator;

import com.example.demo.validation.annotation.ValidPhoneNumber;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class PhoneNumberValidator implements ConstraintValidator<ValidPhoneNumber, String> {

    private static final Pattern ROMANIAN_PHONE_PATTERN = Pattern.compile(
            "^(\\+40|0040|0)?([1-9][0-9]{8})$"
    );

    @Override
    public void initialize(ValidPhoneNumber constraintAnnotation) {
        // Initialization logic if needed
    }

    @Override
    public boolean isValid(String phoneNumber, ConstraintValidatorContext context) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return true; // Let @NotBlank handle null/empty validation
        }

        // Remove spaces and dashes for validation
        String cleanPhone = phoneNumber.replaceAll("[\\s-]", "");

        return ROMANIAN_PHONE_PATTERN.matcher(cleanPhone).matches();
    }
}
package com.example.demo.validation.validator;

import com.example.demo.validation.annotation.ValidTaxId;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class TaxIdValidator implements ConstraintValidator<ValidTaxId, String> {

    private static final Pattern TAX_ID_PATTERN = Pattern.compile("^RO[0-9]{2,10}$");

    @Override
    public void initialize(ValidTaxId constraintAnnotation) {
        // Initialization logic if needed
    }

    @Override
    public boolean isValid(String taxId, ConstraintValidatorContext context) {
        if (taxId == null || taxId.trim().isEmpty()) {
            return true; // Let @NotBlank handle null/empty validation
        }

        return TAX_ID_PATTERN.matcher(taxId.trim().toUpperCase()).matches();
    }
}
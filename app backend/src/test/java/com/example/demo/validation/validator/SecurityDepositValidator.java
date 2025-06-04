package com.example.demo.validation.validator;

import com.example.demo.validation.annotation.ValidSecurityDeposit;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.reflect.Field;

public class SecurityDepositValidator implements ConstraintValidator<ValidSecurityDeposit, Object> {

    private double minMonths;
    private double maxMonths;

    @Override
    public void initialize(ValidSecurityDeposit constraintAnnotation) {
        this.minMonths = constraintAnnotation.minMonths();
        this.maxMonths = constraintAnnotation.maxMonths();
    }

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        if (obj == null) {
            return true;
        }

        try {
            Field monthlyRentField = obj.getClass().getDeclaredField("monthlyRent");
            Field securityDepositField = obj.getClass().getDeclaredField("securityDeposit");

            monthlyRentField.setAccessible(true);
            securityDepositField.setAccessible(true);

            Double monthlyRent = (Double) monthlyRentField.get(obj);
            Double securityDeposit = (Double) securityDepositField.get(obj);

            if (monthlyRent == null || securityDeposit == null) {
                return true; // Let @NotNull handle null validation
            }

            if (monthlyRent <= 0) {
                return true; // Let other validators handle invalid rent
            }

            double depositMonths = securityDeposit / monthlyRent;
            boolean isValid = depositMonths >= minMonths && depositMonths <= maxMonths;

            if (!isValid) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                        String.format("Security deposit must be between %.1f and %.1f months of rent",
                                minMonths, maxMonths)
                ).addConstraintViolation();
            }

            return isValid;

        } catch (Exception e) {
            return false;
        }
    }
}
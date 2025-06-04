package com.example.demo.validation.validator;

import com.example.demo.validation.annotation.ValidContractDates;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.reflect.Field;
import java.time.LocalDate;

public class ValidContractDatesValidator implements ConstraintValidator<ValidContractDates, Object> {

    @Override
    public void initialize(ValidContractDates constraintAnnotation) {
        // Initialization logic if needed
    }

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        if (obj == null) {
            return true;
        }

        try {
            Field startDateField = obj.getClass().getDeclaredField("startDate");
            Field endDateField = obj.getClass().getDeclaredField("endDate");

            startDateField.setAccessible(true);
            endDateField.setAccessible(true);

            LocalDate startDate = (LocalDate) startDateField.get(obj);
            LocalDate endDate = (LocalDate) endDateField.get(obj);

            if (startDate == null || endDate == null) {
                return true; // Let @NotNull handle null validation
            }

            boolean isValid = endDate.isAfter(startDate);

            if (!isValid) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                        "End date must be after start date"
                ).addConstraintViolation();
            }

            return isValid;

        } catch (Exception e) {
            return false;
        }
    }
}
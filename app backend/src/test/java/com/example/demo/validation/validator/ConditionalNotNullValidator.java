package com.example.demo.validation.validator;

import com.example.demo.validation.annotation.ConditionalNotNull;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.reflect.Field;

public class ConditionalNotNullValidator implements ConstraintValidator<ConditionalNotNull, Object> {

    private String field;
    private String condition;
    private String value;

    @Override
    public void initialize(ConditionalNotNull constraintAnnotation) {
        this.field = constraintAnnotation.field();
        this.condition = constraintAnnotation.condition();
        this.value = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        if (obj == null) {
            return true;
        }

        try {
            Field fieldToValidate = obj.getClass().getDeclaredField(field);
            Field conditionField = obj.getClass().getDeclaredField(condition);

            fieldToValidate.setAccessible(true);
            conditionField.setAccessible(true);

            Object fieldValue = fieldToValidate.get(obj);
            Object conditionValue = conditionField.get(obj);

            // If condition matches, field must not be null
            if (value.equals(String.valueOf(conditionValue))) {
                boolean isValid = fieldValue != null;

                if (!isValid) {
                    context.disableDefaultConstraintViolation();
                    context.buildConstraintViolationWithTemplate(
                            String.format("Field %s is required when %s is %s", field, condition, value)
                    ).addPropertyNode(field).addConstraintViolation();
                }

                return isValid;
            }

            return true;

        } catch (Exception e) {
            return false;
        }
    }
}

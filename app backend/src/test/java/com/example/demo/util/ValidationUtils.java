
package com.example.demo.util;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ValidationUtils {

    private final Validator validator;

    public ValidationUtils(Validator validator) {
        this.validator = validator;
    }

    /**
     * Validates an object and returns validation errors as a map
     */
    public <T> Map<String, String> getValidationErrors(T object, Class<?>... groups) {
        Set<ConstraintViolation<T>> violations = validator.validate(object, groups);

        return violations.stream()
                .collect(Collectors.toMap(
                        violation -> violation.getPropertyPath().toString(),
                        ConstraintViolation::getMessage,
                        (existing, replacement) -> existing + "; " + replacement
                ));
    }

    /**
     * Checks if an object is valid
     */
    public <T> boolean isValid(T object, Class<?>... groups) {
        return validator.validate(object, groups).isEmpty();
    }

    /**
     * Validates a specific property of an object
     */
    public <T> Set<ConstraintViolation<T>> validateProperty(T object, String propertyName) {
        return validator.validateProperty(object, propertyName);
    }

    /**
     * Validates a property value against a class
     */
    public <T> Set<ConstraintViolation<T>> validateValue(Class<T> beanType, String propertyName, Object value) {
        return validator.validateValue(beanType, propertyName, value);
    }

    /**
     * Logs validation errors for debugging
     */
    public <T> void logValidationErrors(T object, Class<?>... groups) {
        Map<String, String> errors = getValidationErrors(object, groups);
        if (!errors.isEmpty()) {
            log.warn("Validation errors for {}: {}", object.getClass().getSimpleName(), errors);
        }
    }
}


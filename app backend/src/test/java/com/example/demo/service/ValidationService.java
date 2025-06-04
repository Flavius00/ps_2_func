package com.example.demo.service;

import com.example.demo.exception.ValidationException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ValidationService {

    private final Validator validator;

    public ValidationService(Validator validator) {
        this.validator = validator;
    }

    public <T> Map<String, String> validateObject(T object, Class<?>... groups) {
        Set<ConstraintViolation<T>> violations = validator.validate(object, groups);

        return violations.stream()
                .collect(Collectors.toMap(
                        violation -> violation.getPropertyPath().toString(),
                        ConstraintViolation::getMessage,
                        (existing, replacement) -> existing + "; " + replacement
                ));
    }

    public <T> boolean isValid(T object, Class<?>... groups) {
        return validator.validate(object, groups).isEmpty();
    }

    public <T> void validateAndThrow(T object, Class<?>... groups) {
        Map<String, String> errors = validateObject(object, groups);
        if (!errors.isEmpty()) {
            throw new ValidationException("Validation failed", errors);
        }
    }
}

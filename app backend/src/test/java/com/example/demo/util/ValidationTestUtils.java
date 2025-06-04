package com.example.demo.util;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.Set;

public class ValidationTestUtils {

    private static final Validator validator;

    static {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    public static <T> Set<ConstraintViolation<T>> validate(T object, Class<?>... groups) {
        return validator.validate(object, groups);
    }

    public static <T> boolean isValid(T object, Class<?>... groups) {
        return validate(object, groups).isEmpty();
    }

    public static <T> String getFirstViolationMessage(T object, Class<?>... groups) {
        Set<ConstraintViolation<T>> violations = validate(object, groups);
        return violations.isEmpty() ? null : violations.iterator().next().getMessage();
    }

    public static <T> long getViolationCount(T object, Class<?>... groups) {
        return validate(object, groups).size();
    }
}
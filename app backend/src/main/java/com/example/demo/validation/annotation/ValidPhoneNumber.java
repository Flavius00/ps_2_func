package com.example.demo.validation.annotation;

import com.example.demo.validation.validator.PhoneNumberValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PhoneNumberValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPhoneNumber {
    String message() default "Phone number must be in valid Romanian format (e.g., 0123456789, +40123456789)";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
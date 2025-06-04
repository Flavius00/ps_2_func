package com.example.demo.validation.annotation;

import com.example.demo.validation.validator.TaxIdValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = TaxIdValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidTaxId {
    String message() default "Tax ID must be a valid Romanian format (e.g., RO12345678)";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
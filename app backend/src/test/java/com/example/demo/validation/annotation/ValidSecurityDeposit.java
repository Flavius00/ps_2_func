package com.example.demo.validation.annotation;

import com.example.demo.validation.validator.SecurityDepositValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = SecurityDepositValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidSecurityDeposit {
    String message() default "Security deposit must be between 1 and 6 months of rent";
    double minMonths() default 1.0;
    double maxMonths() default 6.0;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
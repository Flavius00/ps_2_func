package com.example.demo.validation.annotation;

import com.example.demo.validation.validator.RentalPriceValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = RentalPriceValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidRentalPrice {
    String message() default "Rental price must be between 100 and 50000 RON per month";
    double min() default 100.0;
    double max() default 50000.0;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
package com.example.demo.validation.annotation;

import com.example.demo.validation.validator.AreaRangeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AreaRangeValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidAreaRange {
    String message() default "Area must be between {min} and {max} square meters";
    double min() default 10.0;
    double max() default 10000.0;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
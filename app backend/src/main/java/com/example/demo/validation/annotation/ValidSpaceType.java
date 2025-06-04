package com.example.demo.validation.annotation;

import com.example.demo.validation.validator.SpaceTypeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = SpaceTypeValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidSpaceType {
    String message() default "Space type must be one of: OFFICE, RETAIL, WAREHOUSE";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
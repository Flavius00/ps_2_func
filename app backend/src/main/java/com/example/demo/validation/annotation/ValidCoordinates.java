package com.example.demo.validation.annotation;

import com.example.demo.validation.validator.CoordinatesValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CoordinatesValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidCoordinates {
    String message() default "Coordinates must be valid for Romania (latitude: 43.5-48.5, longitude: 20.0-30.0)";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
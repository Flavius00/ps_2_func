package com.example.demo.validation.annotation;

import com.example.demo.validation.validator.BusinessTypeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = BusinessTypeValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidBusinessType {
    String message() default "Business type must be one of the allowed categories";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
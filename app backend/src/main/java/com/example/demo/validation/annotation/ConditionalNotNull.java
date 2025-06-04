package com.example.demo.validation.annotation;

import com.example.demo.validation.validator.ConditionalNotNullValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ConditionalNotNullValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(ConditionalNotNull.List.class)
public @interface ConditionalNotNull {
    String message() default "Field {field} is required when {condition} is {value}";
    String field();
    String condition();
    String value();
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
        ConditionalNotNull[] value();
    }
}

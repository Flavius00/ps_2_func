package com.example.demo.validation.validator;

import com.example.demo.validation.annotation.ValidAreaRange;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class AreaRangeValidatorTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    static class AreaTestObject {
        @ValidAreaRange(min = 10.0, max = 1000.0)
        private Double area;

        public AreaTestObject(Double area) {
            this.area = area;
        }
    }

    @Test
    void shouldAcceptValidArea() {
        AreaTestObject testObject = new AreaTestObject(100.0);
        Set<ConstraintViolation<AreaTestObject>> violations = validator.validate(testObject);
        assertThat(violations).isEmpty();
    }

    @Test
    void shouldAcceptMinimumArea() {
        AreaTestObject testObject = new AreaTestObject(10.0);
        Set<ConstraintViolation<AreaTestObject>> violations = validator.validate(testObject);
        assertThat(violations).isEmpty();
    }

    @Test
    void shouldAcceptMaximumArea() {
        AreaTestObject testObject = new AreaTestObject(1000.0);
        Set<ConstraintViolation<AreaTestObject>> violations = validator.validate(testObject);
        assertThat(violations).isEmpty();
    }

    @Test
    void shouldRejectAreaBelowMinimum() {
        AreaTestObject testObject = new AreaTestObject(5.0);
        Set<ConstraintViolation<AreaTestObject>> violations = validator.validate(testObject);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .contains("Area must be between 10.0 and 1000.0 square meters");
    }

    @Test
    void shouldRejectAreaAboveMaximum() {
        AreaTestObject testObject = new AreaTestObject(1500.0);
        Set<ConstraintViolation<AreaTestObject>> violations = validator.validate(testObject);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .contains("Area must be between 10.0 and 1000.0 square meters");
    }

    @Test
    void shouldAllowNullArea() {
        AreaTestObject testObject = new AreaTestObject(null);
        Set<ConstraintViolation<AreaTestObject>> violations = validator.validate(testObject);
        assertThat(violations).isEmpty();
    }
}

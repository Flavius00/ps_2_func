package com.example.demo.validation.validator;

import com.example.demo.validation.annotation.ConditionalNotNull;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ConditionalNotNullValidatorTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @ConditionalNotNull(field = "shopWindowSize", condition = "spaceType", value = "RETAIL")
    static class ConditionalTestObject {
        private String spaceType;
        private Double shopWindowSize;

        public ConditionalTestObject(String spaceType, Double shopWindowSize) {
            this.spaceType = spaceType;
            this.shopWindowSize = shopWindowSize;
        }
    }

    @Test
    void shouldPassWhenConditionMetAndFieldNotNull() {
        ConditionalTestObject testObject = new ConditionalTestObject("RETAIL", 10.0);
        Set<ConstraintViolation<ConditionalTestObject>> violations = validator.validate(testObject);
        assertThat(violations).isEmpty();
    }

    @Test
    void shouldFailWhenConditionMetAndFieldNull() {
        ConditionalTestObject testObject = new ConditionalTestObject("RETAIL", null);
        Set<ConstraintViolation<ConditionalTestObject>> violations = validator.validate(testObject);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .contains("Field shopWindowSize is required when spaceType is RETAIL");
    }

    @Test
    void shouldPassWhenConditionNotMet() {
        ConditionalTestObject testObject = new ConditionalTestObject("OFFICE", null);
        Set<ConstraintViolation<ConditionalTestObject>> violations = validator.validate(testObject);
        assertThat(violations).isEmpty();
    }

    @Test
    void shouldPassWhenConditionNotMetEvenWithNonNullField() {
        ConditionalTestObject testObject = new ConditionalTestObject("OFFICE", 10.0);
        Set<ConstraintViolation<ConditionalTestObject>> violations = validator.validate(testObject);
        assertThat(violations).isEmpty();
    }
}
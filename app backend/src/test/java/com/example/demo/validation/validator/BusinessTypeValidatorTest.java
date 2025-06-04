package com.example.demo.validation.validator;

import com.example.demo.validation.annotation.ValidBusinessType;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class BusinessTypeValidatorTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    static class BusinessTypeTestObject {
        @ValidBusinessType
        private String businessType;

        public BusinessTypeTestObject(String businessType) {
            this.businessType = businessType;
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "Retail - Clothing",
            "Retail - Food",
            "Software Development",
            "Consulting",
            "Healthcare",
            "Education",
            "Other"
    })
    void shouldAcceptValidBusinessTypes(String validType) {
        BusinessTypeTestObject testObject = new BusinessTypeTestObject(validType);
        Set<ConstraintViolation<BusinessTypeTestObject>> violations = validator.validate(testObject);
        assertThat(violations).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "Invalid Business",
            "Random Type",
            "Retail Clothing",  // Missing dash
            "software development"  // Wrong case
    })
    void shouldRejectInvalidBusinessTypes(String invalidType) {
        BusinessTypeTestObject testObject = new BusinessTypeTestObject(invalidType);
        Set<ConstraintViolation<BusinessTypeTestObject>> violations = validator.validate(testObject);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .contains("Business type must be one of the allowed categories");
    }

    @Test
    void shouldAllowNullBusinessType() {
        BusinessTypeTestObject testObject = new BusinessTypeTestObject(null);
        Set<ConstraintViolation<BusinessTypeTestObject>> violations = validator.validate(testObject);
        assertThat(violations).isEmpty();
    }
}

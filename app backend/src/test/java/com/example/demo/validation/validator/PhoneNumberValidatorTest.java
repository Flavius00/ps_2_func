package com.example.demo.validation.validator;

import com.example.demo.validation.annotation.ValidPhoneNumber;
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

class PhoneNumberValidatorTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    static class PhoneTestObject {
        @ValidPhoneNumber
        private String phone;

        public PhoneTestObject(String phone) {
            this.phone = phone;
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "RO12345678",
            "RO1234567890",
            "RO12",
            "ro12345678"  // Should be converted to uppercase
    })
    void shouldAcceptValidTaxIds(String validTaxId) {
        TaxIdValidatorTest.TaxIdTestObject testObject = new TaxIdValidatorTest.TaxIdTestObject(validTaxId);
        Set<ConstraintViolation<TaxIdValidatorTest.TaxIdTestObject>> violations = validator.validate(testObject);
        assertThat(violations).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "12345678",        // Missing RO prefix
            "RO1",             // Too short
            "RO12345678901",   // Too long
            "EN12345678",      // Wrong country code
            "RO1234567A",      // Contains letters in number part
            "RO 12345678",     // Contains space
            "RO-12345678"      // Contains dash
    })
    void shouldRejectInvalidTaxIds(String invalidTaxId) {
        TaxIdValidatorTest.TaxIdTestObject testObject = new TaxIdValidatorTest.TaxIdTestObject(invalidTaxId);
        Set<ConstraintViolation<TaxIdValidatorTest.TaxIdTestObject>> violations = validator.validate(testObject);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .contains("Tax ID must be a valid Romanian format");
    }

    @Test
    void shouldAllowNullTaxId() {
        TaxIdValidatorTest.TaxIdTestObject testObject = new TaxIdValidatorTest.TaxIdTestObject(null);
        Set<ConstraintViolation<TaxIdValidatorTest.TaxIdTestObject>> violations = validator.validate(testObject);
        assertThat(violations).isEmpty();
    }

    @Test
    void shouldAllowEmptyTaxId() {
        TaxIdValidatorTest.TaxIdTestObject testObject = new TaxIdValidatorTest.TaxIdTestObject("");
        Set<ConstraintViolation<TaxIdValidatorTest.TaxIdTestObject>> violations = validator.validate(testObject);
        assertThat(violations).isEmpty();
    }
}

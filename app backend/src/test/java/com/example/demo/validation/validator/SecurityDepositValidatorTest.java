package com.example.demo.validation.validator;

import com.example.demo.validation.annotation.ValidSecurityDeposit;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityDepositValidatorTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @ValidSecurityDeposit(minMonths = 1.0, maxMonths = 6.0)
    static class SecurityDepositTestObject {
        private Double monthlyRent;
        private Double securityDeposit;

        public SecurityDepositTestObject(Double monthlyRent, Double securityDeposit) {
            this.monthlyRent = monthlyRent;
            this.securityDeposit = securityDeposit;
        }
    }

    @Test
    void shouldAcceptValidSecurityDeposit() {
        // 2 months deposit for 1000 RON rent
        SecurityDepositTestObject testObject = new SecurityDepositTestObject(1000.0, 2000.0);
        Set<ConstraintViolation<SecurityDepositTestObject>> violations = validator.validate(testObject);
        assertThat(violations).isEmpty();
    }

    @Test
    void shouldAcceptMinimumDeposit() {
        // 1 month deposit
        SecurityDepositTestObject testObject = new SecurityDepositTestObject(1000.0, 1000.0);
        Set<ConstraintViolation<SecurityDepositTestObject>> violations = validator.validate(testObject);
        assertThat(violations).isEmpty();
    }

    @Test
    void shouldAcceptMaximumDeposit() {
        // 6 months deposit
        SecurityDepositTestObject testObject = new SecurityDepositTestObject(1000.0, 6000.0);
        Set<ConstraintViolation<SecurityDepositTestObject>> violations = validator.validate(testObject);
        assertThat(violations).isEmpty();
    }

    @Test
    void shouldRejectDepositBelowMinimum() {
        // 0.5 months deposit (below minimum)
        SecurityDepositTestObject testObject = new SecurityDepositTestObject(1000.0, 500.0);
        Set<ConstraintViolation<SecurityDepositTestObject>> violations = validator.validate(testObject);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .contains("Security deposit must be between 1.0 and 6.0 months of rent");
    }

    @Test
    void shouldRejectDepositAboveMaximum() {
        // 7 months deposit (above maximum)
        SecurityDepositTestObject testObject = new SecurityDepositTestObject(1000.0, 7000.0);
        Set<ConstraintViolation<SecurityDepositTestObject>> violations = validator.validate(testObject);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .contains("Security deposit must be between 1.0 and 6.0 months of rent");
    }

    @Test
    void shouldAllowNullValues() {
        SecurityDepositTestObject testObject = new SecurityDepositTestObject(null, null);
        Set<ConstraintViolation<SecurityDepositTestObject>> violations = validator.validate(testObject);
        assertThat(violations).isEmpty();
    }

    @Test
    void shouldAllowOneNullValue() {
        SecurityDepositTestObject testObject1 = new SecurityDepositTestObject(1000.0, null);
        SecurityDepositTestObject testObject2 = new SecurityDepositTestObject(null, 2000.0);

        Set<ConstraintViolation<SecurityDepositTestObject>> violations1 = validator.validate(testObject1);
        Set<ConstraintViolation<SecurityDepositTestObject>> violations2 = validator.validate(testObject2);

        assertThat(violations1).isEmpty();
        assertThat(violations2).isEmpty();
    }
}

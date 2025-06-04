package com.example.demo.validation.validator;

import com.example.demo.validation.annotation.ValidCoordinates;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CoordinatesValidatorTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @ValidCoordinates
    static class CoordinatesTestObject {
        private Double latitude;
        private Double longitude;

        public CoordinatesTestObject(Double latitude, Double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }

    @Test
    void shouldAcceptValidRomanianCoordinates() {
        // Cluj-Napoca coordinates
        CoordinatesTestObject testObject = new CoordinatesTestObject(46.7712, 23.6236);
        Set<ConstraintViolation<CoordinatesTestObject>> violations = validator.validate(testObject);
        assertThat(violations).isEmpty();
    }

    @Test
    void shouldAcceptBoundaryCoordinates() {
        // Test minimum boundaries
        CoordinatesTestObject testObject1 = new CoordinatesTestObject(43.5, 20.0);
        Set<ConstraintViolation<CoordinatesTestObject>> violations1 = validator.validate(testObject1);
        assertThat(violations1).isEmpty();

        // Test maximum boundaries
        CoordinatesTestObject testObject2 = new CoordinatesTestObject(48.5, 30.0);
        Set<ConstraintViolation<CoordinatesTestObject>> violations2 = validator.validate(testObject2);
        assertThat(violations2).isEmpty();
    }

    @Test
    void shouldRejectCoordinatesOutsideRomania() {
        // Paris coordinates (outside Romania)
        CoordinatesTestObject testObject = new CoordinatesTestObject(48.8566, 2.3522);
        Set<ConstraintViolation<CoordinatesTestObject>> violations = validator.validate(testObject);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .contains("Coordinates must be valid for Romania");
    }

    @Test
    void shouldRejectLatitudeTooLow() {
        CoordinatesTestObject testObject = new CoordinatesTestObject(40.0, 25.0);
        Set<ConstraintViolation<CoordinatesTestObject>> violations = validator.validate(testObject);
        assertThat(violations).hasSize(1);
    }

    @Test
    void shouldRejectLatitudeTooHigh() {
        CoordinatesTestObject testObject = new CoordinatesTestObject(50.0, 25.0);
        Set<ConstraintViolation<CoordinatesTestObject>> violations = validator.validate(testObject);
        assertThat(violations).hasSize(1);
    }

    @Test
    void shouldRejectLongitudeTooLow() {
        CoordinatesTestObject testObject = new CoordinatesTestObject(46.0, 15.0);
        Set<ConstraintViolation<CoordinatesTestObject>> violations = validator.validate(testObject);
        assertThat(violations).hasSize(1);
    }

    @Test
    void shouldRejectLongitudeTooHigh() {
        CoordinatesTestObject testObject = new CoordinatesTestObject(46.0, 35.0);
        Set<ConstraintViolation<CoordinatesTestObject>> violations = validator.validate(testObject);
        assertThat(violations).hasSize(1);
    }

    @Test
    void shouldAllowNullCoordinates() {
        CoordinatesTestObject testObject = new CoordinatesTestObject(null, null);
        Set<ConstraintViolation<CoordinatesTestObject>> violations = validator.validate(testObject);
        assertThat(violations).isEmpty();
    }
}

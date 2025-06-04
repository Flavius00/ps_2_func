package com.example.demo.validation.validator;

import com.example.demo.validation.annotation.ValidSpaceType;
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

class SpaceTypeValidatorTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    static class SpaceTypeTestObject {
        @ValidSpaceType
        private String spaceType;

        public SpaceTypeTestObject(String spaceType) {
            this.spaceType = spaceType;
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"OFFICE", "RETAIL", "WAREHOUSE", "office", "retail", "warehouse"})
    void shouldAcceptValidSpaceTypes(String validType) {
        SpaceTypeTestObject testObject = new SpaceTypeTestObject(validType);
        Set<ConstraintViolation<SpaceTypeTestObject>> violations = validator.validate(testObject);
        assertThat(violations).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"INVALID", "SHOP", "STORE", "FACTORY", "random"})
    void shouldRejectInvalidSpaceTypes(String invalidType) {
        SpaceTypeTestObject testObject = new SpaceTypeTestObject(invalidType);
        Set<ConstraintViolation<SpaceTypeTestObject>> violations = validator.validate(testObject);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .contains("Space type must be one of: OFFICE, RETAIL, WAREHOUSE");
    }

    @Test
    void shouldAllowNullSpaceType() {
        SpaceTypeTestObject testObject = new SpaceTypeTestObject(null);
        Set<ConstraintViolation<SpaceTypeTestObject>> violations = validator.validate(testObject);
        assertThat(violations).isEmpty();
    }
}

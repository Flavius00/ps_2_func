package com.example.demo.validation.validator;

import com.example.demo.dto.ComercialSpaceCreateDto;
import com.example.demo.validation.groups.ValidationGroups.CreateValidation;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class ComercialSpaceCreateDtoValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldValidateCompleteValidDto() {
        ComercialSpaceCreateDto dto = ComercialSpaceCreateDto.builder()
                .name("Test Office Space")
                .description("A modern office space")
                .area(150.0)
                .pricePerMonth(2000.0)
                .address("Strada Test 123, Cluj-Napoca")
                .latitude(46.7712)
                .longitude(23.6236)
                .amenities(Arrays.asList("Air Conditioning", "Internet", "Parking"))
                .available(true)
                .spaceType("OFFICE")
                .ownerId(1L)
                .buildingId(1L)
                .numberOfRooms(4)
                .hasReception(true)
                .build();

        Set<ConstraintViolation<ComercialSpaceCreateDto>> violations =
                validator.validate(dto, CreateValidation.class);

        assertThat(violations).isEmpty();
    }

    @Test
    void shouldFailValidationForInvalidDto() {
        ComercialSpaceCreateDto dto = ComercialSpaceCreateDto.builder()
                .name("") // Invalid: empty name
                .area(-10.0) // Invalid: negative area
                .pricePerMonth(50.0) // Invalid: below minimum price
                .latitude(60.0) // Invalid: outside Romania
                .longitude(10.0) // Invalid: outside Romania
                .spaceType("INVALID_TYPE") // Invalid: not a valid space type
                .ownerId(-1L) // Invalid: negative ID
                .buildingId(null) // Invalid: null building ID
                .build();

        Set<ConstraintViolation<ComercialSpaceCreateDto>> violations =
                validator.validate(dto, CreateValidation.class);

        assertThat(violations).hasSizeGreaterThan(5);

        // Check specific validation messages
        boolean hasNameError = violations.stream()
                .anyMatch(v -> v.getMessage().contains("Space name is required"));
        assertThat(hasNameError).isTrue();

        boolean hasAreaError = violations.stream()
                .anyMatch(v -> v.getMessage().contains("Area must be between"));
        assertThat(hasAreaError).isTrue();

        boolean hasPriceError = violations.stream()
                .anyMatch(v -> v.getMessage().contains("Rental price must be between"));
        assertThat(hasPriceError).isTrue();
    }

    @Test
    void shouldValidateConditionalFields() {
        // Test RETAIL space without shop window size (should fail)
        ComercialSpaceCreateDto retailDto = ComercialSpaceCreateDto.builder()
                .name("Test Retail Space")
                .area(100.0)
                .pricePerMonth(3000.0)
                .address("Strada Test 123, Cluj-Napoca")
                .latitude(46.7712)
                .longitude(23.6236)
                .spaceType("RETAIL")
                .ownerId(1L)
                .buildingId(1L)
                // Missing shopWindowSize for RETAIL space
                .build();

        Set<ConstraintViolation<ComercialSpaceCreateDto>> violations =
                validator.validate(retailDto);

        boolean hasConditionalError = violations.stream()
                .anyMatch(v -> v.getMessage().contains("Shop window size is required for retail spaces"));
        assertThat(hasConditionalError).isTrue();
    }

    @Test
    void shouldPassConditionalValidationWhenFieldProvided() {
        // Test RETAIL space with shop window size (should pass)
        ComercialSpaceCreateDto retailDto = ComercialSpaceCreateDto.builder()
                .name("Test Retail Space")
                .area(100.0)
                .pricePerMonth(3000.0)
                .address("Strada Test 123, Cluj-Napoca")
                .latitude(46.7712)
                .longitude(23.6236)
                .spaceType("RETAIL")
                .ownerId(1L)
                .buildingId(1L)
                .shopWindowSize(15.0) // Provided for RETAIL space
                .hasCustomerEntrance(true)
                .maxOccupancy(50)
                .build();

        Set<ConstraintViolation<ComercialSpaceCreateDto>> violations =
                validator.validate(retailDto);

        // Should have no conditional validation errors
        boolean hasConditionalError = violations.stream()
                .anyMatch(v -> v.getMessage().contains("Shop window size is required"));
        assertThat(hasConditionalError).isFalse();
    }
}
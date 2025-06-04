package com.example.demo.validation.validator;

import com.example.demo.validation.annotation.ValidContractDates;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ValidContractDatesValidatorTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @ValidContractDates
    static class ContractDatesTestObject {
        private LocalDate startDate;
        private LocalDate endDate;

        public ContractDatesTestObject(LocalDate startDate, LocalDate endDate) {
            this.startDate = startDate;
            this.endDate = endDate;
        }
    }

    @Test
    void shouldAcceptValidDateRange() {
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusMonths(12);
        ContractDatesTestObject testObject = new ContractDatesTestObject(start, end);

        Set<ConstraintViolation<ContractDatesTestObject>> violations = validator.validate(testObject);
        assertThat(violations).isEmpty();
    }

    @Test
    void shouldRejectEndDateBeforeStartDate() {
        LocalDate start = LocalDate.now();
        LocalDate end = start.minusDays(1);
        ContractDatesTestObject testObject = new ContractDatesTestObject(start, end);

        Set<ConstraintViolation<ContractDatesTestObject>> violations = validator.validate(testObject);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .contains("End date must be after start date");
    }

    @Test
    void shouldRejectSameStartAndEndDate() {
        LocalDate date = LocalDate.now();
        ContractDatesTestObject testObject = new ContractDatesTestObject(date, date);

        Set<ConstraintViolation<ContractDatesTestObject>> violations = validator.validate(testObject);
        assertThat(violations).hasSize(1);
    }

    @Test
    void shouldAllowNullDates() {
        ContractDatesTestObject testObject = new ContractDatesTestObject(null, null);
        Set<ConstraintViolation<ContractDatesTestObject>> violations = validator.validate(testObject);
        assertThat(violations).isEmpty();
    }

    @Test
    void shouldAllowOneNullDate() {
        LocalDate date = LocalDate.now();
        ContractDatesTestObject testObject1 = new ContractDatesTestObject(date, null);
        ContractDatesTestObject testObject2 = new ContractDatesTestObject(null, date);

        Set<ConstraintViolation<ContractDatesTestObject>> violations1 = validator.validate(testObject1);
        Set<ConstraintViolation<ContractDatesTestObject>> violations2 = validator.validate(testObject2);

        assertThat(violations1).isEmpty();
        assertThat(violations2).isEmpty();
    }
}
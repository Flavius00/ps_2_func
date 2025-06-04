package com.example.demo.validation.validator;

import com.example.demo.validation.annotation.ValidTaxId;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;

import static org.assertj.core.api.Assertions.assertThat;

class TaxIdValidatorTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    static class TaxIdTestObject {
        @ValidTaxId
        private String taxId;

        public TaxIdTestObject(String taxId) {
            this.taxId = taxId;
        }
    }
}
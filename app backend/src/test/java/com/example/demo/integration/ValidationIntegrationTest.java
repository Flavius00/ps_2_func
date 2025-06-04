package com.example.demo.integration;

import com.example.demo.dto.ComercialSpaceCreateDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
class ValidationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateSpaceWithValidData() throws Exception {
        ComercialSpaceCreateDto validDto = ComercialSpaceCreateDto.builder()
                .name("Test Office Space")
                .description("A modern office space for tech companies")
                .area(150.0)
                .pricePerMonth(2000.0)
                .address("Strada Memorandumului 28, Cluj-Napoca")
                .latitude(46.7712)
                .longitude(23.6236)
                .amenities(Arrays.asList("Air Conditioning", "High-Speed Internet", "24/7 Access"))
                .available(true)
                .spaceType("OFFICE")
                .ownerId(1L)
                .buildingId(1L)
                .numberOfRooms(4)
                .hasReception(true)
                .build();

        mockMvc.perform(post("/api/v2/spaces/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Test Office Space"))
                .andExpect(jsonPath("$.area").value(150.0))
                .andExpect(jsonPath("$.pricePerMonth").value(2000.0));
    }

    @Test
    void shouldRejectInvalidSpaceData() throws Exception {
        ComercialSpaceCreateDto invalidDto = ComercialSpaceCreateDto.builder()
                .name("") // Invalid: empty name
                .area(-10.0) // Invalid: negative area
                .pricePerMonth(50.0) // Invalid: below minimum
                .latitude(60.0) // Invalid: outside Romania
                .longitude(10.0) // Invalid: outside Romania
                .spaceType("INVALID") // Invalid: not a valid type
                .ownerId(-1L) // Invalid: negative ID
                .build();

        mockMvc.perform(post("/api/v2/spaces/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.validationErrors").isArray())
                .andExpect(jsonPath("$.validationErrors.length()").value(org.hamcrest.Matchers.greaterThan(5)));
    }

    @Test
    void shouldValidateConditionalFields() throws Exception {
        ComercialSpaceCreateDto retailWithoutWindow = ComercialSpaceCreateDto.builder()
                .name("Retail Space")
                .area(100.0)
                .pricePerMonth(3000.0)
                .address("Strada Comercială 15, Cluj-Napoca")
                .latitude(46.7712)
                .longitude(23.6236)
                .spaceType("RETAIL")
                .ownerId(1L)
                .buildingId(1L)
                // Missing shopWindowSize for RETAIL
                .build();

        mockMvc.perform(post("/api/v2/spaces/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(retailWithoutWindow)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors[?(@.field == 'shopWindowSize')]").exists());
    }

    @Test
    void shouldValidatePhoneNumbers() throws Exception {
        // Test invalid phone number in user creation
        String invalidUserJson = """
            {
                "name": "Test User",
                "email": "test@example.com",
                "username": "testuser",
                "phone": "123456789",
                "role": "TENANT"
            }
            """;

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidUserJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors[?(@.field == 'phone')]").exists());
    }

    @Test
    void shouldValidateTaxIds() throws Exception {
        // Test invalid tax ID in owner creation
        String invalidOwnerJson = """
            {
                "name": "Test Owner",
                "email": "owner@example.com",
                "username": "testowner",
                "role": "OWNER",
                "companyName": "Test Company SRL",
                "taxId": "INVALID123"
            }
            """;

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidOwnerJson))
                .andExpect(jsonPath("$.validationErrors[?(@.field == 'taxId')]").exists());
    }
}

package com.example.demo.service.impl;

import com.example.demo.dto.ComercialSpaceCreateDto;
import com.example.demo.dto.ComercialSpaceDto;
import com.example.demo.exception.ValidationException;
import com.example.demo.service.ValidationService;
import com.example.demo.validation.groups.ValidationGroups.CreateValidation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Slf4j
@Service
@Transactional
public class ValidatedComercialSpaceService {

    private final ValidationService validationService;
    // Alte dependințe...

    public ValidatedComercialSpaceService(ValidationService validationService) {
        this.validationService = validationService;
    }

    public ComercialSpaceDto createSpaceWithValidation(ComercialSpaceCreateDto createDto) {
        log.info("Creating commercial space with validation: {}", createDto.getName());

        // Validare manuală cu grupuri specifice
        Map<String, String> validationErrors = validationService.validateObject(createDto, CreateValidation.class);

        if (!validationErrors.isEmpty()) {
            log.warn("Validation failed for commercial space creation: {}", validationErrors);
            throw new ValidationException("Commercial space validation failed", validationErrors);
        }

        // Validări custom de business logic
        validateBusinessRules(createDto);

        // Procesare normală...
        log.info("Commercial space validation passed, proceeding with creation");

        // Aici ar fi logica de creare efectivă
        return ComercialSpaceDto.builder()
                .id(1L)
                .name(createDto.getName())
                .area(createDto.getArea())
                .pricePerMonth(createDto.getPricePerMonth())
                .available(true)
                .build();
    }

    private void validateBusinessRules(ComercialSpaceCreateDto createDto) {
        // Exemplu de validare de business logic
        if ("WAREHOUSE".equals(createDto.getSpaceType()) && createDto.getArea() < 100.0) {
            throw new ValidationException("Business rule violation",
                    Map.of("area", "Warehouse spaces must have at least 100 square meters"));
        }

        if ("RETAIL".equals(createDto.getSpaceType()) && createDto.getPricePerMonth() < 1000.0) {
            throw new ValidationException("Business rule violation",
                    Map.of("pricePerMonth", "Retail spaces must have minimum rent of 1000 RON"));
        }

        // Validare că prețul este rezonabil pentru zona (folosind coordonatele)
        if (isPremiumLocation(createDto.getLatitude(), createDto.getLongitude())
                && createDto.getPricePerMonth() < 1500.0) {
            throw new ValidationException("Business rule violation",
                    Map.of("pricePerMonth", "Premium location requires minimum rent of 1500 RON"));
        }
    }

    private boolean isPremiumLocation(Double latitude, Double longitude) {
        // Logică simplă pentru zone premium (centrul Cluj-Napoca)
        if (latitude == null || longitude == null) return false;

        // Coordonatele centrului Cluj-Napoca (aproximativ)
        double centerLat = 46.7712;
        double centerLng = 23.6236;
        double radius = 0.01; // Aproximativ 1km

        double distance = Math.sqrt(Math.pow(latitude - centerLat, 2) + Math.pow(longitude - centerLng, 2));
        return distance <= radius;
    }
}
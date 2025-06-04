package com.example.demo.controller;

import com.example.demo.dto.ComercialSpaceCreateDto;
import com.example.demo.dto.ComercialSpaceDto;
import com.example.demo.dto.ComercialSpaceUpdateDto;
import com.example.demo.exception.ValidationException;
import com.example.demo.service.impl.ValidatedComercialSpaceService;
import com.example.demo.validation.groups.ValidationGroups.CreateValidation;
import com.example.demo.validation.groups.ValidationGroups.UpdateValidation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v2/spaces")
@CrossOrigin(origins = "http://localhost:3000")
@Validated
public class ValidatedComercialSpaceController {

    private final ValidatedComercialSpaceService spaceService;

    public ValidatedComercialSpaceController(ValidatedComercialSpaceService spaceService) {
        this.spaceService = spaceService;
    }

    @PostMapping("/create")
    public ResponseEntity<ComercialSpaceDto> createSpace(
            @Validated(CreateValidation.class) @RequestBody ComercialSpaceCreateDto createDto) {

        log.info("REST API: Creating commercial space with validation");

        ComercialSpaceDto createdSpace = spaceService.createSpaceWithValidation(createDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdSpace);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ComercialSpaceDto> updateSpace(
            @PathVariable Long id,
            @Validated(UpdateValidation.class) @RequestBody ComercialSpaceUpdateDto updateDto) {

        log.info("REST API: Updating commercial space {} with validation", id);

        // Setează ID-ul din path
        updateDto.setId(id);

        // Procesare update...

        return ResponseEntity.ok().build();
    }

    // Endpoint pentru validare manuală (util pentru frontend)
    @PostMapping("/validate")
    public ResponseEntity<?> validateSpaceData(
            @RequestBody ComercialSpaceCreateDto createDto) {

        try {
            spaceService.createSpaceWithValidation(createDto);
            return ResponseEntity.ok().body(Map.of("valid", true, "message", "Data is valid"));
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "valid", false,
                    "errors", e.getValidationErrors(),
                    "message", "Validation failed"
            ));
        }
    }
}

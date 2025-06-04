package com.example.demo.controller;


import com.example.demo.dto.ComercialSpaceCreateDto;
import com.example.demo.dto.ComercialSpaceDto;
import com.example.demo.validation.groups.ValidationGroups;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/spaces")
@CrossOrigin(origins = "http://localhost:3000")
@Validated
public class ComercialSpaceValidationController {

    // Example of using validation groups in controller
    @PostMapping("/create-validated")
    public ResponseEntity<ComercialSpaceDto> createSpaceWithValidation(
            @Validated(ValidationGroups.CreateValidation.class) @RequestBody ComercialSpaceCreateDto createDto) {

        log.info("Creating commercial space with enhanced validation: {}", createDto.getName());

        // Service logic here...
        // The validation will automatically be applied based on the CreateValidation group

        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/update-validated")
    public ResponseEntity<ComercialSpaceDto> updateSpaceWithValidation(
            @PathVariable Long id,
            @Validated(ValidationGroups.UpdateValidation.class) @RequestBody ComercialSpaceDto updateDto) {

        log.info("Updating commercial space with enhanced validation: {}", id);

        // Service logic here...

        return ResponseEntity.ok().build();
    }
}


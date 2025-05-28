package com.example.demo.controller;

import com.example.demo.dto.ComercialSpaceDto;
import com.example.demo.dto.ComercialSpaceCreateDto;
import com.example.demo.dto.ComercialSpaceUpdateDto;
import com.example.demo.exception.InsufficientPermissionsException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.ComercialSpaceMapper;
import com.example.demo.model.ComercialSpace;
import com.example.demo.service.ComercialSpaceService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/spaces")
@CrossOrigin(origins = "http://localhost:3000")
public class ComercialSpaceController {
    private final ComercialSpaceService spaceService;
    private final ComercialSpaceMapper spaceMapper;

    public ComercialSpaceController(ComercialSpaceService spaceService,
                                    ComercialSpaceMapper spaceMapper) {
        this.spaceService = spaceService;
        this.spaceMapper = spaceMapper;
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<ComercialSpaceDto>> getAllSpaces() {
        log.info("Fetching all commercial spaces");

        List<ComercialSpace> spaces = spaceService.getAllSpaces();
        List<ComercialSpaceDto> spaceDtos = spaces.stream()
                .map(spaceMapper::toDto)
                .collect(Collectors.toList());

        log.info("Successfully retrieved {} commercial spaces", spaceDtos.size());
        return ResponseEntity.ok(spaceDtos);
    }

    @GetMapping("/details/{id}")
    public ResponseEntity<ComercialSpaceDto> spaceDetails(@PathVariable Long id) {
        log.info("Fetching details for space with ID: {}", id);

        ComercialSpace space = spaceService.getSpaceById(id);
        ComercialSpaceDto spaceDto = spaceMapper.toDto(space);

        log.info("Successfully retrieved details for space: {}", space.getName());
        return ResponseEntity.ok(spaceDto);
    }

    @PostMapping("/update")
    public ResponseEntity<ComercialSpaceDto> updateSpace(@Valid @RequestBody ComercialSpaceUpdateDto updateDto) {
        log.info("Updating space with ID: {}", updateDto.getId());

        // Get existing space to preserve relationships
        ComercialSpace existingSpace = spaceService.getSpaceById(updateDto.getId());

        // Map update DTO to entity, preserving owner and building
        ComercialSpace updatedSpace = spaceMapper.updateFromDto(updateDto, existingSpace);

        ComercialSpace savedSpace = spaceService.updateSpace(updatedSpace);
        ComercialSpaceDto responseDto = spaceMapper.toDto(savedSpace);

        log.info("Successfully updated space: {}", savedSpace.getName());
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/create")
    public ResponseEntity<ComercialSpaceDto> createSpace(@Valid @RequestBody ComercialSpaceCreateDto createDto) {
        log.info("Creating new commercial space: {}", createDto.getName());

        // Convert DTO to entity using mapper
        ComercialSpace space = spaceMapper.toEntity(createDto);

        ComercialSpace createdSpace = spaceService.addSpace(space);
        ComercialSpaceDto responseDto = spaceMapper.toDto(createdSpace);

        log.info("Successfully created space with ID: {}", createdSpace.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping("/available")
    public ResponseEntity<List<ComercialSpaceDto>> getAvailableSpaces() {
        log.info("Fetching available commercial spaces");

        List<ComercialSpace> spaces = spaceService.getAvailableSpaces();
        List<ComercialSpaceDto> spaceDtos = spaces.stream()
                .map(spaceMapper::toDto)
                .collect(Collectors.toList());

        log.info("Successfully retrieved {} available spaces", spaceDtos.size());
        return ResponseEntity.ok(spaceDtos);
    }

    @GetMapping("/type/{spaceType}")
    public ResponseEntity<List<ComercialSpaceDto>> getSpacesByType(@PathVariable String spaceType) {
        log.info("Fetching spaces by type: {}", spaceType);

        List<ComercialSpace> spaces = spaceService.getSpacesByType(spaceType);
        List<ComercialSpaceDto> spaceDtos = spaces.stream()
                .map(spaceMapper::toDto)
                .collect(Collectors.toList());

        log.info("Successfully retrieved {} spaces of type {}", spaceDtos.size(), spaceType);
        return ResponseEntity.ok(spaceDtos);
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<ComercialSpaceDto>> getSpacesByOwner(@PathVariable Long ownerId) {
        log.info("Fetching spaces for owner ID: {}", ownerId);

        List<ComercialSpace> spaces = spaceService.getSpacesByOwner(ownerId);
        List<ComercialSpaceDto> spaceDtos = spaces.stream()
                .map(spaceMapper::toDto)
                .collect(Collectors.toList());

        log.info("Successfully retrieved {} spaces for owner {}", spaceDtos.size(), ownerId);
        return ResponseEntity.ok(spaceDtos);
    }

    @GetMapping("/building/{buildingId}")
    public ResponseEntity<List<ComercialSpaceDto>> getSpacesByBuilding(@PathVariable Long buildingId) {
        log.info("Fetching spaces for building ID: {}", buildingId);

        List<ComercialSpace> spaces = spaceService.getSpacesByBuilding(buildingId);
        List<ComercialSpaceDto> spaceDtos = spaces.stream()
                .map(spaceMapper::toDto)
                .collect(Collectors.toList());

        log.info("Successfully retrieved {} spaces for building {}", spaceDtos.size(), buildingId);
        return ResponseEntity.ok(spaceDtos);
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<Void> deleteSpace(@PathVariable Long id) {
        log.info("Deleting space with ID: {}", id);

        spaceService.deleteSpace(id);

        log.info("Successfully deleted space with ID: {}", id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<ComercialSpaceDto>> searchSpaces(@RequestParam String keyword) {
        log.info("Searching spaces with keyword: {}", keyword);

        List<ComercialSpace> spaces = spaceService.searchSpaces(keyword);
        List<ComercialSpaceDto> spaceDtos = spaces.stream()
                .map(spaceMapper::toDto)
                .collect(Collectors.toList());

        log.info("Found {} spaces matching keyword: {}", spaceDtos.size(), keyword);
        return ResponseEntity.ok(spaceDtos);
    }

    @GetMapping("/price-range")
    public ResponseEntity<List<ComercialSpaceDto>> getSpacesByPriceRange(
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice) {

        log.info("Fetching spaces by price range: {} - {}", minPrice, maxPrice);

        List<ComercialSpace> spaces = spaceService.getSpacesByPriceRange(minPrice, maxPrice);
        List<ComercialSpaceDto> spaceDtos = spaces.stream()
                .map(spaceMapper::toDto)
                .collect(Collectors.toList());

        log.info("Found {} spaces in price range {} - {}", spaceDtos.size(), minPrice, maxPrice);
        return ResponseEntity.ok(spaceDtos);
    }

    @GetMapping("/area-range")
    public ResponseEntity<List<ComercialSpaceDto>> getSpacesByAreaRange(
            @RequestParam(required = false) Double minArea,
            @RequestParam(required = false) Double maxArea) {

        log.info("Fetching spaces by area range: {} - {}", minArea, maxArea);

        List<ComercialSpace> spaces = spaceService.getSpacesByAreaRange(minArea, maxArea);
        List<ComercialSpaceDto> spaceDtos = spaces.stream()
                .map(spaceMapper::toDto)
                .collect(Collectors.toList());

        log.info("Found {} spaces in area range {} - {}", spaceDtos.size(), minArea, maxArea);
        return ResponseEntity.ok(spaceDtos);
    }

    @GetMapping("/available/type/{spaceType}")
    public ResponseEntity<List<ComercialSpaceDto>> getAvailableSpacesByType(@PathVariable String spaceType) {
        log.info("Fetching available spaces by type: {}", spaceType);

        List<ComercialSpace> spaces = spaceService.getAvailableSpacesByType(spaceType);
        List<ComercialSpaceDto> spaceDtos = spaces.stream()
                .map(spaceMapper::toDto)
                .collect(Collectors.toList());

        log.info("Found {} available spaces of type {}", spaceDtos.size(), spaceType);
        return ResponseEntity.ok(spaceDtos);
    }

    @GetMapping("/available/count")
    public ResponseEntity<Long> getAvailableSpacesCount() {
        log.info("Fetching count of available spaces");

        long count = spaceService.getAvailableSpacesCount();

        log.info("Total available spaces count: {}", count);
        return ResponseEntity.ok(count);
    }
}

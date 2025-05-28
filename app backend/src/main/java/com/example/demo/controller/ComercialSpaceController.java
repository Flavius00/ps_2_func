package com.example.demo.controller;

import com.example.demo.dto.ComercialSpaceDto;
import com.example.demo.dto.ComercialSpaceCreateDto;
import com.example.demo.dto.ComercialSpaceUpdateDto;
import com.example.demo.mapper.ComercialSpaceMapper;
import com.example.demo.model.ComercialSpace;
import com.example.demo.service.ComercialSpaceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        try {
            List<ComercialSpace> spaces = spaceService.getAllSpaces();
            if (spaces == null) {
                spaces = List.of();
            }

            System.out.println("=== DEBUGGING SPACES WITH MAPPER ===");
            System.out.println("Converting " + spaces.size() + " spaces to DTOs");

            // Convert entities to DTOs using mapper
            List<ComercialSpaceDto> spaceDtos = spaces.stream()
                    .map(spaceMapper::toDto)
                    .collect(Collectors.toList());

            for (ComercialSpaceDto spaceDto : spaceDtos) {
                System.out.println("Space DTO ID: " + spaceDto.getId() +
                        ", Name: " + spaceDto.getName() +
                        ", Owner ID: " + spaceDto.getOwnerId() +
                        ", Owner Name: " + spaceDto.getOwnerName() +
                        ", Building ID: " + spaceDto.getBuildingId() +
                        ", Building Name: " + spaceDto.getBuildingName());
            }
            System.out.println("=== END DEBUGGING ===");

            return ResponseEntity.ok(spaceDtos);
        } catch (Exception e) {
            System.err.println("Error in getAllSpaces: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok(List.of());
        }
    }

    @GetMapping("/details/{id}")
    public ResponseEntity<ComercialSpaceDto> spaceDetails(@PathVariable Long id) {
        try {
            ComercialSpace space = spaceService.getSpaceById(id);
            if (space == null) {
                return ResponseEntity.notFound().build();
            }

            ComercialSpaceDto spaceDto = spaceMapper.toDto(space);
            return ResponseEntity.ok(spaceDto);
        } catch (Exception e) {
            System.err.println("Error getting space details: " + e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/update")
    public ResponseEntity<ComercialSpaceDto> updateSpace(@RequestBody ComercialSpaceUpdateDto updateDto) {
        try {
            System.out.println("Received update DTO: " + updateDto);

            // Get existing space to preserve relationships
            ComercialSpace existingSpace = spaceService.getSpaceById(updateDto.getId());
            if (existingSpace == null) {
                return ResponseEntity.notFound().build();
            }

            System.out.println("Existing space - Owner ID: " + existingSpace.getOwnerId() +
                    ", Building ID: " + existingSpace.getBuildingId());

            // Map update DTO to entity, preserving owner and building
            ComercialSpace updatedSpace = spaceMapper.updateFromDto(updateDto, existingSpace);

            System.out.println("Before save - Owner ID: " + updatedSpace.getOwnerId() +
                    ", Building ID: " + updatedSpace.getBuildingId());

            ComercialSpace savedSpace = spaceService.updateSpace(updatedSpace);

            System.out.println("After save - Owner ID: " + savedSpace.getOwnerId() +
                    ", Building ID: " + savedSpace.getBuildingId());

            // Convert back to DTO for response
            ComercialSpaceDto responseDto = spaceMapper.toDto(savedSpace);
            return ResponseEntity.ok(responseDto);

        } catch (Exception e) {
            System.err.println("Error updating space: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(null);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<ComercialSpaceDto> createSpace(@RequestBody ComercialSpaceCreateDto createDto) {
        try {
            System.out.println("=== CREATE SPACE WITH DTO DEBUG ===");
            System.out.println("Raw create DTO: " + createDto);

            if (createDto.getOwnerId() == null) {
                System.err.println("ERROR: Owner ID is missing from create DTO");
                return ResponseEntity.badRequest().body(null);
            }

            if (createDto.getBuildingId() == null) {
                System.err.println("ERROR: Building ID is missing from create DTO");
                return ResponseEntity.badRequest().body(null);
            }

            System.out.println("Extracted Owner ID: " + createDto.getOwnerId());
            System.out.println("Extracted Building ID: " + createDto.getBuildingId());

            // Convert DTO to entity using mapper
            ComercialSpace space = spaceMapper.toEntity(createDto);

            System.out.println("Mapped space object with Owner ID: " + space.getOwner().getId() +
                    " and Building ID: " + space.getBuilding().getId());

            ComercialSpace createdSpace = spaceService.addSpace(space);

            System.out.println("Space created successfully - ID: " + createdSpace.getId() +
                    ", Owner ID: " + createdSpace.getOwnerId() +
                    ", Owner Name: " + createdSpace.getOwnerName());
            System.out.println("=== END CREATE SPACE DEBUG ===");

            // Convert back to DTO for response
            ComercialSpaceDto responseDto = spaceMapper.toDto(createdSpace);
            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            System.err.println("Error creating space: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/available")
    public ResponseEntity<List<ComercialSpaceDto>> getAvailableSpaces() {
        try {
            List<ComercialSpace> spaces = spaceService.getAvailableSpaces();
            List<ComercialSpaceDto> spaceDtos = spaces.stream()
                    .map(spaceMapper::toDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(spaceDtos);
        } catch (Exception e) {
            System.err.println("Error getting available spaces: " + e.getMessage());
            return ResponseEntity.ok(List.of());
        }
    }

    @GetMapping("/type/{spaceType}")
    public ResponseEntity<List<ComercialSpaceDto>> getSpacesByType(@PathVariable String spaceType) {
        try {
            List<ComercialSpace> spaces = spaceService.getSpacesByType(spaceType);
            List<ComercialSpaceDto> spaceDtos = spaces.stream()
                    .map(spaceMapper::toDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(spaceDtos);
        } catch (Exception e) {
            System.err.println("Error getting spaces by type: " + e.getMessage());
            return ResponseEntity.ok(List.of());
        }
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<ComercialSpaceDto>> getSpacesByOwner(@PathVariable Long ownerId) {
        try {
            System.out.println("Getting spaces for owner ID: " + ownerId);
            List<ComercialSpace> spaces = spaceService.getSpacesByOwner(ownerId);
            System.out.println("Found " + (spaces != null ? spaces.size() : 0) + " spaces for owner " + ownerId);

            List<ComercialSpaceDto> spaceDtos = spaces.stream()
                    .map(spaceMapper::toDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(spaceDtos);
        } catch (Exception e) {
            System.err.println("Error getting spaces by owner: " + e.getMessage());
            return ResponseEntity.ok(List.of());
        }
    }

    @GetMapping("/building/{buildingId}")
    public ResponseEntity<List<ComercialSpaceDto>> getSpacesByBuilding(@PathVariable Long buildingId) {
        try {
            List<ComercialSpace> spaces = spaceService.getSpacesByBuilding(buildingId);
            List<ComercialSpaceDto> spaceDtos = spaces.stream()
                    .map(spaceMapper::toDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(spaceDtos);
        } catch (Exception e) {
            System.err.println("Error getting spaces by building: " + e.getMessage());
            return ResponseEntity.ok(List.of());
        }
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<Void> deleteSpace(@PathVariable Long id) {
        try {
            spaceService.deleteSpace(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.err.println("Error deleting space: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}
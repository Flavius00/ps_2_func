package com.example.demo.controller;

import com.example.demo.dto.BuildingDto;
import com.example.demo.dto.BuildingCreateDto;
import com.example.demo.dto.ComercialSpaceDto;
import com.example.demo.mapper.BuildingMapper;
import com.example.demo.mapper.ComercialSpaceMapper;
import com.example.demo.model.Building;
import com.example.demo.model.ComercialSpace;
import com.example.demo.service.BuildingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/buildings")
@CrossOrigin(origins = "http://localhost:3000")
public class BuildingController {
    private final BuildingService buildingService;
    private final BuildingMapper buildingMapper;
    private final ComercialSpaceMapper commercialSpaceMapper;

    public BuildingController(BuildingService buildingService,
                              BuildingMapper buildingMapper,
                              ComercialSpaceMapper commercialSpaceMapper) {
        this.buildingService = buildingService;
        this.buildingMapper = buildingMapper;
        this.commercialSpaceMapper = commercialSpaceMapper;
    }

    @GetMapping
    public ResponseEntity<List<BuildingDto>> getAllBuildings() {
        try {
            List<Building> buildings = buildingService.getAllBuildings();
            List<BuildingDto> buildingDtos = buildings.stream()
                    .map(buildingMapper::toDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(buildingDtos);
        } catch (Exception e) {
            System.err.println("Error getting all buildings: " + e.getMessage());
            return ResponseEntity.ok(List.of());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<BuildingDto> getBuildingById(@PathVariable Long id) {
        try {
            Building building = buildingService.getBuildingById(id);
            BuildingDto buildingDto = buildingMapper.toDto(building);
            return ResponseEntity.ok(buildingDto);
        } catch (Exception e) {
            System.err.println("Error getting building by id: " + e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<BuildingDto> addBuilding(@RequestBody BuildingCreateDto createDto) {
        try {
            Building building = buildingMapper.toEntity(createDto);
            Building savedBuilding = buildingService.addBuilding(building);
            BuildingDto responseDto = buildingMapper.toDto(savedBuilding);
            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            System.err.println("Error creating building: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<BuildingDto> updateBuilding(@PathVariable Long id, @RequestBody BuildingDto buildingDto) {
        try {
            buildingDto.setId(id);
            Building building = buildingMapper.toEntity(buildingDto);
            Building updatedBuilding = buildingService.updateBuilding(building);
            BuildingDto responseDto = buildingMapper.toDto(updatedBuilding);
            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            System.err.println("Error updating building: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBuilding(@PathVariable Long id) {
        try {
            buildingService.deleteBuilding(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.err.println("Error deleting building: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    // Building spaces endpoints with DTOs
    @GetMapping("/{id}/spaces")
    public ResponseEntity<List<ComercialSpaceDto>> getBuildingSpaces(@PathVariable Long id) {
        try {
            List<ComercialSpace> spaces = buildingService.getBuildingSpaces(id);
            List<ComercialSpaceDto> spaceDtos = spaces.stream()
                    .map(commercialSpaceMapper::toDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(spaceDtos);
        } catch (Exception e) {
            System.err.println("Error getting building spaces: " + e.getMessage());
            return ResponseEntity.ok(List.of());
        }
    }

    @GetMapping("/{id}/spaces/available")
    public ResponseEntity<List<ComercialSpaceDto>> getBuildingAvailableSpaces(@PathVariable Long id) {
        try {
            List<ComercialSpace> spaces = buildingService.getBuildingAvailableSpaces(id);
            List<ComercialSpaceDto> spaceDtos = spaces.stream()
                    .map(commercialSpaceMapper::toDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(spaceDtos);
        } catch (Exception e) {
            System.err.println("Error getting building available spaces: " + e.getMessage());
            return ResponseEntity.ok(List.of());
        }
    }

    @GetMapping("/{id}/spaces/count")
    public ResponseEntity<Long> getBuildingSpacesCount(@PathVariable Long id) {
        try {
            long count = buildingService.getSpaceCountByBuildingId(id);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            System.err.println("Error getting building spaces count: " + e.getMessage());
            return ResponseEntity.ok(0L);
        }
    }

    @GetMapping("/{id}/spaces/available/count")
    public ResponseEntity<Long> getBuildingAvailableSpacesCount(@PathVariable Long id) {
        try {
            long count = buildingService.getBuildingAvailableSpacesCount(id);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            System.err.println("Error getting building available spaces count: " + e.getMessage());
            return ResponseEntity.ok(0L);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<BuildingDto>> searchBuildings(@RequestParam String keyword) {
        try {
            List<Building> buildings = buildingService.searchBuildings(keyword);
            List<BuildingDto> buildingDtos = buildings.stream()
                    .map(buildingMapper::toDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(buildingDtos);
        } catch (Exception e) {
            System.err.println("Error searching buildings: " + e.getMessage());
            return ResponseEntity.ok(List.of());
        }
    }

    @GetMapping("/with-available-spaces")
    public ResponseEntity<List<BuildingDto>> getBuildingsWithAvailableSpaces() {
        try {
            List<Building> buildings = buildingService.getBuildingsWithAvailableSpaces();
            List<BuildingDto> buildingDtos = buildings.stream()
                    .map(buildingMapper::toDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(buildingDtos);
        } catch (Exception e) {
            System.err.println("Error getting buildings with available spaces: " + e.getMessage());
            return ResponseEntity.ok(List.of());
        }
    }
}
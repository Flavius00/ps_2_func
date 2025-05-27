package com.example.demo.controller;

import com.example.demo.model.Building;
import com.example.demo.model.ComercialSpace;
import com.example.demo.service.BuildingService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/buildings")
@CrossOrigin(origins = "http://localhost:3000")
public class BuildingController {
    private final BuildingService buildingService;

    public BuildingController(BuildingService buildingService) {
        this.buildingService = buildingService;
    }

    @GetMapping
    public List<Building> getAllBuildings() {
        return buildingService.getAllBuildings();
    }

    @GetMapping("/{id}")
    public Building getBuildingById(@PathVariable Long id) {
        return buildingService.getBuildingById(id);
    }

    @PostMapping
    public Building addBuilding(@RequestBody Building building) {
        return buildingService.addBuilding(building);
    }

    @PutMapping("/{id}")
    public Building updateBuilding(@PathVariable Long id, @RequestBody Building building) {
        building.setId(id);
        return buildingService.updateBuilding(building);
    }

    @DeleteMapping("/{id}")
    public void deleteBuilding(@PathVariable Long id) {
        buildingService.deleteBuilding(id);
    }

    // ADĂUGATE: Endpoint-uri pentru spațiile unei clădiri
    @GetMapping("/{id}/spaces")
    public List<ComercialSpace> getBuildingSpaces(@PathVariable Long id) {
        return buildingService.getBuildingSpaces(id);
    }

    @GetMapping("/{id}/spaces/available")
    public List<ComercialSpace> getBuildingAvailableSpaces(@PathVariable Long id) {
        return buildingService.getBuildingAvailableSpaces(id);
    }

    @GetMapping("/{id}/spaces/count")
    public long getBuildingSpacesCount(@PathVariable Long id) {
        return buildingService.getSpaceCountByBuildingId(id);
    }

    @GetMapping("/{id}/spaces/available/count")
    public long getBuildingAvailableSpacesCount(@PathVariable Long id) {
        return buildingService.getBuildingAvailableSpacesCount(id);
    }

    @GetMapping("/search")
    public List<Building> searchBuildings(@RequestParam String keyword) {
        return buildingService.searchBuildings(keyword);
    }

    @GetMapping("/with-available-spaces")
    public List<Building> getBuildingsWithAvailableSpaces() {
        return buildingService.getBuildingsWithAvailableSpaces();
    }
}
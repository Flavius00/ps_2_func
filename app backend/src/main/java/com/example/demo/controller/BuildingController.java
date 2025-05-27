package com.example.demo.controller;

import com.example.demo.model.Building;
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
}
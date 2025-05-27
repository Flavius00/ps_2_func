package com.example.demo.service.impl;

import com.example.demo.model.Building;
import com.example.demo.repository.BuildingRepository;
import com.example.demo.service.BuildingService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BuildingServiceImpl implements BuildingService {
    private final BuildingRepository buildingRepository;

    public BuildingServiceImpl(BuildingRepository buildingRepository) {
        this.buildingRepository = buildingRepository;
    }

    @Override
    public Building addBuilding(Building building) {
        return buildingRepository.save(building);
    }

    @Override
    public List<Building> getAllBuildings() {
        return buildingRepository.findAll();
    }

    @Override
    public Building getBuildingById(Long id) {
        return buildingRepository.findById(id);
    }

    @Override
    public Building updateBuilding(Building building) {
        return buildingRepository.update(building);
    }

    @Override
    public void deleteBuilding(Long id) {
        buildingRepository.deleteById(id);
    }
}
package com.example.demo.service.impl;

import com.example.demo.model.Building;
import com.example.demo.repository.BuildingRepository;
import com.example.demo.service.BuildingService;
import com.example.demo.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
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
    @Transactional(readOnly = true)
    public List<Building> getAllBuildings() {
        return buildingRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Building getBuildingById(Long id) {
        return buildingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Building not found with id: " + id));
    }

    @Override
    public Building updateBuilding(Building building) {
        if (!buildingRepository.existsById(building.getId())) {
            throw new ResourceNotFoundException("Building not found with id: " + building.getId());
        }
        return buildingRepository.save(building);
    }

    @Override
    public void deleteBuilding(Long id) {
        if (!buildingRepository.existsById(id)) {
            throw new ResourceNotFoundException("Building not found with id: " + id);
        }
        buildingRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Building> searchBuildings(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllBuildings();
        }

        List<Building> byName = buildingRepository.findByNameContaining(keyword);
        List<Building> byAddress = buildingRepository.findByAddressContaining(keyword);

        // Combină rezultatele și elimină duplicatele
        byName.addAll(byAddress);
        return byName.stream().distinct().toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Building> getBuildingsWithAvailableSpaces() {
        return buildingRepository.findBuildingsWithAvailableSpaces();
    }

    @Override
    @Transactional(readOnly = true)
    public long getSpaceCountByBuildingId(Long buildingId) {
        return buildingRepository.countSpacesByBuildingId(buildingId);
    }
}
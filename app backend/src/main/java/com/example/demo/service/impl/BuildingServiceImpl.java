package com.example.demo.service.impl;

import com.example.demo.mapper.BuildingMapper;
import com.example.demo.mapper.ComercialSpaceMapper;
import com.example.demo.model.Building;
import com.example.demo.model.ComercialSpace;
import com.example.demo.repository.BuildingRepository;
import com.example.demo.repository.ComercialSpaceRepository;
import com.example.demo.service.BuildingService;
import com.example.demo.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class BuildingServiceImpl implements BuildingService {
    private final BuildingRepository buildingRepository;
    private final ComercialSpaceRepository comercialSpaceRepository;
    private final BuildingMapper buildingMapper;
    private final ComercialSpaceMapper spaceMapper;

    public BuildingServiceImpl(BuildingRepository buildingRepository,
                               ComercialSpaceRepository comercialSpaceRepository,
                               BuildingMapper buildingMapper,
                               ComercialSpaceMapper spaceMapper) {
        this.buildingRepository = buildingRepository;
        this.comercialSpaceRepository = comercialSpaceRepository;
        this.buildingMapper = buildingMapper;
        this.spaceMapper = spaceMapper;
    }

    // Metodele existente rămân neschimbate
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

        long spacesCount = comercialSpaceRepository.countByBuildingId(id);
        if (spacesCount > 0) {
            throw new IllegalStateException("Cannot delete building with associated commercial spaces. " +
                    "Please remove all spaces first.");
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

    @Override
    @Transactional(readOnly = true)
    public List<ComercialSpace> getBuildingSpaces(Long buildingId) {
        if (!buildingRepository.existsById(buildingId)) {
            throw new ResourceNotFoundException("Building not found with id: " + buildingId);
        }
        return comercialSpaceRepository.findByBuildingId(buildingId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ComercialSpace> getBuildingAvailableSpaces(Long buildingId) {
        if (!buildingRepository.existsById(buildingId)) {
            throw new ResourceNotFoundException("Building not found with id: " + buildingId);
        }
        return comercialSpaceRepository.findByBuildingIdAndAvailable(buildingId, true);
    }

    @Override
    @Transactional(readOnly = true)
    public long getBuildingAvailableSpacesCount(Long buildingId) {
        return buildingRepository.countAvailableSpacesByBuildingId(buildingId);
    }
}
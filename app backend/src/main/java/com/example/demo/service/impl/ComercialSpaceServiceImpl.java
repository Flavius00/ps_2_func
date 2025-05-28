package com.example.demo.service.impl;

import com.example.demo.exception.*;
import com.example.demo.mapper.ComercialSpaceMapper;
import com.example.demo.model.ComercialSpace;
import com.example.demo.model.Owner;
import com.example.demo.model.Building;
import com.example.demo.repository.ComercialSpaceRepository;
import com.example.demo.repository.OwnerRepository;
import com.example.demo.repository.BuildingRepository;
import com.example.demo.service.ComercialSpaceService;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ComercialSpaceServiceImpl implements ComercialSpaceService {
    private final ComercialSpaceRepository spaceRepository;
    private final OwnerRepository ownerRepository;
    private final BuildingRepository buildingRepository;
    private final ComercialSpaceMapper spaceMapper;

    public ComercialSpaceServiceImpl(ComercialSpaceRepository spaceRepository,
                                     OwnerRepository ownerRepository,
                                     BuildingRepository buildingRepository,
                                     ComercialSpaceMapper spaceMapper) {
        this.spaceRepository = spaceRepository;
        this.ownerRepository = ownerRepository;
        this.buildingRepository = buildingRepository;
        this.spaceMapper = spaceMapper;
    }

    @Override
    public ComercialSpace addSpace(ComercialSpace space) {
        try {
            validateSpaceForCreation(space);

            Owner owner = ownerRepository.findById(space.getOwner().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Owner not found with id: " + space.getOwner().getId()));

            Building building = buildingRepository.findById(space.getBuilding().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Building not found with id: " + space.getBuilding().getId()));

            space.setOwner(owner);
            space.setBuilding(building);

            return spaceRepository.save(space);

        } catch (DataAccessException ex) {
            throw new DatabaseOperationException("create space", "Failed to save commercial space", ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ComercialSpace> getAllSpaces() {
        try {
            List<ComercialSpace> spaces = spaceRepository.findAll();
            return spaces != null ? spaces : List.of();
        } catch (DataAccessException ex) {
            throw new DatabaseOperationException("fetch all spaces", "Failed to retrieve commercial spaces", ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ComercialSpace getSpaceById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Space ID must be a positive number");
        }

        try {
            return spaceRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Commercial space not found with id: " + id));
        } catch (DataAccessException ex) {
            throw new DatabaseOperationException("fetch space by ID", "Failed to retrieve commercial space", ex);
        }
    }

    @Override
    public ComercialSpace updateSpace(ComercialSpace space) {
        if (space.getId() == null) {
            throw new IllegalArgumentException("Space ID cannot be null for update operation");
        }

        try {
            ComercialSpace existingSpace = spaceRepository.findById(space.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Commercial space not found with id: " + space.getId()));

            validateSpaceForUpdate(space, existingSpace);

            // Preserve owner and building if not provided
            if (space.getOwner() == null) {
                space.setOwner(existingSpace.getOwner());
            }
            if (space.getBuilding() == null) {
                space.setBuilding(existingSpace.getBuilding());
            }

            return spaceRepository.save(space);

        } catch (DataAccessException ex) {
            throw new DatabaseOperationException("update space", "Failed to update commercial space", ex);
        }
    }

    @Override
    public void deleteSpace(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Space ID must be a positive number");
        }

        try {
            ComercialSpace space = spaceRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Commercial space not found with id: " + id));

            // Check if space has active contracts
            if (space.getContracts() != null && !space.getContracts().isEmpty()) {
                boolean hasActiveContracts = space.getContracts().stream()
                        .anyMatch(contract -> "ACTIVE".equals(contract.getStatus().name()));

                if (hasActiveContracts) {
                    throw new InvalidOperationException("delete space",
                            "Cannot delete space with active contracts. Please terminate all contracts first.");
                }
            }

            spaceRepository.deleteById(id);

        } catch (DataAccessException ex) {
            throw new DatabaseOperationException("delete space", "Failed to delete commercial space", ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ComercialSpace> getAvailableSpaces() {
        try {
            List<ComercialSpace> spaces = spaceRepository.findByAvailable(true);
            return spaces != null ? spaces : List.of();
        } catch (DataAccessException ex) {
            throw new DatabaseOperationException("fetch available spaces", "Failed to retrieve available spaces", ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ComercialSpace> getSpacesByType(String spaceType) {
        if (spaceType == null || spaceType.trim().isEmpty()) {
            throw new IllegalArgumentException("Space type cannot be null or empty");
        }

        try {
            ComercialSpace.SpaceType type = ComercialSpace.SpaceType.valueOf(spaceType.toUpperCase());
            List<ComercialSpace> spaces = spaceRepository.findBySpaceType(type);
            return spaces != null ? spaces : List.of();
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid space type: " + spaceType +
                    ". Valid types are: OFFICE, RETAIL, WAREHOUSE");
        } catch (DataAccessException ex) {
            throw new DatabaseOperationException("fetch spaces by type", "Failed to retrieve spaces by type", ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ComercialSpace> getSpacesByOwner(Long ownerId) {
        if (ownerId == null || ownerId <= 0) {
            throw new IllegalArgumentException("Owner ID must be a positive number");
        }

        // Verify owner exists
        if (!ownerRepository.existsById(ownerId)) {
            throw new ResourceNotFoundException("Owner not found with id: " + ownerId);
        }

        try {
            List<ComercialSpace> spaces = spaceRepository.findByOwnerId(ownerId);
            return spaces != null ? spaces : List.of();
        } catch (DataAccessException ex) {
            throw new DatabaseOperationException("fetch spaces by owner", "Failed to retrieve spaces by owner", ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ComercialSpace> getSpacesByBuilding(Long buildingId) {
        if (buildingId == null || buildingId <= 0) {
            throw new IllegalArgumentException("Building ID must be a positive number");
        }

        // Verify building exists
        if (!buildingRepository.existsById(buildingId)) {
            throw new ResourceNotFoundException("Building not found with id: " + buildingId);
        }

        try {
            List<ComercialSpace> spaces = spaceRepository.findByBuildingId(buildingId);
            return spaces != null ? spaces : List.of();
        } catch (DataAccessException ex) {
            throw new DatabaseOperationException("fetch spaces by building", "Failed to retrieve spaces by building", ex);
        }
    }

    // Additional methods with exception handling...
    @Override
    @Transactional(readOnly = true)
    public List<ComercialSpace> searchSpaces(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllSpaces();
        }

        try {
            List<ComercialSpace> spaces = spaceRepository.searchByKeyword(keyword);
            return spaces != null ? spaces : List.of();
        } catch (DataAccessException ex) {
            throw new DatabaseOperationException("search spaces", "Failed to search commercial spaces", ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ComercialSpace> getSpacesByPriceRange(Double minPrice, Double maxPrice) {
        if (minPrice != null && minPrice < 0) {
            throw new IllegalArgumentException("Minimum price cannot be negative");
        }
        if (maxPrice != null && maxPrice < 0) {
            throw new IllegalArgumentException("Maximum price cannot be negative");
        }
        if (minPrice != null && maxPrice != null && minPrice > maxPrice) {
            throw new IllegalArgumentException("Minimum price cannot be greater than maximum price");
        }

        try {
            List<ComercialSpace> spaces = spaceRepository.findByPricePerMonthBetween(
                    minPrice != null ? minPrice : 0.0,
                    maxPrice != null ? maxPrice : Double.MAX_VALUE
            );
            return spaces != null ? spaces : List.of();
        } catch (DataAccessException ex) {
            throw new DatabaseOperationException("fetch spaces by price range", "Failed to retrieve spaces by price range", ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ComercialSpace> getSpacesByAreaRange(Double minArea, Double maxArea) {
        if (minArea != null && minArea <= 0) {
            throw new IllegalArgumentException("Minimum area must be positive");
        }
        if (maxArea != null && maxArea <= 0) {
            throw new IllegalArgumentException("Maximum area must be positive");
        }
        if (minArea != null && maxArea != null && minArea > maxArea) {
            throw new IllegalArgumentException("Minimum area cannot be greater than maximum area");
        }

        try {
            List<ComercialSpace> spaces = spaceRepository.findByAreaBetween(
                    minArea != null ? minArea : 0.0,
                    maxArea != null ? maxArea : Double.MAX_VALUE
            );
            return spaces != null ? spaces : List.of();
        } catch (DataAccessException ex) {
            throw new DatabaseOperationException("fetch spaces by area range", "Failed to retrieve spaces by area range", ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ComercialSpace> getAvailableSpacesByType(String spaceType) {
        if (spaceType == null || spaceType.trim().isEmpty()) {
            throw new IllegalArgumentException("Space type cannot be null or empty");
        }

        try {
            ComercialSpace.SpaceType type = ComercialSpace.SpaceType.valueOf(spaceType.toUpperCase());
            List<ComercialSpace> spaces = spaceRepository.findAvailableBySpaceType(type);
            return spaces != null ? spaces : List.of();
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid space type: " + spaceType +
                    ". Valid types are: OFFICE, RETAIL, WAREHOUSE");
        } catch (DataAccessException ex) {
            throw new DatabaseOperationException("fetch available spaces by type", "Failed to retrieve available spaces by type", ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public long getAvailableSpacesCount() {
        try {
            return spaceRepository.countAvailableSpaces();
        } catch (DataAccessException ex) {
            throw new DatabaseOperationException("count available spaces", "Failed to count available spaces", ex);
        }
    }

    // Private validation methods
    private void validateSpaceForCreation(ComercialSpace space) {
        if (space == null) {
            throw new IllegalArgumentException("Commercial space cannot be null");
        }

        if (space.getName() == null || space.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Space name cannot be null or empty");
        }

        if (space.getArea() == null || space.getArea() <= 0) {
            throw new IllegalArgumentException("Space area must be positive");
        }

        if (space.getPricePerMonth() == null || space.getPricePerMonth() < 0) {
            throw new IllegalArgumentException("Price per month cannot be negative");
        }

        if (space.getOwner() == null || space.getOwner().getId() == null) {
            throw new IllegalArgumentException("Owner is required for creating a space");
        }

        if (space.getBuilding() == null || space.getBuilding().getId() == null) {
            throw new IllegalArgumentException("Building is required for creating a space");
        }
    }

    private void validateSpaceForUpdate(ComercialSpace space, ComercialSpace existingSpace) {
        if (space == null) {
            throw new IllegalArgumentException("Commercial space cannot be null");
        }

        if (space.getName() != null && space.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Space name cannot be empty");
        }

        if (space.getArea() != null && space.getArea() <= 0) {
            throw new IllegalArgumentException("Space area must be positive");
        }

        if (space.getPricePerMonth() != null && space.getPricePerMonth() < 0) {
            throw new IllegalArgumentException("Price per month cannot be negative");
        }

        // Check if space is being marked as unavailable when it has active contracts
        if (space.getAvailable() != null && !space.getAvailable() && existingSpace.getAvailable()) {
            if (existingSpace.getContracts() != null && !existingSpace.getContracts().isEmpty()) {
                boolean hasActiveContracts = existingSpace.getContracts().stream()
                        .anyMatch(contract -> "ACTIVE".equals(contract.getStatus().name()));

                if (hasActiveContracts) {
                    throw new InvalidOperationException("mark space as unavailable",
                            "Space has active contracts and cannot be marked as unavailable");
                }
            }
        }
    }
}
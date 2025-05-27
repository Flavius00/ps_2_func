package com.example.demo.service.impl;

import com.example.demo.model.ComercialSpace;
import com.example.demo.repository.ComercialSpaceRepository;
import com.example.demo.service.ComercialSpaceService;
import com.example.demo.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ComercialSpaceServiceImpl implements ComercialSpaceService {
    private final ComercialSpaceRepository spaceRepository;

    public ComercialSpaceServiceImpl(ComercialSpaceRepository spaceRepository) {
        this.spaceRepository = spaceRepository;
    }

    @Override
    public ComercialSpace addSpace(ComercialSpace space) {
        return spaceRepository.save(space);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ComercialSpace> getAllSpaces() {
        return spaceRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public ComercialSpace getSpaceById(Long id) {
        return spaceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commercial space not found with id: " + id));
    }

    @Override
    public ComercialSpace updateSpace(ComercialSpace space) {
        if (!spaceRepository.existsById(space.getId())) {
            throw new ResourceNotFoundException("Commercial space not found with id: " + space.getId());
        }
        return spaceRepository.save(space);
    }

    @Override
    public void deleteSpace(Long id) {
        if (!spaceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Commercial space not found with id: " + id);
        }
        spaceRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ComercialSpace> getAvailableSpaces() {
        return spaceRepository.findByAvailable(true);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ComercialSpace> getSpacesByType(String spaceType) {
        try {
            ComercialSpace.SpaceType type = ComercialSpace.SpaceType.valueOf(spaceType.toUpperCase());
            return spaceRepository.findBySpaceType(type);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid space type: " + spaceType);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ComercialSpace> getSpacesByOwner(Long ownerId) {
        return spaceRepository.findByOwnerId(ownerId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ComercialSpace> getSpacesByBuilding(Long buildingId) {
        return spaceRepository.findByBuildingId(buildingId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ComercialSpace> searchSpaces(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllSpaces();
        }
        return spaceRepository.searchByKeyword(keyword);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ComercialSpace> getSpacesByPriceRange(Double minPrice, Double maxPrice) {
        return spaceRepository.findByPricePerMonthBetween(minPrice, maxPrice);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ComercialSpace> getSpacesByAreaRange(Double minArea, Double maxArea) {
        return spaceRepository.findByAreaBetween(minArea, maxArea);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ComercialSpace> getAvailableSpacesByType(String spaceType) {
        try {
            ComercialSpace.SpaceType type = ComercialSpace.SpaceType.valueOf(spaceType.toUpperCase());
            return spaceRepository.findAvailableBySpaceType(type);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid space type: " + spaceType);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public long getAvailableSpacesCount() {
        return spaceRepository.countAvailableSpaces();
    }
}
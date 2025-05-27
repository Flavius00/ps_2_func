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
        try {
            List<ComercialSpace> spaces = spaceRepository.findAll();
            System.out.println("Service: Found " + spaces.size() + " spaces in database");
            // Asigură-te că nu returnezi null
            return spaces != null ? spaces : List.of();
        } catch (Exception e) {
            System.err.println("Error in getAllSpaces service: " + e.getMessage());
            e.printStackTrace();
            return List.of(); // Returnează listă goală în caz de eroare
        }
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
        try {
            List<ComercialSpace> spaces = spaceRepository.findByAvailable(true);
            return spaces != null ? spaces : List.of();
        } catch (Exception e) {
            System.err.println("Error getting available spaces: " + e.getMessage());
            return List.of();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ComercialSpace> getSpacesByType(String spaceType) {
        try {
            ComercialSpace.SpaceType type = ComercialSpace.SpaceType.valueOf(spaceType.toUpperCase());
            List<ComercialSpace> spaces = spaceRepository.findBySpaceType(type);
            return spaces != null ? spaces : List.of();
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid space type: " + spaceType);
        } catch (Exception e) {
            System.err.println("Error getting spaces by type: " + e.getMessage());
            return List.of();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ComercialSpace> getSpacesByOwner(Long ownerId) {
        try {
            List<ComercialSpace> spaces = spaceRepository.findByOwnerId(ownerId);
            return spaces != null ? spaces : List.of();
        } catch (Exception e) {
            System.err.println("Error getting spaces by owner: " + e.getMessage());
            return List.of();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ComercialSpace> getSpacesByBuilding(Long buildingId) {
        try {
            List<ComercialSpace> spaces = spaceRepository.findByBuildingId(buildingId);
            return spaces != null ? spaces : List.of();
        } catch (Exception e) {
            System.err.println("Error getting spaces by building: " + e.getMessage());
            return List.of();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ComercialSpace> searchSpaces(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllSpaces();
        }
        try {
            List<ComercialSpace> spaces = spaceRepository.searchByKeyword(keyword);
            return spaces != null ? spaces : List.of();
        } catch (Exception e) {
            System.err.println("Error searching spaces: " + e.getMessage());
            return List.of();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ComercialSpace> getSpacesByPriceRange(Double minPrice, Double maxPrice) {
        try {
            List<ComercialSpace> spaces = spaceRepository.findByPricePerMonthBetween(minPrice, maxPrice);
            return spaces != null ? spaces : List.of();
        } catch (Exception e) {
            System.err.println("Error getting spaces by price range: " + e.getMessage());
            return List.of();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ComercialSpace> getSpacesByAreaRange(Double minArea, Double maxArea) {
        try {
            List<ComercialSpace> spaces = spaceRepository.findByAreaBetween(minArea, maxArea);
            return spaces != null ? spaces : List.of();
        } catch (Exception e) {
            System.err.println("Error getting spaces by area range: " + e.getMessage());
            return List.of();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ComercialSpace> getAvailableSpacesByType(String spaceType) {
        try {
            ComercialSpace.SpaceType type = ComercialSpace.SpaceType.valueOf(spaceType.toUpperCase());
            List<ComercialSpace> spaces = spaceRepository.findAvailableBySpaceType(type);
            return spaces != null ? spaces : List.of();
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid space type: " + spaceType);
        } catch (Exception e) {
            System.err.println("Error getting available spaces by type: " + e.getMessage());
            return List.of();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public long getAvailableSpacesCount() {
        try {
            return spaceRepository.countAvailableSpaces();
        } catch (Exception e) {
            System.err.println("Error counting available spaces: " + e.getMessage());
            return 0;
        }
    }
}
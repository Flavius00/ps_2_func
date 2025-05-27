package com.example.demo.service.impl;

import com.example.demo.model.ComercialSpace;
import com.example.demo.repository.ComercialSpaceRepository;
import com.example.demo.service.ComercialSpaceService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ComercialSpaceServiceImpl implements ComercialSpaceService {
    private final ComercialSpaceRepository spaceRepository;

    public ComercialSpaceServiceImpl(ComercialSpaceRepository spaceRepository) {
        this.spaceRepository = spaceRepository;
    }

    @Override
    public ComercialSpace addSpace(ComercialSpace space) {
        space.setId((long) (spaceRepository.findAll().size() + 1));
        return spaceRepository.save(space);
    }

    @Override
    public List<ComercialSpace> getAllSpaces() {
        return spaceRepository.findAll();
    }

    @Override
    public ComercialSpace getSpaceById(Long id) {
        ComercialSpace space = spaceRepository.findById(id);
        if (space == null) {
            throw new IllegalArgumentException("Comercial space not found with ID: " + id);
        }
        return space;
    }

    @Override
    public ComercialSpace updateSpace(ComercialSpace space) {
        return spaceRepository.update(space);
    }

    @Override
    public void deleteSpace(Long id) {
        spaceRepository.deleteById(id);
    }

    @Override
    public List<ComercialSpace> getAvailableSpaces() {
        return spaceRepository.findAll().stream()
                .filter(ComercialSpace::getAvailable)
                .collect(Collectors.toList());
    }

    @Override
    public List<ComercialSpace> getSpacesByType(String spaceType) {
        return spaceRepository.findAll().stream()
                .filter(space -> spaceType.equalsIgnoreCase(space.getSpaceType()))
                .collect(Collectors.toList());
    }

    @Override
    public List<ComercialSpace> getSpacesByOwner(Long ownerId) {
        return spaceRepository.findAll().stream()
                .filter(space -> space.getOwner() != null && space.getOwner().getId().equals(ownerId))
                .collect(Collectors.toList());
    }

    @Override
    public List<ComercialSpace> getSpacesByBuilding(Long buildingId) {
        return spaceRepository.findAll().stream()
                .filter(space -> space.getBuilding() != null && space.getBuilding().getId().equals(buildingId))
                .collect(Collectors.toList());
    }
}
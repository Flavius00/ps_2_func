package com.example.demo.repository;

import com.example.demo.model.Building;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class BuildingRepository {
    private final List<Building> buildings = new ArrayList<>();

    public Building save(Building building) {
        if (building.getId() == null) {
            building.setId((long) (buildings.size() + 1));
        }

        Building existingBuilding = findById(building.getId());
        if (existingBuilding == null) {
            buildings.add(building);
        } else {
            update(building);
        }
        return building;
    }

    public List<Building> findAll() {
        return buildings;
    }

    public Building findById(Long id) {
        return buildings.stream()
                .filter(building -> building.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public Building update(Building updatedBuilding) {
        for (int i = 0; i < buildings.size(); i++) {
            if (buildings.get(i).getId().equals(updatedBuilding.getId())) {
                buildings.set(i, updatedBuilding);
                return updatedBuilding;
            }
        }
        return null;
    }

    public boolean deleteById(Long id) {
        return buildings.removeIf(building -> building.getId().equals(id));
    }
}
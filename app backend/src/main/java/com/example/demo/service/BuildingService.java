package com.example.demo.service;

import com.example.demo.model.Building;
import java.util.List;

public interface BuildingService {
    Building addBuilding(Building building);
    List<Building> getAllBuildings();
    Building getBuildingById(Long id);
    Building updateBuilding(Building building);
    void deleteBuilding(Long id);
}
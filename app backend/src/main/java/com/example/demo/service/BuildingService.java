package com.example.demo.service;

import com.example.demo.model.Building;
import com.example.demo.model.ComercialSpace;
import java.util.List;

public interface BuildingService {
    Building addBuilding(Building building);
    List<Building> getAllBuildings();
    Building getBuildingById(Long id);
    Building updateBuilding(Building building);
    void deleteBuilding(Long id);

    // Metode pentru gestionarea spațiilor unei clădiri
    List<Building> searchBuildings(String keyword);
    List<Building> getBuildingsWithAvailableSpaces();
    long getSpaceCountByBuildingId(Long buildingId);

    // ADĂUGATE: Metode pentru obținerea spațiilor unei clădiri
    List<ComercialSpace> getBuildingSpaces(Long buildingId);
    List<ComercialSpace> getBuildingAvailableSpaces(Long buildingId);
    long getBuildingAvailableSpacesCount(Long buildingId);
}
package com.example.demo.service;

import com.example.demo.model.ComercialSpace;
import java.util.List;

public interface ComercialSpaceService {
    ComercialSpace addSpace(ComercialSpace space);
    List<ComercialSpace> getAllSpaces();
    ComercialSpace getSpaceById(Long id);
    ComercialSpace updateSpace(ComercialSpace space);
    void deleteSpace(Long id);
    List<ComercialSpace> getAvailableSpaces();
    List<ComercialSpace> getSpacesByType(String spaceType);
    List<ComercialSpace> getSpacesByOwner(Long ownerId);
    List<ComercialSpace> getSpacesByBuilding(Long buildingId);
}
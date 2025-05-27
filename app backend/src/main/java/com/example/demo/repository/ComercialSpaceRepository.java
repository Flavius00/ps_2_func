package com.example.demo.repository;

import com.example.demo.model.ComercialSpace;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class ComercialSpaceRepository {
    private final List<ComercialSpace> spaces = new ArrayList<>();

    public ComercialSpace save(ComercialSpace space) {
        ComercialSpace existingSpace = findById(space.getId());
        if (existingSpace == null) {
            spaces.add(space);
        } else {
            // Update existing space properties
            existingSpace.setName(space.getName());
            existingSpace.setDescription(space.getDescription());
            existingSpace.setArea(space.getArea());
            existingSpace.setPricePerMonth(space.getPricePerMonth());
            existingSpace.setAddress(space.getAddress());
            existingSpace.setLatitude(space.getLatitude());
            existingSpace.setLongitude(space.getLongitude());
            existingSpace.setAmenities(space.getAmenities());
            existingSpace.setAvailable(space.getAvailable());
            existingSpace.setOwner(space.getOwner());
            existingSpace.setBuilding(space.getBuilding());
            existingSpace.setParking(space.getParking());
            existingSpace.setSpaceType(space.getSpaceType());

            // Space type specific properties
            existingSpace.setFloors(space.getFloors());
            existingSpace.setNumberOfRooms(space.getNumberOfRooms());
            existingSpace.setHasReception(space.getHasReception());
            existingSpace.setShopWindowSize(space.getShopWindowSize());
            existingSpace.setHasCustomerEntrance(space.getHasCustomerEntrance());
            existingSpace.setMaxOccupancy(space.getMaxOccupancy());
            existingSpace.setCeilingHeight(space.getCeilingHeight());
            existingSpace.setHasLoadingDock(space.getHasLoadingDock());
            existingSpace.setSecurityLevel(space.getSecurityLevel());
        }
        return space;
    }

    public List<ComercialSpace> findAll() {
        return spaces;
    }

    public ComercialSpace findById(Long id) {
        return spaces.stream()
                .filter(space -> space.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public ComercialSpace update(ComercialSpace updatedSpace) {
        ComercialSpace existingSpace = findById(updatedSpace.getId());
        if (existingSpace != null) {
            existingSpace.setName(updatedSpace.getName());
            existingSpace.setDescription(updatedSpace.getDescription());
            existingSpace.setArea(updatedSpace.getArea());
            existingSpace.setPricePerMonth(updatedSpace.getPricePerMonth());
            existingSpace.setAddress(updatedSpace.getAddress());
            existingSpace.setLatitude(updatedSpace.getLatitude());
            existingSpace.setLongitude(updatedSpace.getLongitude());
            existingSpace.setAmenities(updatedSpace.getAmenities());
            existingSpace.setAvailable(updatedSpace.getAvailable());
            existingSpace.setOwner(updatedSpace.getOwner());
            existingSpace.setBuilding(updatedSpace.getBuilding());
            existingSpace.setParking(updatedSpace.getParking());
            existingSpace.setSpaceType(updatedSpace.getSpaceType());

            // Space type specific properties
            existingSpace.setFloors(updatedSpace.getFloors());
            existingSpace.setNumberOfRooms(updatedSpace.getNumberOfRooms());
            existingSpace.setHasReception(updatedSpace.getHasReception());
            existingSpace.setShopWindowSize(updatedSpace.getShopWindowSize());
            existingSpace.setHasCustomerEntrance(updatedSpace.getHasCustomerEntrance());
            existingSpace.setMaxOccupancy(updatedSpace.getMaxOccupancy());
            existingSpace.setCeilingHeight(updatedSpace.getCeilingHeight());
            existingSpace.setHasLoadingDock(updatedSpace.getHasLoadingDock());
            existingSpace.setSecurityLevel(updatedSpace.getSecurityLevel());

            return existingSpace;
        }
        return null;
    }

    public boolean deleteById(Long id) {
        return spaces.removeIf(space -> space.getId().equals(id));
    }
}

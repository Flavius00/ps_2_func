package com.example.demo.mapper;

import com.example.demo.dto.ComercialSpaceDto;
import com.example.demo.dto.ComercialSpaceCreateDto;
import com.example.demo.dto.ComercialSpaceUpdateDto;
import com.example.demo.model.ComercialSpace;
import com.example.demo.model.Owner;
import com.example.demo.model.Building;
import com.example.demo.repository.OwnerRepository;
import com.example.demo.repository.BuildingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ComercialSpaceMapper {

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private BuildingRepository buildingRepository;

    public ComercialSpaceDto toDto(ComercialSpace entity) {
        if (entity == null) {
            return null;
        }

        ComercialSpaceDto dto = new ComercialSpaceDto();

        // Basic properties
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setArea(entity.getArea());
        dto.setPricePerMonth(entity.getPricePerMonth());
        dto.setAddress(entity.getAddress());
        dto.setLatitude(entity.getLatitude());
        dto.setLongitude(entity.getLongitude());
        dto.setAmenities(entity.getAmenities());
        dto.setAvailable(entity.getAvailable());

        // Space type
        if (entity.getSpaceType() != null) {
            dto.setSpaceType(entity.getSpaceType().name());
        }

        // Security level
        if (entity.getSecurityLevel() != null) {
            dto.setSecurityLevel(entity.getSecurityLevel().name());
        }

        // Type-specific fields
        dto.setFloors(entity.getFloors());
        dto.setNumberOfRooms(entity.getNumberOfRooms());
        dto.setHasReception(entity.getHasReception());
        dto.setShopWindowSize(entity.getShopWindowSize());
        dto.setHasCustomerEntrance(entity.getHasCustomerEntrance());
        dto.setMaxOccupancy(entity.getMaxOccupancy());
        dto.setCeilingHeight(entity.getCeilingHeight());
        dto.setHasLoadingDock(entity.getHasLoadingDock());

        // Owner information
        if (entity.getOwner() != null) {
            dto.setOwnerId(entity.getOwner().getId());
            dto.setOwnerName(entity.getOwner().getName());
            dto.setOwnerEmail(entity.getOwner().getEmail());
            dto.setOwnerPhone(entity.getOwner().getPhone());
            dto.setOwnerCompanyName(entity.getOwner().getCompanyName());
        }

        // Building information
        if (entity.getBuilding() != null) {
            dto.setBuildingId(entity.getBuilding().getId());
            dto.setBuildingName(entity.getBuilding().getName());
            dto.setBuildingAddress(entity.getBuilding().getAddress());
            dto.setBuildingTotalFloors(entity.getBuilding().getTotalFloors());
            dto.setBuildingYearBuilt(entity.getBuilding().getYearBuilt());
        }

        // Parking information
        if (entity.getParking() != null) {
            dto.setParkingId(entity.getParking().getId());
            dto.setParkingSpots(entity.getParking().getNumberOfSpots());
            dto.setParkingPricePerSpot(entity.getParking().getPricePerSpot());
            dto.setParkingCovered(entity.getParking().getCovered());
            if (entity.getParking().getParkingType() != null) {
                dto.setParkingType(entity.getParking().getParkingType().name());
            }
        }

        // Contract information
        if (entity.getContracts() != null) {
            dto.setContractsCount(entity.getContracts().size());
            dto.setHasActiveContract(entity.getContracts().stream()
                    .anyMatch(contract -> contract.getStatus() != null &&
                            contract.getStatus().name().equals("ACTIVE")));
        } else {
            dto.setContractsCount(0);
            dto.setHasActiveContract(false);
        }

        return dto;
    }

    public ComercialSpace toEntity(ComercialSpaceCreateDto createDto) {
        if (createDto == null) {
            return null;
        }

        ComercialSpace entity = new ComercialSpace();

        // Basic properties
        entity.setName(createDto.getName());
        entity.setDescription(createDto.getDescription());
        entity.setArea(createDto.getArea());
        entity.setPricePerMonth(createDto.getPricePerMonth());
        entity.setAddress(createDto.getAddress());
        entity.setLatitude(createDto.getLatitude());
        entity.setLongitude(createDto.getLongitude());
        entity.setAmenities(createDto.getAmenities());
        entity.setAvailable(createDto.getAvailable());

        // Space type
        if (createDto.getSpaceType() != null) {
            entity.setSpaceType(stringToSpaceType(createDto.getSpaceType()));
        }

        // Security level
        if (createDto.getSecurityLevel() != null) {
            entity.setSecurityLevel(stringToSecurityLevel(createDto.getSecurityLevel()));
        }

        // Type-specific fields
        entity.setFloors(createDto.getFloors());
        entity.setNumberOfRooms(createDto.getNumberOfRooms());
        entity.setHasReception(createDto.getHasReception());
        entity.setShopWindowSize(createDto.getShopWindowSize());
        entity.setHasCustomerEntrance(createDto.getHasCustomerEntrance());
        entity.setMaxOccupancy(createDto.getMaxOccupancy());
        entity.setCeilingHeight(createDto.getCeilingHeight());
        entity.setHasLoadingDock(createDto.getHasLoadingDock());

        // Set owner and building
        if (createDto.getOwnerId() != null) {
            entity.setOwner(ownerIdToOwner(createDto.getOwnerId()));
        }

        if (createDto.getBuildingId() != null) {
            entity.setBuilding(buildingIdToBuilding(createDto.getBuildingId()));
        }

        return entity;
    }

    public ComercialSpace updateFromDto(ComercialSpaceUpdateDto updateDto, ComercialSpace existingEntity) {
        if (updateDto == null) {
            return existingEntity;
        }

        // Update basic properties
        if (updateDto.getName() != null) {
            existingEntity.setName(updateDto.getName());
        }
        if (updateDto.getDescription() != null) {
            existingEntity.setDescription(updateDto.getDescription());
        }
        if (updateDto.getArea() != null) {
            existingEntity.setArea(updateDto.getArea());
        }
        if (updateDto.getPricePerMonth() != null) {
            existingEntity.setPricePerMonth(updateDto.getPricePerMonth());
        }
        if (updateDto.getAddress() != null) {
            existingEntity.setAddress(updateDto.getAddress());
        }
        if (updateDto.getLatitude() != null) {
            existingEntity.setLatitude(updateDto.getLatitude());
        }
        if (updateDto.getLongitude() != null) {
            existingEntity.setLongitude(updateDto.getLongitude());
        }
        if (updateDto.getAmenities() != null) {
            existingEntity.setAmenities(updateDto.getAmenities());
        }
        if (updateDto.getAvailable() != null) {
            existingEntity.setAvailable(updateDto.getAvailable());
        }

        // Update type-specific fields
        if (updateDto.getFloors() != null) {
            existingEntity.setFloors(updateDto.getFloors());
        }
        if (updateDto.getNumberOfRooms() != null) {
            existingEntity.setNumberOfRooms(updateDto.getNumberOfRooms());
        }
        if (updateDto.getHasReception() != null) {
            existingEntity.setHasReception(updateDto.getHasReception());
        }
        if (updateDto.getShopWindowSize() != null) {
            existingEntity.setShopWindowSize(updateDto.getShopWindowSize());
        }
        if (updateDto.getHasCustomerEntrance() != null) {
            existingEntity.setHasCustomerEntrance(updateDto.getHasCustomerEntrance());
        }
        if (updateDto.getMaxOccupancy() != null) {
            existingEntity.setMaxOccupancy(updateDto.getMaxOccupancy());
        }
        if (updateDto.getCeilingHeight() != null) {
            existingEntity.setCeilingHeight(updateDto.getCeilingHeight());
        }
        if (updateDto.getHasLoadingDock() != null) {
            existingEntity.setHasLoadingDock(updateDto.getHasLoadingDock());
        }
        if (updateDto.getSecurityLevel() != null) {
            existingEntity.setSecurityLevel(stringToSecurityLevel(updateDto.getSecurityLevel()));
        }

        return existingEntity;
    }

    // Helper methods
    private Owner ownerIdToOwner(Long ownerId) {
        if (ownerId == null) {
            return null;
        }
        return ownerRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("Owner not found with id: " + ownerId));
    }

    private Building buildingIdToBuilding(Long buildingId) {
        if (buildingId == null) {
            return null;
        }
        return buildingRepository.findById(buildingId)
                .orElseThrow(() -> new RuntimeException("Building not found with id: " + buildingId));
    }

    private ComercialSpace.SpaceType stringToSpaceType(String spaceType) {
        if (spaceType == null) {
            return null;
        }
        try {
            return ComercialSpace.SpaceType.valueOf(spaceType.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ComercialSpace.SpaceType.OFFICE;
        }
    }

    private ComercialSpace.SecurityLevel stringToSecurityLevel(String securityLevel) {
        if (securityLevel == null) {
            return ComercialSpace.SecurityLevel.MEDIUM;
        }
        try {
            return ComercialSpace.SecurityLevel.valueOf(securityLevel.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ComercialSpace.SecurityLevel.MEDIUM;
        }
    }
}
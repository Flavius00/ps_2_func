package com.example.demo.mapper;

import com.example.demo.dto.ComercialSpaceDto;
import com.example.demo.dto.ComercialSpaceCreateDto;
import com.example.demo.dto.ComercialSpaceUpdateDto;
import com.example.demo.model.ComercialSpace;
import com.example.demo.model.Owner;
import com.example.demo.model.Building;
import com.example.demo.repository.OwnerRepository;
import com.example.demo.repository.BuildingRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class ComercialSpaceMapper {

    @Autowired
    protected OwnerRepository ownerRepository;

    @Autowired
    protected BuildingRepository buildingRepository;

    // Entity to DTO mapping - folosește metodele helper pentru nested properties
    @Mapping(target = "ownerId", expression = "java(entity.getOwner() != null ? entity.getOwner().getId() : null)")
    @Mapping(target = "ownerName", expression = "java(entity.getOwner() != null ? entity.getOwner().getName() : null)")
    @Mapping(target = "ownerEmail", expression = "java(entity.getOwner() != null ? entity.getOwner().getEmail() : null)")
    @Mapping(target = "ownerPhone", expression = "java(entity.getOwner() != null ? entity.getOwner().getPhone() : null)")
    @Mapping(target = "ownerCompanyName", expression = "java(entity.getOwner() != null ? entity.getOwner().getCompanyName() : null)")
    @Mapping(target = "buildingId", expression = "java(entity.getBuilding() != null ? entity.getBuilding().getId() : null)")
    @Mapping(target = "buildingName", expression = "java(entity.getBuilding() != null ? entity.getBuilding().getName() : null)")
    @Mapping(target = "buildingAddress", expression = "java(entity.getBuilding() != null ? entity.getBuilding().getAddress() : null)")
    @Mapping(target = "buildingTotalFloors", expression = "java(entity.getBuilding() != null ? entity.getBuilding().getTotalFloors() : null)")
    @Mapping(target = "buildingYearBuilt", expression = "java(entity.getBuilding() != null ? entity.getBuilding().getYearBuilt() : null)")
    @Mapping(target = "parkingId", expression = "java(entity.getParking() != null ? entity.getParking().getId() : null)")
    @Mapping(target = "parkingSpots", expression = "java(entity.getParking() != null ? entity.getParking().getNumberOfSpots() : null)")
    @Mapping(target = "parkingPricePerSpot", expression = "java(entity.getParking() != null ? entity.getParking().getPricePerSpot() : null)")
    @Mapping(target = "parkingCovered", expression = "java(entity.getParking() != null ? entity.getParking().getCovered() : null)")
    @Mapping(target = "parkingType", expression = "java(entity.getParking() != null ? entity.getParking().getParkingType().name() : null)")
    @Mapping(target = "spaceType", expression = "java(entity.getSpaceType() != null ? entity.getSpaceType().name() : null)")
    @Mapping(target = "securityLevel", expression = "java(entity.getSecurityLevel() != null ? entity.getSecurityLevel().name() : null)")
    @Mapping(target = "contractsCount", expression = "java(entity.getContracts() != null ? entity.getContracts().size() : 0)")
    @Mapping(target = "hasActiveContract", expression = "java(hasActiveContract(entity))")
    public abstract ComercialSpaceDto toDto(ComercialSpace entity);

    // Create DTO to Entity mapping
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", expression = "java(ownerIdToOwner(createDto.getOwnerId()))")
    @Mapping(target = "building", expression = "java(buildingIdToBuilding(createDto.getBuildingId()))")
    @Mapping(target = "parking", ignore = true)
    @Mapping(target = "contracts", ignore = true)
    @Mapping(target = "spaceType", expression = "java(stringToSpaceType(createDto.getSpaceType()))")
    @Mapping(target = "securityLevel", expression = "java(stringToSecurityLevel(createDto.getSecurityLevel()))")
    public abstract ComercialSpace toEntity(ComercialSpaceCreateDto createDto);

    // Update DTO to Entity mapping (preserving existing owner and building)
    @Mapping(target = "owner", source = "existingEntity.owner")
    @Mapping(target = "building", source = "existingEntity.building")
    @Mapping(target = "parking", source = "existingEntity.parking")
    @Mapping(target = "contracts", source = "existingEntity.contracts")
    @Mapping(target = "spaceType", source = "existingEntity.spaceType")
    @Mapping(target = "securityLevel", expression = "java(updateDto.getSecurityLevel() != null ? stringToSecurityLevel(updateDto.getSecurityLevel()) : existingEntity.getSecurityLevel())")
    public abstract ComercialSpace updateFromDto(ComercialSpaceUpdateDto updateDto, @MappingTarget ComercialSpace existingEntity);

    // Helper methods
    protected Owner ownerIdToOwner(Long ownerId) {
        if (ownerId == null) {
            return null;
        }
        return ownerRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("Owner not found with id: " + ownerId));
    }

    protected Building buildingIdToBuilding(Long buildingId) {
        if (buildingId == null) {
            return null;
        }
        return buildingRepository.findById(buildingId)
                .orElseThrow(() -> new RuntimeException("Building not found with id: " + buildingId));
    }

    protected ComercialSpace.SpaceType stringToSpaceType(String spaceType) {
        if (spaceType == null) {
            return null;
        }
        try {
            return ComercialSpace.SpaceType.valueOf(spaceType.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ComercialSpace.SpaceType.OFFICE; // default value
        }
    }

    protected ComercialSpace.SecurityLevel stringToSecurityLevel(String securityLevel) {
        if (securityLevel == null) {
            return ComercialSpace.SecurityLevel.MEDIUM; // default value
        }
        try {
            return ComercialSpace.SecurityLevel.valueOf(securityLevel.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ComercialSpace.SecurityLevel.MEDIUM; // default value
        }
    }

    protected Boolean hasActiveContract(ComercialSpace entity) {
        if (entity.getContracts() == null || entity.getContracts().isEmpty()) {
            return false;
        }
        return entity.getContracts().stream()
                .anyMatch(contract ->
                        contract.getStatus() != null &&
                                contract.getStatus().name().equals("ACTIVE"));
    }
}
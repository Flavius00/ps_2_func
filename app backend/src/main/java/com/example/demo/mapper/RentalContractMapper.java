package com.example.demo.mapper;

import com.example.demo.dto.RentalContractDto;
import com.example.demo.dto.RentalContractCreateDto;
import com.example.demo.model.RentalContract;
import com.example.demo.model.ComercialSpace;
import com.example.demo.model.Tenant;
import com.example.demo.repository.ComercialSpaceRepository;
import com.example.demo.repository.TenantRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class RentalContractMapper {

    @Autowired
    protected ComercialSpaceRepository spaceRepository;

    @Autowired
    protected TenantRepository tenantRepository;

    // Entity to DTO mapping - folosește expresii Java pentru nested properties
    @Mapping(target = "spaceId", expression = "java(entity.getSpace() != null ? entity.getSpace().getId() : null)")
    @Mapping(target = "spaceName", expression = "java(entity.getSpace() != null ? entity.getSpace().getName() : null)")
    @Mapping(target = "spaceAddress", expression = "java(entity.getSpace() != null ? entity.getSpace().getAddress() : null)")
    @Mapping(target = "spaceArea", expression = "java(entity.getSpace() != null ? entity.getSpace().getArea() : null)")
    @Mapping(target = "spaceType", expression = "java(entity.getSpace() != null && entity.getSpace().getSpaceType() != null ? entity.getSpace().getSpaceType().name() : null)")
    @Mapping(target = "tenantId", expression = "java(entity.getTenant() != null ? entity.getTenant().getId() : null)")
    @Mapping(target = "tenantName", expression = "java(entity.getTenant() != null ? entity.getTenant().getName() : null)")
    @Mapping(target = "tenantEmail", expression = "java(entity.getTenant() != null ? entity.getTenant().getEmail() : null)")
    @Mapping(target = "tenantPhone", expression = "java(entity.getTenant() != null ? entity.getTenant().getPhone() : null)")
    @Mapping(target = "tenantCompanyName", expression = "java(entity.getTenant() != null ? entity.getTenant().getCompanyName() : null)")
    @Mapping(target = "ownerId", expression = "java(entity.getSpace() != null && entity.getSpace().getOwner() != null ? entity.getSpace().getOwner().getId() : null)")
    @Mapping(target = "ownerName", expression = "java(entity.getSpace() != null && entity.getSpace().getOwner() != null ? entity.getSpace().getOwner().getName() : null)")
    @Mapping(target = "ownerEmail", expression = "java(entity.getSpace() != null && entity.getSpace().getOwner() != null ? entity.getSpace().getOwner().getEmail() : null)")
    @Mapping(target = "buildingId", expression = "java(entity.getSpace() != null && entity.getSpace().getBuilding() != null ? entity.getSpace().getBuilding().getId() : null)")
    @Mapping(target = "buildingName", expression = "java(entity.getSpace() != null && entity.getSpace().getBuilding() != null ? entity.getSpace().getBuilding().getName() : null)")
    @Mapping(target = "status", expression = "java(entity.getStatus() != null ? entity.getStatus().name() : null)")
    public abstract RentalContractDto toDto(RentalContract entity);

    // Create DTO to Entity mapping
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "space", expression = "java(spaceIdToSpace(createDto.getSpaceId()))")
    @Mapping(target = "tenant", expression = "java(tenantIdToTenant(createDto.getTenantId()))")
    @Mapping(target = "status", expression = "java(stringToContractStatus(createDto.getStatus()))")
    @Mapping(target = "dateCreated", expression = "java(createDto.getDateCreated() != null ? createDto.getDateCreated() : java.time.LocalDate.now())")
    public abstract RentalContract toEntity(RentalContractCreateDto createDto);

    // Full DTO to Entity mapping (for updates)
    @Mapping(target = "space", expression = "java(spaceIdToSpace(dto.getSpaceId()))")
    @Mapping(target = "tenant", expression = "java(tenantIdToTenant(dto.getTenantId()))")
    @Mapping(target = "status", expression = "java(stringToContractStatus(dto.getStatus()))")
    public abstract RentalContract toEntity(RentalContractDto dto);

    // Helper methods
    protected ComercialSpace spaceIdToSpace(Long spaceId) {
        if (spaceId == null) {
            return null;
        }
        return spaceRepository.findById(spaceId)
                .orElseThrow(() -> new RuntimeException("Space not found with id: " + spaceId));
    }

    protected Tenant tenantIdToTenant(Long tenantId) {
        if (tenantId == null) {
            return null;
        }
        return tenantRepository.findById(tenantId)
                .orElseThrow(() -> new RuntimeException("Tenant not found with id: " + tenantId));
    }

    protected RentalContract.ContractStatus stringToContractStatus(String status) {
        if (status == null) {
            return RentalContract.ContractStatus.ACTIVE; // default value
        }
        try {
            return RentalContract.ContractStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return RentalContract.ContractStatus.ACTIVE; // default value
        }
    }
}
package com.example.demo.mapper;

import com.example.demo.dto.RentalContractDto;
import com.example.demo.dto.RentalContractCreateDto;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.RentalContract;
import com.example.demo.model.ComercialSpace;
import com.example.demo.model.Tenant;
import com.example.demo.model.User;
import com.example.demo.repository.ComercialSpaceRepository;
import com.example.demo.repository.TenantRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

@Component
public class RentalContractMapper {

    @Autowired
    private ComercialSpaceRepository spaceRepository;

    @Autowired
    private TenantRepository tenantRepository;

    public RentalContractDto toDto(RentalContract entity) {
        if (entity == null) {
            return null;
        }

        RentalContractDto dto = new RentalContractDto();

        // Basic properties
        dto.setId(entity.getId());
        dto.setStartDate(entity.getStartDate());
        dto.setEndDate(entity.getEndDate());
        dto.setMonthlyRent(entity.getMonthlyRent());
        dto.setSecurityDeposit(entity.getSecurityDeposit());
        dto.setIsPaid(entity.getIsPaid());
        dto.setDateCreated(entity.getDateCreated());
        dto.setContractNumber(entity.getContractNumber());
        dto.setNotes(entity.getNotes());

        // Status
        if (entity.getStatus() != null) {
            dto.setStatus(entity.getStatus().name());
        }

        // Space information
        if (entity.getSpace() != null) {
            dto.setSpaceId(entity.getSpace().getId());
            dto.setSpaceName(entity.getSpace().getName());
            dto.setSpaceAddress(entity.getSpace().getAddress());
            dto.setSpaceArea(entity.getSpace().getArea());
            if (entity.getSpace().getSpaceType() != null) {
                dto.setSpaceType(entity.getSpace().getSpaceType().name());
            }

            // Owner information through space
            if (entity.getSpace().getOwner() != null) {
                dto.setOwnerId(entity.getSpace().getOwner().getId());
                dto.setOwnerName(entity.getSpace().getOwner().getName());
                dto.setOwnerEmail(entity.getSpace().getOwner().getEmail());
            }

            // Building information through space
            if (entity.getSpace().getBuilding() != null) {
                dto.setBuildingId(entity.getSpace().getBuilding().getId());
                dto.setBuildingName(entity.getSpace().getBuilding().getName());
            }
        }

        // Tenant information
        if (entity.getTenant() != null) {
            dto.setTenantId(entity.getTenant().getId());
            dto.setTenantName(entity.getTenant().getName());
            dto.setTenantEmail(entity.getTenant().getEmail());
            dto.setTenantPhone(entity.getTenant().getPhone());
            dto.setTenantCompanyName(entity.getTenant().getCompanyName());
        }

        return dto;
    }

    public RentalContract toEntity(RentalContractCreateDto createDto) {
        if (createDto == null) {
            return null;
        }

        RentalContract entity = new RentalContract();

        // Basic properties
        entity.setStartDate(createDto.getStartDate());
        entity.setEndDate(createDto.getEndDate());
        entity.setMonthlyRent(createDto.getMonthlyRent());
        entity.setSecurityDeposit(createDto.getSecurityDeposit());
        entity.setIsPaid(createDto.getIsPaid());
        entity.setContractNumber(createDto.getContractNumber());
        entity.setNotes(createDto.getNotes());

        // Set date created
        if (createDto.getDateCreated() != null) {
            entity.setDateCreated(createDto.getDateCreated());
        } else {
            entity.setDateCreated(LocalDate.now());
        }

        // Set status
        if (createDto.getStatus() != null) {
            entity.setStatus(stringToContractStatus(createDto.getStatus()));
        } else {
            entity.setStatus(RentalContract.ContractStatus.ACTIVE);
        }

        // Set space and tenant
        if (createDto.getSpaceId() != null) {
            entity.setSpace(spaceIdToSpace(createDto.getSpaceId()));
        }

        if (createDto.getTenantId() != null) {
            entity.setTenant(tenantIdToTenant(createDto.getTenantId()));
        }

        return entity;
    }

    public RentalContract toEntity(RentalContractDto dto) {
        if (dto == null) {
            return null;
        }

        RentalContract entity = new RentalContract();

        entity.setId(dto.getId());
        entity.setStartDate(dto.getStartDate());
        entity.setEndDate(dto.getEndDate());
        entity.setMonthlyRent(dto.getMonthlyRent());
        entity.setSecurityDeposit(dto.getSecurityDeposit());
        entity.setIsPaid(dto.getIsPaid());
        entity.setDateCreated(dto.getDateCreated());
        entity.setContractNumber(dto.getContractNumber());
        entity.setNotes(dto.getNotes());

        if (dto.getStatus() != null) {
            entity.setStatus(stringToContractStatus(dto.getStatus()));
        }

        if (dto.getSpaceId() != null) {
            entity.setSpace(spaceIdToSpace(dto.getSpaceId()));
        }

        if (dto.getTenantId() != null) {
            entity.setTenant(tenantIdToTenant(dto.getTenantId()));
        }

        return entity;
    }

    // Helper methods
    private ComercialSpace spaceIdToSpace(Long spaceId) {
        if (spaceId == null) {
            return null;
        }
        return spaceRepository.findById(spaceId)
                .orElseThrow(() -> new RuntimeException("Space not found with id: " + spaceId));
    }

    @Autowired
    private UserRepository userRepository;

    private Tenant tenantIdToTenant(Long tenantId) {
        if (tenantId == null) {
            return null;
        }

        // Caută utilizatorul în repository-ul general
        User user = userRepository.findById(tenantId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + tenantId));

        // Verifică dacă are rolul de TENANT
        if (user.getRole() != User.UserRole.TENANT) {
            throw new RuntimeException("User with id " + tenantId + " is not a tenant. Role: " + user.getRole());
        }

        // Dacă este deja o instanță de Tenant, returnează-l
        if (user instanceof Tenant) {
            return (Tenant) user;
        }

        // Altfel, caută în TenantRepository (ar trebui să funcționeze după corectarea datelor)
        return tenantRepository.findById(tenantId)
                .orElseThrow(() -> new RuntimeException("Tenant entity not found for user id: " + tenantId +
                        ". User_type inconsistency detected."));
    }

    private RentalContract.ContractStatus stringToContractStatus(String status) {
        if (status == null) {
            return RentalContract.ContractStatus.ACTIVE;
        }
        try {
            return RentalContract.ContractStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return RentalContract.ContractStatus.ACTIVE;
        }
    }
}
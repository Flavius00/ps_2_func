package com.example.demo.service.impl;

import com.example.demo.exception.*;
import com.example.demo.mapper.RentalContractMapper;
import com.example.demo.model.ComercialSpace;
import com.example.demo.model.RentalContract;
import com.example.demo.model.Tenant;
import com.example.demo.repository.RentalContractRepository;
import com.example.demo.repository.ComercialSpaceRepository;
import com.example.demo.repository.TenantRepository;
import com.example.demo.service.RentalContractService;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class RentalContractServiceImpl implements RentalContractService {
    private final RentalContractRepository contractRepository;
    private final ComercialSpaceRepository spaceRepository;
    private final TenantRepository tenantRepository;
    private final RentalContractMapper contractMapper;

    public RentalContractServiceImpl(RentalContractRepository contractRepository,
                                     ComercialSpaceRepository spaceRepository,
                                     TenantRepository tenantRepository,
                                     RentalContractMapper contractMapper) {
        this.contractRepository = contractRepository;
        this.spaceRepository = spaceRepository;
        this.tenantRepository = tenantRepository;
        this.contractMapper = contractMapper;
    }

    @Override
    public RentalContract createContract(RentalContract contract) {
        try {
            validateContractForCreation(contract);

            ComercialSpace space = spaceRepository.findById(contract.getSpace().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Commercial space not found with id: " + contract.getSpace().getId()));

            if (!space.getAvailable()) {
                throw new SpaceAlreadyRentedException(space.getName());
            }

            Tenant tenant = tenantRepository.findById(contract.getTenant().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with id: " + contract.getTenant().getId()));

            // Check for overlapping contracts
            checkForOverlappingContracts(space.getId(), contract.getStartDate(), contract.getEndDate(), null);

            // Set default values
            if (contract.getContractNumber() == null || contract.getContractNumber().isEmpty()) {
                contract.setContractNumber("RENT-" + System.currentTimeMillis());
            }

            if (contract.getDateCreated() == null) {
                contract.setDateCreated(LocalDate.now());
            }

            if (contract.getStatus() == null) {
                contract.setStatus(RentalContract.ContractStatus.ACTIVE);
            }

            contract.setSpace(space);
            contract.setTenant(tenant);

            RentalContract savedContract = contractRepository.save(contract);

            // Update space availability
            space.setAvailable(false);
            spaceRepository.save(space);

            return savedContract;

        } catch (DataAccessException ex) {
            throw new DatabaseOperationException("create contract", "Failed to create rental contract", ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<RentalContract> getAllContracts() {
        try {
            return contractRepository.findAll();
        } catch (DataAccessException ex) {
            throw new DatabaseOperationException("fetch all contracts", "Failed to retrieve rental contracts", ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public RentalContract getContractById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Contract ID must be a positive number");
        }

        try {
            return contractRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Contract not found with id: " + id));
        } catch (DataAccessException ex) {
            throw new DatabaseOperationException("fetch contract by ID", "Failed to retrieve rental contract", ex);
        }
    }

    @Override
    public RentalContract updateContract(RentalContract contract) {
        if (contract.getId() == null) {
            throw new IllegalArgumentException("Contract ID cannot be null for update operation");
        }

        try {
            RentalContract existingContract = contractRepository.findById(contract.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Contract not found with id: " + contract.getId()));

            validateContractForUpdate(contract, existingContract);

            return contractRepository.save(contract);

        } catch (DataAccessException ex) {
            throw new DatabaseOperationException("update contract", "Failed to update rental contract", ex);
        }
    }

    @Override
    public void terminateContract(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Contract ID must be a positive number");
        }

        try {
            RentalContract contract = contractRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Contract not found with id: " + id));

            if (contract.getStatus() == RentalContract.ContractStatus.TERMINATED) {
                throw new InvalidOperationException("terminate contract", "Contract is already terminated");
            }

            contract.setStatus(RentalContract.ContractStatus.TERMINATED);
            contractRepository.save(contract);

            // Update space availability
            if (contract.getSpace() != null) {
                ComercialSpace space = contract.getSpace();
                space.setAvailable(true);
                spaceRepository.save(space);
            }

        } catch (DataAccessException ex) {
            throw new DatabaseOperationException("terminate contract", "Failed to terminate rental contract", ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<RentalContract> getContractsByTenant(Long tenantId) {
        if (tenantId == null || tenantId <= 0) {
            throw new IllegalArgumentException("Tenant ID must be a positive number");
        }

        // Verify tenant exists
        if (!tenantRepository.existsById(tenantId)) {
            throw new ResourceNotFoundException("Tenant not found with id: " + tenantId);
        }

        try {
            return contractRepository.findByTenantId(tenantId);
        } catch (DataAccessException ex) {
            throw new DatabaseOperationException("fetch contracts by tenant", "Failed to retrieve contracts by tenant", ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<RentalContract> getContractsByOwner(Long ownerId) {
        if (ownerId == null || ownerId <= 0) {
            throw new IllegalArgumentException("Owner ID must be a positive number");
        }

        try {
            return contractRepository.findByOwnerId(ownerId);
        } catch (DataAccessException ex) {
            throw new DatabaseOperationException("fetch contracts by owner", "Failed to retrieve contracts by owner", ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<RentalContract> getContractsBySpace(Long spaceId) {
        if (spaceId == null || spaceId <= 0) {
            throw new IllegalArgumentException("Space ID must be a positive number");
        }

        try {
            return contractRepository.findBySpaceId(spaceId);
        } catch (DataAccessException ex) {
            throw new DatabaseOperationException("fetch contracts by space", "Failed to retrieve contracts by space", ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<RentalContract> getContractsByStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Contract status cannot be null or empty");
        }

        try {
            RentalContract.ContractStatus contractStatus = RentalContract.ContractStatus.valueOf(status.toUpperCase());
            return contractRepository.findByStatus(contractStatus);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid contract status: " + status +
                    ". Valid statuses are: ACTIVE, EXPIRED, TERMINATED, PENDING");
        } catch (DataAccessException ex) {
            throw new DatabaseOperationException("fetch contracts by status", "Failed to retrieve contracts by status", ex);
        }
    }

    @Override
    public RentalContract renewContract(Long contractId, RentalContract renewalDetails) {
        if (contractId == null || contractId <= 0) {
            throw new IllegalArgumentException("Contract ID must be a positive number");
        }

        try {
            RentalContract existingContract = contractRepository.findById(contractId)
                    .orElseThrow(() -> new ResourceNotFoundException("Contract not found with id: " + contractId));

            if (existingContract.getStatus() != RentalContract.ContractStatus.ACTIVE &&
                    existingContract.getStatus() != RentalContract.ContractStatus.EXPIRED) {
                throw new InvalidOperationException("renew contract",
                        "Only active or expired contracts can be renewed");
            }

            validateContractRenewal(renewalDetails, existingContract);

            // Create new contract
            RentalContract newContract = RentalContract.builder()
                    .tenant(existingContract.getTenant())
                    .space(existingContract.getSpace())
                    .startDate(renewalDetails.getStartDate() != null ?
                            renewalDetails.getStartDate() : LocalDate.now())
                    .endDate(renewalDetails.getEndDate())
                    .monthlyRent(renewalDetails.getMonthlyRent() != null ?
                            renewalDetails.getMonthlyRent() : existingContract.getMonthlyRent())
                    .securityDeposit(renewalDetails.getSecurityDeposit() != null ?
                            renewalDetails.getSecurityDeposit() : existingContract.getSecurityDeposit())
                    .status(RentalContract.ContractStatus.ACTIVE)
                    .isPaid(false)
                    .dateCreated(LocalDate.now())
                    .contractNumber("RENEWAL-" + existingContract.getContractNumber())
                    .notes(renewalDetails.getNotes())
                    .build();

            // Update existing contract status
            existingContract.setStatus(RentalContract.ContractStatus.EXPIRED);
            contractRepository.save(existingContract);

            return contractRepository.save(newContract);

        } catch (DataAccessException ex) {
            throw new DatabaseOperationException("renew contract", "Failed to renew rental contract", ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<RentalContract> getExpiredContracts() {
        try {
            return contractRepository.findExpiredContracts(LocalDate.now());
        } catch (DataAccessException ex) {
            throw new DatabaseOperationException("fetch expired contracts", "Failed to retrieve expired contracts", ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<RentalContract> getContractsExpiringInDays(int days) {
        if (days < 0) {
            throw new IllegalArgumentException("Days must be non-negative");
        }

        try {
            LocalDate startDate = LocalDate.now();
            LocalDate endDate = startDate.plusDays(days);
            return contractRepository.findContractsExpiringBetween(startDate, endDate);
        } catch (DataAccessException ex) {
            throw new DatabaseOperationException("fetch expiring contracts", "Failed to retrieve expiring contracts", ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Double getTotalActiveMonthlyRevenue() {
        try {
            Double revenue = contractRepository.getTotalActiveMonthlyRevenue();
            return revenue != null ? revenue : 0.0;
        } catch (DataAccessException ex) {
            throw new DatabaseOperationException("calculate revenue", "Failed to calculate total monthly revenue", ex);
        }
    }

    // Private validation methods
    private void validateContractForCreation(RentalContract contract) {
        if (contract == null) {
            throw new IllegalArgumentException("Rental contract cannot be null");
        }

        if (contract.getSpace() == null || contract.getSpace().getId() == null) {
            throw new IllegalArgumentException("Space is required for creating a contract");
        }

        if (contract.getTenant() == null || contract.getTenant().getId() == null) {
            throw new IllegalArgumentException("Tenant is required for creating a contract");
        }

        validateContractDates(contract.getStartDate(), contract.getEndDate());

        if (contract.getMonthlyRent() == null || contract.getMonthlyRent() <= 0) {
            throw new IllegalArgumentException("Monthly rent must be positive");
        }

        if (contract.getSecurityDeposit() != null && contract.getSecurityDeposit() < 0) {
            throw new IllegalArgumentException("Security deposit cannot be negative");
        }
    }

    private void validateContractForUpdate(RentalContract contract, RentalContract existingContract) {
        if (contract == null) {
            throw new IllegalArgumentException("Rental contract cannot be null");
        }

        if (contract.getStartDate() != null && contract.getEndDate() != null) {
            validateContractDates(contract.getStartDate(), contract.getEndDate());
        }

        if (contract.getMonthlyRent() != null && contract.getMonthlyRent() <= 0) {
            throw new IllegalArgumentException("Monthly rent must be positive");
        }

        if (contract.getSecurityDeposit() != null && contract.getSecurityDeposit() < 0) {
            throw new IllegalArgumentException("Security deposit cannot be negative");
        }

        // Check if trying to change space or tenant (not allowed)
        if (contract.getSpace() != null && !contract.getSpace().getId().equals(existingContract.getSpace().getId())) {
            throw new InvalidOperationException("update contract", "Cannot change space in existing contract");
        }

        if (contract.getTenant() != null && !contract.getTenant().getId().equals(existingContract.getTenant().getId())) {
            throw new InvalidOperationException("update contract", "Cannot change tenant in existing contract");
        }
    }

    private void validateContractRenewal(RentalContract renewalDetails, RentalContract existingContract) {
        if (renewalDetails.getEndDate() == null) {
            throw new IllegalArgumentException("End date is required for contract renewal");
        }

        LocalDate startDate = renewalDetails.getStartDate() != null ?
                renewalDetails.getStartDate() : LocalDate.now();

        validateContractDates(startDate, renewalDetails.getEndDate());

        if (renewalDetails.getMonthlyRent() != null && renewalDetails.getMonthlyRent() <= 0) {
            throw new IllegalArgumentException("Monthly rent must be positive");
        }

        if (renewalDetails.getSecurityDeposit() != null && renewalDetails.getSecurityDeposit() < 0) {
            throw new IllegalArgumentException("Security deposit cannot be negative");
        }
    }

    private void validateContractDates(LocalDate startDate, LocalDate endDate) {
        if (startDate == null) {
            throw new ContractValidationException("Start date cannot be null");
        }

        if (endDate == null) {
            throw new ContractValidationException("End date cannot be null");
        }

        if (endDate.isBefore(startDate) || endDate.isEqual(startDate)) {
            throw ContractValidationException.invalidDateRange();
        }

        // Check minimum duration (1 month)
        if (startDate.plusMonths(1).isAfter(endDate)) {
            throw ContractValidationException.contractTooShort();
        }

        // Check maximum duration (5 years)
        if (startDate.plusYears(5).isBefore(endDate)) {
            throw ContractValidationException.contractTooLong();
        }

        // Check if start date is too far in the past
        if (startDate.isBefore(LocalDate.now().minusMonths(1))) {
            throw new ContractValidationException("Start date cannot be more than 1 month in the past");
        }
    }

    private void checkForOverlappingContracts(Long spaceId, LocalDate startDate, LocalDate endDate, Long excludeContractId) {
        try {
            List<RentalContract> existingContracts = contractRepository.findBySpaceId(spaceId);

            for (RentalContract existing : existingContracts) {
                // Skip the contract being updated
                if (excludeContractId != null && existing.getId().equals(excludeContractId)) {
                    continue;
                }

                // Skip terminated contracts
                if (existing.getStatus() == RentalContract.ContractStatus.TERMINATED) {
                    continue;
                }

                // Check for overlap
                if (datesOverlap(existing.getStartDate(), existing.getEndDate(), startDate, endDate)) {
                    throw new InvalidOperationException("create/update contract",
                            "Contract dates overlap with existing contract #" + existing.getContractNumber());
                }
            }
        } catch (DataAccessException ex) {
            throw new DatabaseOperationException("check overlapping contracts", "Failed to check for overlapping contracts", ex);
        }
    }

    private boolean datesOverlap(LocalDate start1, LocalDate end1, LocalDate start2, LocalDate end2) {
        return start1.isBefore(end2) && start2.isBefore(end1);
    }
}
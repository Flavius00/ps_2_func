package com.example.demo.service.impl;

import com.example.demo.model.ComercialSpace;
import com.example.demo.model.RentalContract;
import com.example.demo.repository.RentalContractRepository;
import com.example.demo.repository.ComercialSpaceRepository;
import com.example.demo.service.RentalContractService;
import com.example.demo.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class RentalContractServiceImpl implements RentalContractService {
    private final RentalContractRepository contractRepository;
    private final ComercialSpaceRepository spaceRepository;

    public RentalContractServiceImpl(RentalContractRepository contractRepository,
                                     ComercialSpaceRepository spaceRepository) {
        this.contractRepository = contractRepository;
        this.spaceRepository = spaceRepository;
    }

    @Override
    public RentalContract createContract(RentalContract contract) {
        // Generate a contract number if not provided
        if (contract.getContractNumber() == null || contract.getContractNumber().isEmpty()) {
            contract.setContractNumber("RENT-" + System.currentTimeMillis());
        }

        // Set the creation date to now if not provided
        if (contract.getDateCreated() == null) {
            contract.setDateCreated(LocalDate.now());
        }

        // Set default status to ACTIVE if not provided
        if (contract.getStatus() == null) {
            contract.setStatus(RentalContract.ContractStatus.ACTIVE);
        }

        // Save the contract first
        RentalContract savedContract = contractRepository.save(contract);

        // Mark the space as unavailable
        if (contract.getSpace() != null) {
            ComercialSpace space = contract.getSpace();
            space.setAvailable(false);
            spaceRepository.save(space);
        }

        return savedContract;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RentalContract> getAllContracts() {
        return contractRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public RentalContract getContractById(Long id) {
        return contractRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contract not found with id: " + id));
    }

    @Override
    public RentalContract updateContract(RentalContract contract) {
        if (!contractRepository.existsById(contract.getId())) {
            throw new ResourceNotFoundException("Contract not found with id: " + contract.getId());
        }
        return contractRepository.save(contract);
    }

    @Override
    public void terminateContract(Long id) {
        RentalContract contract = getContractById(id);
        contract.setStatus(RentalContract.ContractStatus.TERMINATED);
        contractRepository.save(contract);

        // Make the space available again
        if (contract.getSpace() != null) {
            ComercialSpace space = contract.getSpace();
            space.setAvailable(true);
            spaceRepository.save(space);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<RentalContract> getContractsByTenant(Long tenantId) {
        return contractRepository.findByTenantId(tenantId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RentalContract> getContractsByOwner(Long ownerId) {
        return contractRepository.findByOwnerId(ownerId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RentalContract> getContractsBySpace(Long spaceId) {
        return contractRepository.findBySpaceId(spaceId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RentalContract> getContractsByStatus(String status) {
        try {
            RentalContract.ContractStatus contractStatus = RentalContract.ContractStatus.valueOf(status.toUpperCase());
            return contractRepository.findByStatus(contractStatus);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid contract status: " + status);
        }
    }

    @Override
    public RentalContract renewContract(Long contractId, RentalContract renewalDetails) {
        RentalContract existingContract = getContractById(contractId);

        // Create a new contract based on the existing one with new dates
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

        // Mark old contract as expired
        existingContract.setStatus(RentalContract.ContractStatus.EXPIRED);
        contractRepository.save(existingContract);

        return contractRepository.save(newContract);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RentalContract> getExpiredContracts() {
        return contractRepository.findExpiredContracts(LocalDate.now());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RentalContract> getContractsExpiringInDays(int days) {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(days);
        return contractRepository.findContractsExpiringBetween(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public Double getTotalActiveMonthlyRevenue() {
        return contractRepository.getTotalActiveMonthlyRevenue();
    }
}
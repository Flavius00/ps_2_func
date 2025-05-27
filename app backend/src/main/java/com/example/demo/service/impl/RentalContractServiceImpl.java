package com.example.demo.service.impl;

import com.example.demo.model.ComercialSpace;
import com.example.demo.model.RentalContract;
import com.example.demo.repository.RentalContractRepository;
import com.example.demo.repository.ComercialSpaceRepository;
import com.example.demo.service.RentalContractService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
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
        // Generate a contract number
        contract.setContractNumber("RENT-" + System.currentTimeMillis());

        // Set the creation date to now
        contract.setDateCreated(LocalDate.now());

        // Set default status to ACTIVE
        if (contract.getStatus() == null) {
            contract.setStatus("ACTIVE");
        }

        // Mark the space as unavailable
        ComercialSpace space = contract.getSpace();
        if (space != null) {
            space.setAvailable(false);
            spaceRepository.update(space);
        }

        return contractRepository.save(contract);
    }

    @Override
    public List<RentalContract> getAllContracts() {
        return contractRepository.findAll();
    }

    @Override
    public RentalContract getContractById(Long id) {
        return contractRepository.findById(id);
    }

    @Override
    public RentalContract updateContract(RentalContract contract) {
        return contractRepository.update(contract);
    }

    @Override
    public void terminateContract(Long id) {
        RentalContract contract = contractRepository.findById(id);
        if (contract != null) {
            contract.setStatus("TERMINATED");
            contractRepository.update(contract);

            // Make the space available again
            ComercialSpace space = contract.getSpace();
            if (space != null) {
                space.setAvailable(true);
                spaceRepository.update(space);
            }
        }
    }

    @Override
    public List<RentalContract> getContractsByTenant(Long tenantId) {
        return contractRepository.findAll().stream()
                .filter(contract -> contract.getTenant() != null
                        && contract.getTenant().getId().equals(tenantId))
                .collect(Collectors.toList());
    }

    @Override
    public List<RentalContract> getContractsByOwner(Long ownerId) {
        return contractRepository.findAll().stream()
                .filter(contract -> contract.getSpace().getOwner() != null
                        && contract.getSpace().getOwner().getId().equals(ownerId))
                .collect(Collectors.toList());
    }

    @Override
    public List<RentalContract> getContractsBySpace(Long spaceId) {
        return contractRepository.findAll().stream()
                .filter(contract -> contract.getSpace() != null
                        && contract.getSpace().getId().equals(spaceId))
                .collect(Collectors.toList());
    }

    @Override
    public List<RentalContract> getContractsByStatus(String status) {
        return contractRepository.findAll().stream()
                .filter(contract -> status.equalsIgnoreCase(contract.getStatus()))
                .collect(Collectors.toList());
    }

    @Override
    public RentalContract renewContract(Long contractId, RentalContract renewalDetails) {
        RentalContract existingContract = contractRepository.findById(contractId);
        if (existingContract == null) {
            throw new IllegalArgumentException("Contract not found with ID: " + contractId);
        }

        // Create a new contract based on the existing one with new dates
        RentalContract newContract = new RentalContract();
        newContract.setTenant(existingContract.getTenant());
        newContract.setSpace(existingContract.getSpace());
        newContract.setStartDate(renewalDetails.getStartDate() != null ?
                renewalDetails.getStartDate() : LocalDate.now());
        newContract.setEndDate(renewalDetails.getEndDate());
        newContract.setMonthlyRent(renewalDetails.getMonthlyRent() != null ?
                renewalDetails.getMonthlyRent() : existingContract.getMonthlyRent());
        newContract.setSecurityDeposit(renewalDetails.getSecurityDeposit() != null ?
                renewalDetails.getSecurityDeposit() : existingContract.getSecurityDeposit());
        newContract.setStatus("ACTIVE");
        newContract.setIsPaid(false);
        newContract.setDateCreated(LocalDate.now());
        newContract.setContractNumber("RENEWAL-" + existingContract.getContractNumber());

        // Mark old contract as expired
        existingContract.setStatus("EXPIRED");
        contractRepository.update(existingContract);

        return contractRepository.save(newContract);
    }
}
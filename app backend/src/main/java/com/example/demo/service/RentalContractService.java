package com.example.demo.service;

import com.example.demo.model.RentalContract;
import java.util.List;

public interface RentalContractService {
    RentalContract createContract(RentalContract contract);
    List<RentalContract> getAllContracts();
    RentalContract getContractById(Long id);
    RentalContract updateContract(RentalContract contract);
    void terminateContract(Long id);
    List<RentalContract> getContractsByTenant(Long tenantId);
    List<RentalContract> getContractsByOwner(Long ownerId);
    List<RentalContract> getContractsBySpace(Long spaceId);
    List<RentalContract> getContractsByStatus(String status);
    RentalContract renewContract(Long contractId, RentalContract renewalDetails);

    // Metode noi pentru JPA
    List<RentalContract> getExpiredContracts();
    List<RentalContract> getContractsExpiringInDays(int days);
    Double getTotalActiveMonthlyRevenue();
}
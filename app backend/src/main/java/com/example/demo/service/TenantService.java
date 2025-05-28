package com.example.demo.service;

import com.example.demo.model.Tenant;
import com.example.demo.model.ComercialSpace;
import com.example.demo.model.RentalContract;
import java.util.List;

public interface TenantService {
    // Operații CRUD de bază pentru Tenant
    Tenant addTenant(Tenant tenant);
    List<Tenant> getAllTenants();
    Tenant getTenantById(Long id);
    Tenant updateTenant(Tenant tenant);
    void deleteTenant(Long id);

    // Operații pentru managementul contractelor și spațiilor
    List<ComercialSpace> getTenantRentedSpaces(Long tenantId);
    List<RentalContract> getTenantContracts(Long tenantId);
    List<RentalContract> getTenantActiveContracts(Long tenantId);
    Double getTenantMonthlyExpenses(Long tenantId);
    boolean hasActiveContractForSpace(Long tenantId, Long spaceId);
    List<Tenant> getTenantsWithActiveContracts();


}
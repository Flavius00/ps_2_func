package com.example.demo.service.impl;

import com.example.demo.model.Tenant;
import com.example.demo.model.ComercialSpace;
import com.example.demo.model.RentalContract;
import com.example.demo.repository.TenantRepository;
import com.example.demo.repository.RentalContractRepository;
import com.example.demo.service.TenantService;
import com.example.demo.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TenantServiceImpl implements TenantService {

    private final TenantRepository tenantRepository;
    private final RentalContractRepository contractRepository;

    public TenantServiceImpl(TenantRepository tenantRepository,
                             RentalContractRepository contractRepository) {
        this.tenantRepository = tenantRepository;
        this.contractRepository = contractRepository;
    }

    // Metodele de bază pentru Tenant
    @Override
    public Tenant addTenant(Tenant tenant) {
        return tenantRepository.save(tenant);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Tenant> getAllTenants() {
        return tenantRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Tenant getTenantById(Long id) {
        return tenantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with id: " + id));
    }

    @Override
    public Tenant updateTenant(Tenant tenant) {
        if (!tenantRepository.existsById(tenant.getId())) {
            throw new ResourceNotFoundException("Tenant not found with id: " + tenant.getId());
        }
        return tenantRepository.save(tenant);
    }

    @Override
    public void deleteTenant(Long id) {
        if (!tenantRepository.existsById(id)) {
            throw new ResourceNotFoundException("Tenant not found with id: " + id);
        }

        // Verifică dacă tenant-ul are contracte active
        long activeContractsCount = contractRepository.findActiveContractsByTenantId(id).size();
        if (activeContractsCount > 0) {
            throw new IllegalStateException("Cannot delete tenant with active contracts. " +
                    "Please terminate all contracts first.");
        }

        tenantRepository.deleteById(id);
    }

    // Metodele pentru managementul contractelor și spațiilor
    /**
     * Obține toate spațiile închiriate de un tenant prin contractele active
     * NOTA: Nu mai folosim tenant.getRentedSpaces() deoarece nu există asemenea listă în Tenant
     * În schimb, obținem spațiile prin contractele active
     */
    @Override
    @Transactional(readOnly = true)
    public List<ComercialSpace> getTenantRentedSpaces(Long tenantId) {
        return contractRepository.findActiveContractsByTenantId(tenantId)
                .stream()
                .map(RentalContract::getSpace)
                .collect(Collectors.toList());
    }

    /**
     * Obține toate contractele unui tenant
     */
    @Override
    @Transactional(readOnly = true)
    public List<RentalContract> getTenantContracts(Long tenantId) {
        return contractRepository.findByTenantId(tenantId);
    }

    /**
     * Obține doar contractele active ale unui tenant
     */
    @Override
    @Transactional(readOnly = true)
    public List<RentalContract> getTenantActiveContracts(Long tenantId) {
        return contractRepository.findActiveContractsByTenantId(tenantId);
    }

    /**
     * Calculează costul total lunar pentru un tenant
     */
    @Override
    @Transactional(readOnly = true)
    public Double getTenantMonthlyExpenses(Long tenantId) {
        return contractRepository.findActiveContractsByTenantId(tenantId)
                .stream()
                .mapToDouble(RentalContract::getMonthlyRent)
                .sum();
    }

    /**
     * Verifică dacă un tenant are deja un contract activ pentru un anumit spațiu
     */
    @Override
    @Transactional(readOnly = true)
    public boolean hasActiveContractForSpace(Long tenantId, Long spaceId) {
        return contractRepository.findActiveContractsByTenantId(tenantId)
                .stream()
                .anyMatch(contract -> contract.getSpace().getId().equals(spaceId));
    }

    /**
     * Obține toți tenant-ii care au contracte active
     */
    @Override
    @Transactional(readOnly = true)
    public List<Tenant> getTenantsWithActiveContracts() {
        return tenantRepository.findTenantsWithActiveContracts();
    }
}
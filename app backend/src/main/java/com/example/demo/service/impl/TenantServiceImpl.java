package com.example.demo.service.impl;

import com.example.demo.mapper.UserMapper;
import com.example.demo.mapper.ComercialSpaceMapper;
import com.example.demo.mapper.RentalContractMapper;
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
    private final UserMapper userMapper;
    private final ComercialSpaceMapper spaceMapper;
    private final RentalContractMapper contractMapper;

    public TenantServiceImpl(TenantRepository tenantRepository,
                             RentalContractRepository contractRepository,
                             UserMapper userMapper,
                             ComercialSpaceMapper spaceMapper,
                             RentalContractMapper contractMapper) {
        this.tenantRepository = tenantRepository;
        this.contractRepository = contractRepository;
        this.userMapper = userMapper;
        this.spaceMapper = spaceMapper;
        this.contractMapper = contractMapper;
    }

    // Metodele existente rămân neschimbate
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

        long activeContractsCount = contractRepository.findActiveContractsByTenantId(id).size();
        if (activeContractsCount > 0) {
            throw new IllegalStateException("Cannot delete tenant with active contracts. " +
                    "Please terminate all contracts first.");
        }

        tenantRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ComercialSpace> getTenantRentedSpaces(Long tenantId) {
        return contractRepository.findActiveContractsByTenantId(tenantId)
                .stream()
                .map(RentalContract::getSpace)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RentalContract> getTenantContracts(Long tenantId) {
        return contractRepository.findByTenantId(tenantId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RentalContract> getTenantActiveContracts(Long tenantId) {
        return contractRepository.findActiveContractsByTenantId(tenantId);
    }

    @Override
    @Transactional(readOnly = true)
    public Double getTenantMonthlyExpenses(Long tenantId) {
        return contractRepository.findActiveContractsByTenantId(tenantId)
                .stream()
                .mapToDouble(RentalContract::getMonthlyRent)
                .sum();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasActiveContractForSpace(Long tenantId, Long spaceId) {
        return contractRepository.findActiveContractsByTenantId(tenantId)
                .stream()
                .anyMatch(contract -> contract.getSpace().getId().equals(spaceId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Tenant> getTenantsWithActiveContracts() {
        return tenantRepository.findTenantsWithActiveContracts();
    }
}
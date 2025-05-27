package com.example.demo.repository;

import com.example.demo.model.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, Long> {

    Optional<Tenant> findByTaxId(String taxId);

    List<Tenant> findByBusinessType(String businessType);

    List<Tenant> findByCompanyNameContaining(String companyName);

    @Query("SELECT t FROM Tenant t JOIN t.contracts c WHERE c.status = 'ACTIVE'")
    List<Tenant> findTenantsWithActiveContracts();

    @Query("SELECT COUNT(c) FROM Tenant t JOIN t.contracts c WHERE t.id = :tenantId")
    long countContractsByTenantId(@Param("tenantId") Long tenantId);

    @Query("SELECT DISTINCT t.businessType FROM Tenant t WHERE t.businessType IS NOT NULL")
    List<String> findDistinctBusinessTypes();
}
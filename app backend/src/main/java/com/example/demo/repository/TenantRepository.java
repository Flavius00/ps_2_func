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

    // CORECTATĂ: Nu mai folosim t.contracts, ci folosim o jointure inversă cu RentalContract
    @Query("SELECT DISTINCT t FROM Tenant t WHERE EXISTS (SELECT 1 FROM RentalContract c WHERE c.tenant.id = t.id AND c.status = 'ACTIVE')")
    List<Tenant> findTenantsWithActiveContracts();

    // CORECTATĂ: Folosim jointure inversă pentru a număra contractele
    @Query("SELECT COUNT(c) FROM RentalContract c WHERE c.tenant.id = :tenantId")
    long countContractsByTenantId(@Param("tenantId") Long tenantId);

    @Query("SELECT DISTINCT t.businessType FROM Tenant t WHERE t.businessType IS NOT NULL")
    List<String> findDistinctBusinessTypes();
}
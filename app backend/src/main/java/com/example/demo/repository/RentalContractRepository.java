package com.example.demo.repository;

import com.example.demo.model.RentalContract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RentalContractRepository extends JpaRepository<RentalContract, Long> {

    @Query("SELECT c FROM RentalContract c WHERE c.tenant.id = :tenantId")
    List<RentalContract> findByTenantId(@Param("tenantId") Long tenantId);

    @Query("SELECT c FROM RentalContract c WHERE c.space.id = :spaceId")
    List<RentalContract> findBySpaceId(@Param("spaceId") Long spaceId);

    List<RentalContract> findByStatus(RentalContract.ContractStatus status);

    Optional<RentalContract> findByContractNumber(String contractNumber);

    List<RentalContract> findByStartDateBetween(LocalDate startDate, LocalDate endDate);

    List<RentalContract> findByEndDateBetween(LocalDate startDate, LocalDate endDate);

    List<RentalContract> findByIsPaid(Boolean isPaid);

    @Query("SELECT c FROM RentalContract c WHERE c.space.owner.id = :ownerId")
    List<RentalContract> findByOwnerId(@Param("ownerId") Long ownerId);

    @Query("SELECT c FROM RentalContract c WHERE c.endDate < :currentDate AND c.status = 'ACTIVE'")
    List<RentalContract> findExpiredContracts(@Param("currentDate") LocalDate currentDate);

    @Query("SELECT c FROM RentalContract c WHERE c.endDate BETWEEN :startDate AND :endDate AND c.status = 'ACTIVE'")
    List<RentalContract> findContractsExpiringBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(c) FROM RentalContract c WHERE c.status = :status")
    long countByStatus(@Param("status") RentalContract.ContractStatus status);

    @Query("SELECT SUM(c.monthlyRent) FROM RentalContract c WHERE c.status = 'ACTIVE'")
    Double getTotalActiveMonthlyRevenue();

    @Query("SELECT c FROM RentalContract c WHERE c.tenant.id = :tenantId AND c.status = 'ACTIVE'")
    List<RentalContract> findActiveContractsByTenantId(@Param("tenantId") Long tenantId);

    @Query("SELECT c FROM RentalContract c WHERE c.space.owner.id = :ownerId AND c.status = 'ACTIVE'")
    List<RentalContract> findActiveContractsByOwnerId(@Param("ownerId") Long ownerId);
}
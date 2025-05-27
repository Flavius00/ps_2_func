package com.example.demo.repository;

import com.example.demo.model.Owner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OwnerRepository extends JpaRepository<Owner, Long> {

    Optional<Owner> findByTaxId(String taxId);

    List<Owner> findByCompanyNameContaining(String companyName);

    @Query("SELECT o FROM Owner o JOIN o.spaces s WHERE s.available = true")
    List<Owner> findOwnersWithAvailableSpaces();

    @Query("SELECT COUNT(s) FROM Owner o JOIN o.spaces s WHERE o.id = :ownerId")
    long countSpacesByOwnerId(@Param("ownerId") Long ownerId);

    @Query("SELECT o FROM Owner o WHERE SIZE(o.spaces) > :minSpaces")
    List<Owner> findOwnersWithMinimumSpaces(@Param("minSpaces") int minSpaces);
}
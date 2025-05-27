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

    @Query("SELECT o FROM Owner o WHERE EXISTS (SELECT 1 FROM ComercialSpace s WHERE s.owner.id = o.id AND s.available = true)")
    List<Owner> findOwnersWithAvailableSpaces();

    // CORECTATĂ: Query-ul nu mai folosește o.spaces care nu mai există
    // În schimb, folosim o jointure inversă cu ComercialSpace
    @Query("SELECT COUNT(s) FROM ComercialSpace s WHERE s.owner.id = :ownerId")
    long countSpacesByOwnerId(@Param("ownerId") Long ownerId);

    // CORECTATĂ: Și această metodă pentru consistență
    @Query("SELECT o FROM Owner o WHERE (SELECT COUNT(s) FROM ComercialSpace s WHERE s.owner.id = o.id) > :minSpaces")
    List<Owner> findOwnersWithMinimumSpaces(@Param("minSpaces") int minSpaces);
}
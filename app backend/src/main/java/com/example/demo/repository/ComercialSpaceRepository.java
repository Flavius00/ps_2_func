package com.example.demo.repository;

import com.example.demo.model.ComercialSpace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComercialSpaceRepository extends JpaRepository<ComercialSpace, Long> {

    List<ComercialSpace> findByAvailable(Boolean available);

    List<ComercialSpace> findBySpaceType(ComercialSpace.SpaceType spaceType);

    // Aceste metode sunt corecte - folosesc relațiile ManyToOne existente
    @Query("SELECT cs FROM ComercialSpace cs WHERE cs.owner.id = :ownerId")
    List<ComercialSpace> findByOwnerId(@Param("ownerId") Long ownerId);

    @Query("SELECT cs FROM ComercialSpace cs WHERE cs.building.id = :buildingId")
    List<ComercialSpace> findByBuildingId(@Param("buildingId") Long buildingId);

    // ADĂUGATĂ: Metodă pentru spațiile disponibile ale unei clădiri
    @Query("SELECT cs FROM ComercialSpace cs WHERE cs.building.id = :buildingId AND cs.available = :available")
    List<ComercialSpace> findByBuildingIdAndAvailable(@Param("buildingId") Long buildingId, @Param("available") Boolean available);

    List<ComercialSpace> findByPricePerMonthBetween(Double minPrice, Double maxPrice);

    List<ComercialSpace> findByAreaBetween(Double minArea, Double maxArea);

    @Query("SELECT s FROM ComercialSpace s WHERE s.available = true AND s.spaceType = :spaceType")
    List<ComercialSpace> findAvailableBySpaceType(@Param("spaceType") ComercialSpace.SpaceType spaceType);

    @Query("SELECT s FROM ComercialSpace s WHERE s.available = true AND s.pricePerMonth <= :maxPrice")
    List<ComercialSpace> findAvailableWithinBudget(@Param("maxPrice") Double maxPrice);

    @Query("SELECT s FROM ComercialSpace s WHERE s.name LIKE %:keyword% OR s.description LIKE %:keyword%")
    List<ComercialSpace> searchByKeyword(@Param("keyword") String keyword);

    @Query("SELECT s FROM ComercialSpace s WHERE s.latitude BETWEEN :minLat AND :maxLat AND s.longitude BETWEEN :minLng AND :maxLng")
    List<ComercialSpace> findSpacesInArea(@Param("minLat") Double minLat, @Param("maxLat") Double maxLat,
                                          @Param("minLng") Double minLng, @Param("maxLng") Double maxLng);

    @Query("SELECT COUNT(s) FROM ComercialSpace s WHERE s.available = true")
    long countAvailableSpaces();

    @Query("SELECT s.spaceType, COUNT(s) FROM ComercialSpace s GROUP BY s.spaceType")
    List<Object[]> countSpacesByType();

    @Query("SELECT AVG(s.pricePerMonth) FROM ComercialSpace s WHERE s.spaceType = :spaceType")
    Double getAveragePriceBySpaceType(@Param("spaceType") ComercialSpace.SpaceType spaceType);

    // Metodă pentru a număra spațiile unui owner
    @Query("SELECT COUNT(cs) FROM ComercialSpace cs WHERE cs.owner.id = :ownerId")
    long countByOwnerId(@Param("ownerId") Long ownerId);

    // Metodă pentru spațiile disponibile ale unui owner
    @Query("SELECT cs FROM ComercialSpace cs WHERE cs.owner.id = :ownerId AND cs.available = true")
    List<ComercialSpace> findAvailableSpacesByOwnerId(@Param("ownerId") Long ownerId);

    // ADĂUGATĂ: Metodă pentru a număra spațiile unei clădiri
    @Query("SELECT COUNT(cs) FROM ComercialSpace cs WHERE cs.building.id = :buildingId")
    long countByBuildingId(@Param("buildingId") Long buildingId);
}
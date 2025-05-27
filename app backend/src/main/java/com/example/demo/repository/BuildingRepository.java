package com.example.demo.repository;

import com.example.demo.model.Building;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BuildingRepository extends JpaRepository<Building, Long> {

    List<Building> findByNameContaining(String name);

    List<Building> findByAddressContaining(String address);

    List<Building> findByYearBuiltBetween(Integer startYear, Integer endYear);

    List<Building> findByTotalFloorsGreaterThan(Integer minFloors);

    @Query("SELECT b FROM Building b WHERE b.latitude BETWEEN :minLat AND :maxLat AND b.longitude BETWEEN :minLng AND :maxLng")
    List<Building> findBuildingsInArea(@Param("minLat") Double minLat, @Param("maxLat") Double maxLat,
                                       @Param("minLng") Double minLng, @Param("maxLng") Double maxLng);

    // VERIFICATĂ: Această metodă folosește o jointure inversă, deci este corectă
    @Query("SELECT DISTINCT b FROM Building b WHERE EXISTS (SELECT 1 FROM ComercialSpace s WHERE s.building.id = b.id AND s.available = true)")
    List<Building> findBuildingsWithAvailableSpaces();

    // VERIFICATĂ: Folosește jointure inversă, corectă
    @Query("SELECT COUNT(s) FROM ComercialSpace s WHERE s.building.id = :buildingId")
    long countSpacesByBuildingId(@Param("buildingId") Long buildingId);

    // VERIFICATĂ: Folosește jointure inversă, corectă
    @Query("SELECT COUNT(s) FROM ComercialSpace s WHERE s.building.id = :buildingId AND s.available = true")
    long countAvailableSpacesByBuildingId(@Param("buildingId") Long buildingId);
}
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

    @Query("SELECT b FROM Building b JOIN b.spaces s WHERE s.available = true")
    List<Building> findBuildingsWithAvailableSpaces();

    @Query("SELECT COUNT(s) FROM Building b JOIN b.spaces s WHERE b.id = :buildingId")
    long countSpacesByBuildingId(@Param("buildingId") Long buildingId);

    @Query("SELECT COUNT(s) FROM Building b JOIN b.spaces s WHERE b.id = :buildingId AND s.available = true")
    long countAvailableSpacesByBuildingId(@Param("buildingId") Long buildingId);
}
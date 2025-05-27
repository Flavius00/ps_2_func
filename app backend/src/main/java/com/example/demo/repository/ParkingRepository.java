package com.example.demo.repository;

import com.example.demo.model.Parking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParkingRepository extends JpaRepository<Parking, Long> {

    List<Parking> findByParkingType(Parking.ParkingType parkingType);

    List<Parking> findByCovered(Boolean covered);

    List<Parking> findByNumberOfSpotsGreaterThan(Integer minSpots);

    List<Parking> findByPricePerSpotBetween(Double minPrice, Double maxPrice);

    @Query("SELECT p FROM Parking p WHERE p.numberOfSpots >= :requiredSpots AND p.pricePerSpot <= :maxPrice")
    List<Parking> findSuitableParking(@Param("requiredSpots") Integer requiredSpots, @Param("maxPrice") Double maxPrice);

    @Query("SELECT AVG(p.pricePerSpot) FROM Parking p WHERE p.parkingType = :parkingType")
    Double getAveragePriceByType(@Param("parkingType") Parking.ParkingType parkingType);

    @Query("SELECT SUM(p.numberOfSpots) FROM Parking p")
    Long getTotalParkingSpots();
}
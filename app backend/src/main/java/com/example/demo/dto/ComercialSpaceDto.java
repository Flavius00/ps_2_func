package com.example.demo.dto;

import lombok.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComercialSpaceDto {
    private Long id;
    private String name;
    private String description;
    private Double area;
    private Double pricePerMonth;
    private String address;
    private Double latitude;
    private Double longitude;
    private List<String> amenities;
    private Boolean available;
    private String spaceType;

    // Owner information (flattened)
    private Long ownerId;
    private String ownerName;
    private String ownerEmail;
    private String ownerPhone;
    private String ownerCompanyName;

    // Building information (flattened)
    private Long buildingId;
    private String buildingName;
    private String buildingAddress;
    private Integer buildingTotalFloors;
    private Integer buildingYearBuilt;

    // Parking information (flattened)
    private Long parkingId;
    private Integer parkingSpots;
    private Double parkingPricePerSpot;
    private Boolean parkingCovered;
    private String parkingType;

    // Type-specific fields
    private Integer floors;
    private Integer numberOfRooms;
    private Boolean hasReception;
    private Double shopWindowSize;
    private Boolean hasCustomerEntrance;
    private Integer maxOccupancy;
    private Double ceilingHeight;
    private Boolean hasLoadingDock;
    private String securityLevel;

    // Contract information
    private Integer contractsCount;
    private Boolean hasActiveContract;
}
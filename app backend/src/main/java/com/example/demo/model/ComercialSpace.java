package com.example.demo.model;

import lombok.*;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ComercialSpace {
    private Long id;
    private String name;
    private String description;
    private Double area; // in square meters
    private Double pricePerMonth;
    private String address;
    private Double latitude;
    private Double longitude;
    private List<String> amenities;
    private Boolean available;
    private Owner owner;
    private Building building;
    private Parking parking;

    // Space type: OFFICE, RETAIL, WAREHOUSE
    private String spaceType;

    // Office specific properties
    private Integer floors;
    private Integer numberOfRooms;
    private Boolean hasReception;

    // Retail specific properties
    private Double shopWindowSize;
    private Boolean hasCustomerEntrance;
    private Integer maxOccupancy;

    // Warehouse specific properties
    private Double ceilingHeight;
    private Boolean hasLoadingDock;
    private String securityLevel;
}
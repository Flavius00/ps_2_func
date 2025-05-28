package com.example.demo.dto;

import lombok.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComercialSpaceUpdateDto {
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
}
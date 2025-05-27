package com.example.demo.model;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Parking {
    private Long id;
    private Integer numberOfSpots;
    private Double pricePerSpot;
    private Boolean covered;
    private String parkingType; // UNDERGROUND, SURFACE, MULTI_LEVEL
}
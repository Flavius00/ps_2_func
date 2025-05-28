package com.example.demo.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BuildingDto {
    private Long id;
    private String name;
    private String address;
    private Integer totalFloors;
    private Integer yearBuilt;
    private Double latitude;
    private Double longitude;
}

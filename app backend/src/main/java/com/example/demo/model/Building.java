package com.example.demo.model;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Building {
    private Long id;
    private String name;
    private String address;
    private Integer totalFloors;
    private Integer yearBuilt;
    private Double latitude;
    private Double longitude;
}
package com.example.demo.model;

import lombok.*;
import jakarta.persistence.*;

@Entity
@Table(name = "buildings")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Building {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 500)
    private String address;

    private Integer totalFloors;

    private Integer yearBuilt;

    private Double latitude;

    private Double longitude;

    // ELIMINAT: Lista de ComercialSpace
    // Motivul: Relația este gestionată prin ComercialSpace.building
    // Pentru a obține spațiile unei clădiri, folosim query-uri în repository/service
    // Exemplu: comercialSpaceRepository.findByBuildingId(buildingId)
}
package com.example.demo.model;

import lombok.*;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "parking")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Parking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer numberOfSpots;

    private Double pricePerSpot;

    private Boolean covered;

    @Enumerated(EnumType.STRING)
    private ParkingType parkingType;

    // Relația OneToOne cu ComercialSpace - evită referința circulară
    @OneToOne(mappedBy = "parking", fetch = FetchType.LAZY)
    @JsonIgnore  // Evită serializarea circulară în JSON
    private ComercialSpace space;

    public enum ParkingType {
        UNDERGROUND,
        SURFACE,
        MULTI_LEVEL
    }
}
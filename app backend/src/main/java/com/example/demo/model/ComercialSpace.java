package com.example.demo.model;

import lombok.*;
import jakarta.persistence.*;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "comercial_spaces")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ComercialSpace {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private Double area; // in square meters

    @Column(nullable = false)
    private Double pricePerMonth;

    private String address;

    private Double latitude;

    private Double longitude;

    @ElementCollection
    @CollectionTable(name = "space_amenities", joinColumns = @JoinColumn(name = "space_id"))
    @Column(name = "amenity")
    private List<String> amenities;

    @Column(nullable = false)
    private Boolean available = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private Owner owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "building_id")
    @JsonBackReference
    private Building building;

    // Cascadă pentru parking - salvează automat parking-ul când salvează space-ul
    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinColumn(name = "parking_id")
    private Parking parking;

    // Space type: OFFICE, RETAIL, WAREHOUSE
    @Enumerated(EnumType.STRING)
    private SpaceType spaceType;

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

    @Enumerated(EnumType.STRING)
    private SecurityLevel securityLevel;

    @OneToMany(mappedBy = "space", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RentalContract> contracts;

    public enum SpaceType {
        OFFICE,
        RETAIL,
        WAREHOUSE
    }

    public enum SecurityLevel {
        LOW,
        MEDIUM,
        HIGH
    }
}
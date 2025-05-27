package com.example.demo.model;

import lombok.*;
import jakarta.persistence.*;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

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

    // Relația cu Owner - doar referința către owner, fără @JsonManagedReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private Owner owner;

    // Relația cu Building - doar referința către building
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "building_id")
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

    // Relația cu contractele - un spațiu poate avea multiple contracte de-a lungul timpului
    @OneToMany(mappedBy = "space", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RentalContract> contracts;

    // Metode helper pentru a accesa informațiile owner-ului fără a cauza loop
    @JsonProperty("ownerId")
    public Long getOwnerId() {
        return owner != null ? owner.getId() : null;
    }

    @JsonProperty("ownerName")
    public String getOwnerName() {
        return owner != null ? owner.getName() : null;
    }

    @JsonProperty("ownerEmail")
    public String getOwnerEmail() {
        return owner != null ? owner.getEmail() : null;
    }

    @JsonProperty("ownerPhone")
    public String getOwnerPhone() {
        return owner != null ? owner.getPhone() : null;
    }

    @JsonProperty("ownerCompanyName")
    public String getOwnerCompanyName() {
        return owner != null ? owner.getCompanyName() : null;
    }

    // Metode helper pentru building
    @JsonProperty("buildingId")
    public Long getBuildingId() {
        return building != null ? building.getId() : null;
    }

    @JsonProperty("buildingName")
    public String getBuildingName() {
        return building != null ? building.getName() : null;
    }

    @JsonProperty("buildingAddress")
    public String getBuildingAddress() {
        return building != null ? building.getAddress() : null;
    }

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
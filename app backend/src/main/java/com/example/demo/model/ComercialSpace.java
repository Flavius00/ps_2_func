package com.example.demo.model;

import lombok.*;
import jakarta.persistence.*;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnore;

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

    // CRITIC: Relația cu Owner - IGNORĂ serializarea pentru a evita loop-uri
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    @JsonIgnore // Evită serializarea directă a owner-ului
    private Owner owner;

    // CRITIC: Relația cu Building - IGNORĂ serializarea pentru a evita loop-uri
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "building_id")
    @JsonIgnore // Evită serializarea directă a building-ului
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

    // CRITIC: Relația cu contractele - IGNORĂ serializarea pentru a evita loop-uri
    @OneToMany(mappedBy = "space", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore // Evită serializarea directă a contractelor
    private List<RentalContract> contracts;

    // ===== METODE HELPER PENTRU JSON =====
    // Acestea returnează doar informațiile necesare fără a cauza loop-uri

    // Informații despre Owner (fără referința circulară)
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

    // Informații despre Building (fără referința circulară)
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

    @JsonProperty("buildingTotalFloors")
    public Integer getBuildingTotalFloors() {
        return building != null ? building.getTotalFloors() : null;
    }

    @JsonProperty("buildingYearBuilt")
    public Integer getBuildingYearBuilt() {
        return building != null ? building.getYearBuilt() : null;
    }

    // Informații despre contracte (doar count, fără lista completă)
    @JsonProperty("contractsCount")
    public Integer getContractsCount() {
        return contracts != null ? contracts.size() : 0;
    }

    @JsonProperty("hasActiveContract")
    public Boolean getHasActiveContract() {
        if (contracts == null || contracts.isEmpty()) return false;
        return contracts.stream()
                .anyMatch(contract ->
                        RentalContract.ContractStatus.ACTIVE.equals(contract.getStatus()));
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
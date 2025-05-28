package com.example.demo.model;

import lombok.*;
import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "rental_contracts")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RentalContract {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "space_id", nullable = false)
    @JsonIgnore
    private ComercialSpace space;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    @JsonIgnore
    private Tenant tenant;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(nullable = false)
    private LocalDate startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private Double monthlyRent;

    private Double securityDeposit;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ContractStatus status = ContractStatus.ACTIVE;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isPaid = false;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateCreated;

    @Column(unique = true)
    private String contractNumber;

    @Column(length = 1000)
    private String notes;

    // All the JSON helper methods remain the same...
    @JsonProperty("spaceId")
    public Long getSpaceId() {
        return space != null ? space.getId() : null;
    }

    @JsonProperty("spaceName")
    public String getSpaceName() {
        return space != null ? space.getName() : null;
    }

    @JsonProperty("spaceAddress")
    public String getSpaceAddress() {
        return space != null ? space.getAddress() : null;
    }

    @JsonProperty("spaceArea")
    public Double getSpaceArea() {
        return space != null ? space.getArea() : null;
    }

    @JsonProperty("spaceType")
    public String getSpaceType() {
        return space != null && space.getSpaceType() != null ?
                space.getSpaceType().toString() : null;
    }

    @JsonProperty("tenantId")
    public Long getTenantId() {
        return tenant != null ? tenant.getId() : null;
    }

    @JsonProperty("tenantName")
    public String getTenantName() {
        return tenant != null ? tenant.getName() : null;
    }

    @JsonProperty("tenantEmail")
    public String getTenantEmail() {
        return tenant != null ? tenant.getEmail() : null;
    }

    @JsonProperty("tenantPhone")
    public String getTenantPhone() {
        return tenant != null ? tenant.getPhone() : null;
    }

    @JsonProperty("tenantCompanyName")
    public String getTenantCompanyName() {
        return tenant != null ? tenant.getCompanyName() : null;
    }

    @JsonProperty("ownerId")
    public Long getOwnerId() {
        return space != null && space.getOwner() != null ?
                space.getOwner().getId() : null;
    }

    @JsonProperty("ownerName")
    public String getOwnerName() {
        return space != null && space.getOwner() != null ?
                space.getOwner().getName() : null;
    }

    @JsonProperty("ownerEmail")
    public String getOwnerEmail() {
        return space != null && space.getOwner() != null ?
                space.getOwner().getEmail() : null;
    }

    @JsonProperty("buildingId")
    public Long getBuildingId() {
        return space != null && space.getBuilding() != null ?
                space.getBuilding().getId() : null;
    }

    @JsonProperty("buildingName")
    public String getBuildingName() {
        return space != null && space.getBuilding() != null ?
                space.getBuilding().getName() : null;
    }

    public enum ContractStatus {
        ACTIVE,
        EXPIRED,
        TERMINATED,
        PENDING
    }
}
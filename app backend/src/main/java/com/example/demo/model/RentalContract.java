package com.example.demo.model;

import lombok.*;
import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;

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
    private ComercialSpace space;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
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
    private ContractStatus status = ContractStatus.ACTIVE;

    @Column(nullable = false)
    private Boolean isPaid = false;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateCreated;

    @Column(unique = true)
    private String contractNumber;

    @Column(length = 1000)
    private String notes;

    // Metode helper pentru a accesa informațiile space-ului fără a cauza loop
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

    // Metode helper pentru tenant
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

    public enum ContractStatus {
        ACTIVE,
        EXPIRED,
        TERMINATED,
        PENDING
    }
}
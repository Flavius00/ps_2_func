package com.example.demo.dto;

import lombok.*;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RentalContractDto {
    private Long id;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double monthlyRent;
    private Double securityDeposit;
    private String status;
    private Boolean isPaid;
    private LocalDate dateCreated;
    private String contractNumber;
    private String notes;

    // Space information (flattened)
    private Long spaceId;
    private String spaceName;
    private String spaceAddress;
    private Double spaceArea;
    private String spaceType;

    // Tenant information (flattened)
    private Long tenantId;
    private String tenantName;
    private String tenantEmail;
    private String tenantPhone;
    private String tenantCompanyName;

    // Owner information (flattened)
    private Long ownerId;
    private String ownerName;
    private String ownerEmail;

    // Building information (flattened)
    private Long buildingId;
    private String buildingName;
}
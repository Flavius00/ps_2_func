package com.example.demo.dto;

import lombok.*;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RentalContractCreateDto {
    private Long spaceId;
    private Long tenantId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double monthlyRent;
    private Double securityDeposit;

    @Builder.Default
    private String status = "ACTIVE";

    @Builder.Default
    private Boolean isPaid = false;

    private LocalDate dateCreated;
    private String contractNumber;
    private String notes;
}
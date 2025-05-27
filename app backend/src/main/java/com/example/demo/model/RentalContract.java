package com.example.demo.model;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RentalContract {
    private Long id;
    private ComercialSpace space;
    private Tenant tenant;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    private Double monthlyRent;
    private Double securityDeposit;
    private String status; // ACTIVE, EXPIRED, TERMINATED, PENDING
    private Boolean isPaid;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateCreated;

    private String contractNumber;
    private String notes;
}
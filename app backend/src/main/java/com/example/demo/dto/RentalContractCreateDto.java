package com.example.demo.dto;

import com.example.demo.validation.annotation.ValidContractDates;
import com.example.demo.validation.annotation.ValidSecurityDeposit;
import com.example.demo.validation.annotation.ValidRentalPrice;
import com.example.demo.groups.ValidationGroups;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ValidContractDates
@ValidSecurityDeposit(minMonths = 1.0, maxMonths = 6.0)
public class RentalContractCreateDto {

    @NotNull(message = "Space ID is required", groups = ValidationGroups.CreateValidation.class)
    @Positive(message = "Space ID must be positive")
    private Long spaceId;

    @NotNull(message = "Tenant ID is required", groups = ValidationGroups.CreateValidation.class)
    @Positive(message = "Tenant ID must be positive")
    private Long tenantId;

    @NotNull(message = "Start date is required", groups = ValidationGroups.CreateValidation.class)
    @FutureOrPresent(message = "Start date cannot be in the past")
    private LocalDate startDate;

    @NotNull(message = "End date is required", groups = ValidationGroups.CreateValidation.class)
    @Future(message = "End date must be in the future")
    private LocalDate endDate;

    @NotNull(message = "Monthly rent is required", groups = ValidationGroups.CreateValidation.class)
    @ValidRentalPrice(min = 100.0, max = 50000.0)
    private Double monthlyRent;

    @NotNull(message = "Security deposit is required", groups = ValidationGroups.CreateValidation.class)
    @DecimalMin(value = "0.0", message = "Security deposit cannot be negative")
    @DecimalMax(value = "300000.0", message = "Security deposit cannot exceed 300,000 RON")
    private Double securityDeposit;

    @Builder.Default
    @Pattern(regexp = "ACTIVE|PENDING|TERMINATED|EXPIRED",
            message = "Status must be ACTIVE, PENDING, TERMINATED, or EXPIRED")
    private String status = "ACTIVE";

    @Builder.Default
    private Boolean isPaid = false;

    @PastOrPresent(message = "Date created cannot be in the future")
    private LocalDate dateCreated;

    @Size(min = 5, max = 50, message = "Contract number must be between 5 and 50 characters")
    @Pattern(regexp = "^[A-Z0-9-]+$", message = "Contract number can only contain uppercase letters, numbers and hyphens")
    private String contractNumber;

    @Size(max = 1000, message = "Notes cannot exceed 1000 characters")
    private String notes;

    // Custom validation methods
    @AssertTrue(message = "Contract duration must be at least 1 month")
    public boolean isValidContractDuration() {
        if (startDate == null || endDate == null) {
            return true; // Let @NotNull handle null validation
        }
        return startDate.plusMonths(1).isBefore(endDate) || startDate.plusMonths(1).equals(endDate);
    }

    @AssertTrue(message = "Contract duration cannot exceed 5 years")
    public boolean isValidMaxContractDuration() {
        if (startDate == null || endDate == null) {
            return true; // Let @NotNull handle null validation
        }
        return endDate.isBefore(startDate.plusYears(5)) || endDate.equals(startDate.plusYears(5));
    }

    @AssertTrue(message = "Start date cannot be more than 1 month in the past")
    public boolean isValidStartDate() {
        if (startDate == null) {
            return true; // Let @NotNull handle null validation
        }
        return startDate.isAfter(LocalDate.now().minusMonths(1)) ||
                startDate.equals(LocalDate.now().minusMonths(1));
    }
}
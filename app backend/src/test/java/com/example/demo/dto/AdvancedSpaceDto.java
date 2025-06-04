package com.example.demo.dto;

import com.example.demo.validation.groups.ValidationGroups;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdvancedSpaceDto {

    @NotNull(groups = {ValidationGroups.CreateValidation.class, ValidationGroups.UpdateValidation.class})
    @Positive(groups = {ValidationGroups.CreateValidation.class, ValidationGroups.UpdateValidation.class})
    private Long id;

    @NotBlank(groups = {ValidationGroups.BasicValidation.class, ValidationGroups.CreateValidation.class})
    @Size(min = 3, max = 100, groups = {ValidationGroups.BasicValidation.class, ValidationGroups.CreateValidation.class})
    private String name;

    // Doar administratorii pot seta prețuri foarte mari
    @Max(value = 10000, groups = ValidationGroups.UserValidation.class, message = "Regular users cannot set price above 10000 RON")
    @Max(value = 50000, groups = ValidationGroups.AdminValidation.class, message = "Maximum price is 50000 RON")
    private Double pricePerMonth;

    // Validări specifice pentru spații de birouri
    @NotNull(groups = ValidationGroups.OfficeSpaceValidation.class, message = "Number of rooms is required for office spaces")
    @Min(value = 1, groups = ValidationGroups.OfficeSpaceValidation.class)
    private Integer numberOfRooms;

    // Validări specifice pentru spații comerciale
    @NotNull(groups = ValidationGroups.RetailSpaceValidation.class, message = "Shop window size is required for retail spaces")
    @DecimalMin(value = "1.0", groups = ValidationGroups.RetailSpaceValidation.class)
    private Double shopWindowSize;

    // Validări specifice pentru depozite
    @NotNull(groups = ValidationGroups.WarehouseSpaceValidation.class, message = "Ceiling height is required for warehouses")
    @DecimalMin(value = "3.0", groups = ValidationGroups.WarehouseSpaceValidation.class)
    private Double ceilingHeight;
}

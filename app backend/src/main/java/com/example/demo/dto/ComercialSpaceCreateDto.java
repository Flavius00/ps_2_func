package com.example.demo.dto;

import com.example.demo.validation.annotation.*;
import com.example.demo.groups.ValidationGroups;
import com.example.demo.validation.annotation.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ValidCoordinates
@ConditionalNotNull(field = "shopWindowSize", condition = "spaceType", value = "RETAIL")
@ConditionalNotNull(field = "hasCustomerEntrance", condition = "spaceType", value = "RETAIL")
@ConditionalNotNull(field = "maxOccupancy", condition = "spaceType", value = "RETAIL")
@ConditionalNotNull(field = "numberOfRooms", condition = "spaceType", value = "OFFICE")
@ConditionalNotNull(field = "hasReception", condition = "spaceType", value = "OFFICE")
@ConditionalNotNull(field = "ceilingHeight", condition = "spaceType", value = "WAREHOUSE")
@ConditionalNotNull(field = "hasLoadingDock", condition = "spaceType", value = "WAREHOUSE")
@ConditionalNotNull(field = "securityLevel", condition = "spaceType", value = "WAREHOUSE")
public class ComercialSpaceCreateDto {

    @NotBlank(message = "Space name is required", groups = {ValidationGroups.CreateValidation.class, ValidationGroups.BasicValidation.class})
    @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters", groups = {ValidationGroups.CreateValidation.class})
    private String name;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    @NotNull(message = "Area is required", groups = ValidationGroups.CreateValidation.class)
    @ValidAreaRange(min = 10.0, max = 10000.0, groups = {ValidationGroups.CreateValidation.class, ValidationGroups.BasicValidation.class})
    private Double area;

    @NotNull(message = "Price per month is required", groups = ValidationGroups.CreateValidation.class)
    @ValidRentalPrice(min = 100.0, max = 100000.0, groups = ValidationGroups.AdminValidation.class)
    private Double pricePerMonth;

    @NotBlank(message = "Address is required", groups = ValidationGroups.CreateValidation.class)
    @Size(min = 10, max = 500, message = "Address must be between 10 and 500 characters")
    private String address;

    @NotNull(message = "Latitude is required", groups = ValidationGroups.CreateValidation.class)
    @DecimalMin(value = "43.5", message = "Latitude must be within Romania boundaries")
    @DecimalMax(value = "48.5", message = "Latitude must be within Romania boundaries")
    private Double latitude;

    @NotNull(message = "Longitude is required", groups = ValidationGroups.CreateValidation.class)
    @DecimalMin(value = "20.0", message = "Longitude must be within Romania boundaries")
    @DecimalMax(value = "30.0", message = "Longitude must be within Romania boundaries")
    private Double longitude;

    @Size(max = 20, message = "Maximum 20 amenities allowed")
    private List<@NotBlank(message = "Amenity name cannot be blank") @Size(max = 50) String> amenities;

    @Builder.Default
    private Boolean available = true;

    @NotBlank(message = "Space type is required", groups = ValidationGroups.CreateValidation.class)
    @ValidSpaceType
    private String spaceType;

    @NotNull(message = "Owner is required", groups = ValidationGroups.CreateValidation.class)
    @Positive(message = "Owner ID must be positive")
    private Long ownerId;

    @NotNull(message = "Building is required", groups = ValidationGroups.CreateValidation.class)
    @Positive(message = "Building ID must be positive")
    private Long buildingId;

    // Type-specific fields for OFFICE
    @Min(value = 1, message = "Office must have at least 1 floor")
    @Max(value = 50, message = "Maximum 50 floors allowed")
    private Integer floors;

    @Min(value = 1, message = "Office must have at least 1 room")
    @Max(value = 100, message = "Maximum 100 rooms allowed")
    private Integer numberOfRooms;

    private Boolean hasReception;

    // Type-specific fields for RETAIL
    @DecimalMin(value = "1.0", message = "Shop window size must be at least 1 square meter")
    @DecimalMax(value = "100.0", message = "Shop window size cannot exceed 100 square meters")
    private Double shopWindowSize;

    private Boolean hasCustomerEntrance;

    @Min(value = 1, message = "Maximum occupancy must be at least 1 person")
    @Max(value = 1000, message = "Maximum occupancy cannot exceed 1000 people")
    private Integer maxOccupancy;

    // Type-specific fields for WAREHOUSE
    @DecimalMin(value = "2.5", message = "Ceiling height must be at least 2.5 meters")
    @DecimalMax(value = "20.0", message = "Ceiling height cannot exceed 20 meters")
    private Double ceilingHeight;

    private Boolean hasLoadingDock;

    @Pattern(regexp = "LOW|MEDIUM|HIGH", message = "Security level must be LOW, MEDIUM, or HIGH")
    private String securityLevel;
}
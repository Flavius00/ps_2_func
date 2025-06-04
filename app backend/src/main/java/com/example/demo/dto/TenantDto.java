package com.example.demo.dto;

import com.example.demo.validation.annotation.ValidBusinessType;
import com.example.demo.validation.annotation.ValidTaxId;
import com.example.demo.groups.ValidationGroups;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TenantDto extends UserDto {

    @Size(min = 2, max = 200, message = "Company name must be between 2 and 200 characters")
    @Pattern(regexp = "^[a-zA-ZăâîșțĂÂÎȘȚ0-9\\s.-]+$",
            message = "Company name can only contain letters, numbers, spaces, dots and hyphens")
    private String companyName;

    @NotBlank(message = "Business type is required", groups = {ValidationGroups.CreateValidation.class, ValidationGroups.TenantValidation.class})
    @ValidBusinessType
    private String businessType;

    @ValidTaxId
    private String taxId;

    @Builder(builderMethodName = "tenantBuilder")
    public TenantDto(Long id, String name, String email, String username,
                     String phone, String address, String profilePictureUrl,
                     String role, String companyName, String businessType, String taxId) {
        super();
        setId(id);
        setName(name);
        setEmail(email);
        setUsername(username);
        setPhone(phone);
        setAddress(address);
        setProfilePictureUrl(profilePictureUrl);
        setRole(role);
        this.companyName = companyName;
        this.businessType = businessType;
        this.taxId = taxId;
    }

    // Additional business validation
    @AssertTrue(message = "Tenant role must be set to TENANT")
    public boolean isValidTenantRole() {
        return "TENANT".equals(getRole());
    }

    // Business logic validation
    @AssertTrue(message = "Tax ID is required for companies with registered business")
    public boolean isTaxIdRequiredForCompany() {
        // If company name is provided, tax ID should also be provided
        return companyName == null || companyName.trim().isEmpty() ||
                (taxId != null && !taxId.trim().isEmpty());
    }
}
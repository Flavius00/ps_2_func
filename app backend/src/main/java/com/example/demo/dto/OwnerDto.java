package com.example.demo.dto;

import com.example.demo.validation.annotation.ValidTaxId;
import com.example.demo.groups.ValidationGroups;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OwnerDto extends UserDto {

    @NotBlank(message = "Company name is required", groups = {ValidationGroups.CreateValidation.class, ValidationGroups.OwnerValidation.class})
    @Size(min = 2, max = 200, message = "Company name must be between 2 and 200 characters")
    @Pattern(regexp = "^[a-zA-ZăâîșțĂÂÎȘȚ0-9\\s.-]+$",
            message = "Company name can only contain letters, numbers, spaces, dots and hyphens")
    private String companyName;

    @NotBlank(message = "Tax ID is required", groups = {ValidationGroups.CreateValidation.class, ValidationGroups.OwnerValidation.class})
    @ValidTaxId
    private String taxId;

    @Builder(builderMethodName = "ownerBuilder")
    public OwnerDto(Long id, String name, String email, String username,
                    String phone, String address, String profilePictureUrl,
                    String role, String companyName, String taxId) {
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
        this.taxId = taxId;
    }

    // Additional business validation
    @AssertTrue(message = "Owner role must be set to OWNER")
    public boolean isValidOwnerRole() {
        return "OWNER".equals(getRole());
    }
}
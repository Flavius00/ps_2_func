package com.example.demo.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TenantDto extends UserDto {
    private String companyName;
    private String businessType;
    private String taxId;

    @Builder(builderMethodName = "tenantBuilder")
    public TenantDto(Long id, String name, String email, String username,
                     String phone, String address, String profilePictureUrl,
                     String role, String companyName, String businessType, String taxId) {
        super(id, name, email, username, phone, address, profilePictureUrl, role);
        this.companyName = companyName;
        this.businessType = businessType;
        this.taxId = taxId;
    }
}
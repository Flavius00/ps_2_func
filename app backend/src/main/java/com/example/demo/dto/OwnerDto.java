package com.example.demo.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OwnerDto extends UserDto {
    private String companyName;
    private String taxId;

    @Builder(builderMethodName = "ownerBuilder")
    public OwnerDto(Long id, String name, String email, String username,
                    String phone, String address, String profilePictureUrl,
                    String role, String companyName, String taxId) {
        super(id, name, email, username, phone, address, profilePictureUrl, role);
        this.companyName = companyName;
        this.taxId = taxId;
    }
}
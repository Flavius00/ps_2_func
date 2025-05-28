package com.example.demo.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TenantDto extends UserDto {
    private String companyName;
    private String businessType;
    private String taxId;
}
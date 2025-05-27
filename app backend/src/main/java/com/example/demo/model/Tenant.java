package com.example.demo.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Tenant extends User {
    private String companyName;
    private String businessType;
    private String taxId;
}
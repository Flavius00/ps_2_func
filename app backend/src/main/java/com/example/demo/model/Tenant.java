package com.example.demo.model;

import lombok.*;
import lombok.experimental.SuperBuilder;
import jakarta.persistence.*;

@Entity
@DiscriminatorValue("TENANT")
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Tenant extends User {

    private String companyName;

    private String businessType;

    private String taxId;

    // ELIMINAT COMPLET: Lista de RentalContract sau ComercialSpace
    // Motivul: Relațiile sunt deja gestionate prin RentalContract.tenant
    // Pentru a obține contractele unui tenant, folosim:
    // rentalContractRepository.findByTenantId(tenantId)
    // Pentru spațiile închiriate:
    // tenant.getContracts().stream().map(RentalContract::getSpace).collect(toList())

    // NU MAI EXISTĂ referințe circulare prin liste!

    // Constructor care setează automat rolul
    public Tenant(String name, String email, String username, String password) {
        super();
        this.setName(name);
        this.setEmail(email);
        this.setUsername(username);
        this.setPassword(password);
        this.setRole(UserRole.TENANT);
    }
}
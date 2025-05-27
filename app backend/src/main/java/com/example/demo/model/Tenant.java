package com.example.demo.model;

import lombok.*;
import lombok.experimental.SuperBuilder;
import jakarta.persistence.*;
import java.util.List;

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



    // ELIMINAT: Lista directă de CommercialSpace
    // Motivul: Spațiile închiriate sunt accesibile prin contracts -> space
    // Pentru a obține spațiile închiriate de un tenant, folosim:
    // tenant.getContracts().stream().map(RentalContract::getSpace).collect(toList())

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
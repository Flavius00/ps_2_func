package com.example.demo.model;

import lombok.*;
import lombok.experimental.SuperBuilder;
import jakarta.persistence.*;

@Entity
@DiscriminatorValue("OWNER")
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Owner extends User {

    private String companyName;

    private String taxId;

    // ELIMINAT COMPLET: Lista de CommercialSpace
    // Motivul: Relația este deja gestionată prin CommercialSpace.owner
    // Pentru a obține spațiile unui proprietar, folosim query-uri în repository/service
    // Exemplu: comercialSpaceRepository.findByOwnerId(ownerId)

    // NU MAI EXISTĂ referințe circulare prin liste!

    // Constructor care setează automat rolul
    public Owner(String name, String email, String username, String password) {
        super();
        this.setName(name);
        this.setEmail(email);
        this.setUsername(username);
        this.setPassword(password);
        this.setRole(UserRole.OWNER);
    }
}
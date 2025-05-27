package com.example.demo.model;

import lombok.*;
import lombok.experimental.SuperBuilder;
import jakarta.persistence.*;
import java.util.List;

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

    // ELIMINAT: Lista de CommercialSpace
    // Motivul: Relația este deja gestionată prin CommercialSpace.owner
    // Pentru a obține spațiile unui proprietar, folosim query-uri în repository/service

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
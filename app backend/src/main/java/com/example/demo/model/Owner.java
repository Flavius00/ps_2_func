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

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ComercialSpace> spaces;

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
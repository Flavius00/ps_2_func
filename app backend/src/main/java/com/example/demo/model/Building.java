package com.example.demo.model;

import lombok.*;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "buildings")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Building {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 500)
    private String address;

    private Integer totalFloors;

    private Integer yearBuilt;

    private Double latitude;

    private Double longitude;

    // ELIMINAT @JsonManagedReference - nu mai avem nevoie de adnotări JSON circulare
    // Relația este păstrată pentru integritatea referențială, dar nu va fi serializată automat
    @OneToMany(mappedBy = "building", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ComercialSpace> spaces;
}
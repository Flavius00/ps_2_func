package com.example.demo.model;

import lombok.*;
import jakarta.persistence.*;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonManagedReference;

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

    @OneToMany(mappedBy = "building", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference("building-spaces") // Numele trebuie să corespundă cu cel din ComercialSpace
    private List<ComercialSpace> spaces;
}
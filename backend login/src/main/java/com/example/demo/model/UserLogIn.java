package com.example.demo.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserLogIn {
    private Long id;
    private String username;
    private String password;
    private String role; // <-- Aici adaugi câmpul role

}
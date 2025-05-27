package com.example.demo.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class User {
    private Long id;
    private String name;
    private String email;
    private String username;
    private String password;
    private String phone;
    private String address;
    private String profilePictureUrl;
    private UserRole role;

    public enum UserRole {
        OWNER,
        TENANT,
        ADMIN
    }
}
package com.example.demo.dto.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {
    private Long id;
    private String name;
    private String email;
    private String username;
    private String phone;
    private String address;
    private String profilePictureUrl;
    private String role;
    private boolean success;
    private String message;
    private String token; // pentru viitoare implementări JWT
}
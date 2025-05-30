package com.example.demo.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String name;
    private String email;
    private String username;
    private String phone;
    private String address;
    private String profilePictureUrl;
    private String role;
}
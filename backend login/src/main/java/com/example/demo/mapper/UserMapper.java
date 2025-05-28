package com.example.demo.mapper;

import com.example.demo.dto.UserDTO;
import com.example.demo.dto.request.RegisterRequestDTO;
import com.example.demo.dto.response.LoginResponseDTO;
import com.example.demo.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDTO toDTO(User user) {
        if (user == null) {
            return null;
        }

        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .username(user.getUsername())
                .phone(user.getPhone())
                .address(user.getAddress())
                .profilePictureUrl(user.getProfilePictureUrl())
                .role(user.getRole())
                .build();
    }

    public LoginResponseDTO toLoginResponse(User user) {
        if (user == null) {
            return null;
        }

        return LoginResponseDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .username(user.getUsername())
                .phone(user.getPhone())
                .address(user.getAddress())
                .profilePictureUrl(user.getProfilePictureUrl())
                .role(user.getRole().toString())
                .success(true)
                .message("Login successful")
                .build();
    }

    public User fromRegisterDTO(RegisterRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        return User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .username(dto.getUsername())
                .password(dto.getPassword())
                .phone(dto.getPhone())
                .address(dto.getAddress())
                .profilePictureUrl(dto.getProfilePictureUrl())
                .role(dto.getRole())
                .build();
    }

    public void updateUserFromDTO(User user, UserDTO dto) {
        if (user == null || dto == null) {
            return;
        }

        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setAddress(dto.getAddress());
        user.setProfilePictureUrl(dto.getProfilePictureUrl());
        // Note: Username, password, and role should not be updated through this method
    }
}
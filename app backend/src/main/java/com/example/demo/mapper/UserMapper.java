package com.example.demo.mapper;

import com.example.demo.dto.UserDto;
import com.example.demo.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDto toDto(User entity) {
        if (entity == null) {
            return null;
        }

        UserDto dto = new UserDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setEmail(entity.getEmail());
        dto.setUsername(entity.getUsername());
        dto.setPhone(entity.getPhone());
        dto.setAddress(entity.getAddress());
        dto.setProfilePictureUrl(entity.getProfilePictureUrl());

        if (entity.getRole() != null) {
            dto.setRole(entity.getRole().name());
        }

        return dto;
    }

    public User toEntity(UserDto dto) {
        if (dto == null) {
            return null;
        }

        User entity = new User();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setEmail(dto.getEmail());
        entity.setUsername(dto.getUsername());
        entity.setPhone(dto.getPhone());
        entity.setAddress(dto.getAddress());
        entity.setProfilePictureUrl(dto.getProfilePictureUrl());

        if (dto.getRole() != null) {
            entity.setRole(stringToRole(dto.getRole()));
        }

        return entity;
    }

    private User.UserRole stringToRole(String role) {
        if (role == null) {
            return User.UserRole.TENANT;
        }
        try {
            return User.UserRole.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            return User.UserRole.TENANT;
        }
    }
}
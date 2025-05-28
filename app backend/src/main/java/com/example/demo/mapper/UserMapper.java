package com.example.demo.mapper;

import com.example.demo.dto.UserDto;
import com.example.demo.model.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // Entity to DTO mapping
    @Mapping(target = "role", expression = "java(entity.getRole() != null ? entity.getRole().name() : null)")
    UserDto toDto(User entity);

    // DTO to Entity mapping
    @Mapping(target = "role", expression = "java(stringToRole(dto.getRole()))")
    User toEntity(UserDto dto);

    // Helper method pentru conversie role
    default User.UserRole stringToRole(String role) {
        if (role == null) {
            return User.UserRole.TENANT; // default value
        }
        try {
            return User.UserRole.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            return User.UserRole.TENANT; // default value
        }
    }
}
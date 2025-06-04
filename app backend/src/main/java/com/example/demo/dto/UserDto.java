package com.example.demo.dto;

import com.example.demo.validation.annotation.ValidPhoneNumber;
import com.example.demo.groups.ValidationGroups;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    @Positive(message = "User ID must be positive")
    private Long id;

    @NotBlank(message = "Name is required", groups = {ValidationGroups.CreateValidation.class, ValidationGroups.BasicValidation.class})
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    @Pattern(regexp = "^[a-zA-ZăâîșțĂÂÎȘȚ\\s-']+$", message = "Name can only contain letters, spaces, hyphens and apostrophes")
    private String name;

    @NotBlank(message = "Email is required", groups = {ValidationGroups.CreateValidation.class, ValidationGroups.BasicValidation.class})
    @Email(message = "Please provide a valid email address")
    @Size(max = 150, message = "Email cannot exceed 150 characters")
    private String email;

    @NotBlank(message = "Username is required", groups = {ValidationGroups.CreateValidation.class, ValidationGroups.BasicValidation.class})
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "Username can only contain letters, numbers, dots, underscores and hyphens")
    private String username;

    @ValidPhoneNumber
    private String phone;

    @Size(max = 500, message = "Address cannot exceed 500 characters")
    private String address;

    @Pattern(regexp = "^https?://.*\\.(jpg|jpeg|png|gif|bmp|webp)$",
            message = "Profile picture URL must be a valid image URL")
    private String profilePictureUrl;

    @NotBlank(message = "Role is required", groups = ValidationGroups.CreateValidation.class)
    @Pattern(regexp = "OWNER|TENANT|ADMIN", message = "Role must be OWNER, TENANT, or ADMIN")
    private String role;

    // Custom validation method for password (when needed)
    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$",
            message = "Password must contain at least one lowercase letter, one uppercase letter, and one digit")
    private String password;

}
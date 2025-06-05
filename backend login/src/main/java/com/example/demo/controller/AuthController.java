package com.example.demo.controller;

import com.example.demo.dto.request.LoginRequestDTO;
import com.example.demo.dto.request.RegisterRequestDTO;
import com.example.demo.dto.response.LoginResponseDTO;
import com.example.demo.model.User;
import com.example.demo.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        try {
            System.out.println("Login attempt for username: " + loginRequest.getUsername());

            LoginResponseDTO loginResponse = authService.login(loginRequest);

            if (loginResponse.isSuccess()) {
                System.out.println("Login successful for: " + loginRequest.getUsername());
                return ResponseEntity.ok(loginResponse);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(loginResponse);
            }

        } catch (Exception e) {
            System.err.println("Login error: " + e.getMessage());
            e.printStackTrace();

            LoginResponseDTO errorResponse = LoginResponseDTO.builder()
                    .success(false)
                    .message("Login failed: " + e.getMessage())
                    .build();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestDTO registerRequest) {
        try {
            // Debug log to verify role is being received
            System.out.println("Register request received with role: " + registerRequest.getRole());

            // Validate role is a valid enum value
            if (registerRequest.getRole() == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Role is required");
                errorResponse.put("field", "role");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }

            try {
                // This will throw an exception if the role value is not a valid enum
                User.UserRole.valueOf(registerRequest.getRole().toString());
            } catch (IllegalArgumentException e) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Invalid role value: " + registerRequest.getRole());
                errorResponse.put("field", "role");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }

            User createdUser = authService.register(registerRequest);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "User registered successfully with encrypted password");
            response.put("userId", createdUser.getId());
            response.put("username", createdUser.getUsername());
            response.put("role", createdUser.getRole().toString());

            // Debug log to verify role was properly saved
            System.out.println("User registered with role: " + createdUser.getRole());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());

            if (e.getMessage().contains("Username")) {
                errorResponse.put("field", "username");
            } else if (e.getMessage().contains("Email")) {
                errorResponse.put("field", "email");
            }

            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);

        } catch (Exception e) {
            System.err.println("Registration error: " + e.getMessage());
            e.printStackTrace();

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Registration failed: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            String token = null;
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
            }

            authService.logout(token);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Logout successful");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Logout failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/check")
    public ResponseEntity<?> checkAuthentication() {
        Map<String, Object> response = new HashMap<>();
        response.put("authenticated", true);
        response.put("service", "login-backend");
        response.put("timestamp", System.currentTimeMillis());
        response.put("security", "BCrypt password hashing enabled");
        return ResponseEntity.ok(response);
    }
}
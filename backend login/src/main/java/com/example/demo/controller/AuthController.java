package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {
    private final UserRepository userRepository;

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginUser) {
        try {
            System.out.println("Login attempt for username: " + loginUser.getUsername());

            Optional<User> userOptional = userRepository.findByUsername(loginUser.getUsername());

            if (userOptional.isPresent()) {
                User user = userOptional.get();
                System.out.println("User found: " + user.getUsername());
                System.out.println("Password check: " + user.getPassword().equals(loginUser.getPassword()));

                if (user.getPassword().equals(loginUser.getPassword())) {
                    // Login successful
                    Map<String, Object> response = new HashMap<>();
                    response.put("id", user.getId());
                    response.put("name", user.getName());
                    response.put("email", user.getEmail());
                    response.put("username", user.getUsername());
                    response.put("phone", user.getPhone());
                    response.put("address", user.getAddress());
                    response.put("profilePictureUrl", user.getProfilePictureUrl());
                    response.put("role", user.getRole().toString());
                    response.put("success", true);
                    response.put("message", "Login successful");

                    System.out.println("Login successful for: " + user.getUsername());
                    return ResponseEntity.ok(response);
                }
            }

            // Login failed
            System.out.println("Login failed for username: " + loginUser.getUsername());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Invalid username or password");

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);

        } catch (Exception e) {
            System.err.println("Login error: " + e.getMessage());
            e.printStackTrace();

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Login failed: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User newUser) {
        try {
            // Check if username already exists
            if (userRepository.existsByUsername(newUser.getUsername())) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Username already exists");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
            }

            // Check if email already exists
            if (userRepository.existsByEmail(newUser.getEmail())) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Email already exists");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
            }

            // Set default role if not provided
            if (newUser.getRole() == null) {
                newUser.setRole(User.UserRole.TENANT);
            }

            // Create the new user
            User createdUser = userRepository.save(newUser);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "User registered successfully");
            response.put("userId", createdUser.getId());
            response.put("username", createdUser.getUsername());
            response.put("role", createdUser.getRole().toString());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

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
    public ResponseEntity<?> logout() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Logout successful");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/check")
    public ResponseEntity<?> checkAuthentication() {
        Map<String, Object> response = new HashMap<>();
        response.put("authenticated", true);
        response.put("service", "login-backend");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }

    // Endpoint pentru debugging - vezi toți utilizatorii
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        try {
            long userCount = userRepository.count();
            Map<String, Object> response = new HashMap<>();
            response.put("totalUsers", userCount);
            response.put("message", "Users count retrieved successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to get users: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
package com.example.demo.service.impl;

import com.example.demo.dto.request.LoginRequestDTO;
import com.example.demo.dto.request.RegisterRequestDTO;
import com.example.demo.dto.response.LoginResponseDTO;
import com.example.demo.mapper.UserMapper;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public LoginResponseDTO login(LoginRequestDTO loginRequest) {
        System.out.println("AuthService: Login attempt for username: " + loginRequest.getUsername());

        Optional<User> userOptional = userRepository.findByUsername(loginRequest.getUsername());

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            System.out.println("AuthService: User found: " + user.getUsername());

            // Verifică parola folosind BCrypt
            if (passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                System.out.println("AuthService: Password match, login successful");
                return userMapper.toLoginResponse(user);
            } else {
                System.out.println("AuthService: Password mismatch");
                return LoginResponseDTO.builder()
                        .success(false)
                        .message("Invalid username or password")
                        .build();
            }
        } else {
            System.out.println("AuthService: User not found with username: " + loginRequest.getUsername());
            return LoginResponseDTO.builder()
                    .success(false)
                    .message("Invalid username or password")
                    .build();
        }
    }

    @Override
    public User register(RegisterRequestDTO registerRequest) {
        // Validate that username doesn't exist
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new IllegalArgumentException("Username already exists: " + registerRequest.getUsername());
        }

        // Validate that email doesn't exist
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + registerRequest.getEmail());
        }

        // Create user from DTO
        User user = userMapper.fromRegisterDTO(registerRequest);

        // Make sure the role is properly set - this is important
        if (user.getRole() == null) {
            // Default to TENANT if role is not specified
            user.setRole(User.UserRole.TENANT);
        }

        // Print debug information
        System.out.println("AuthService: Registering user with role: " + user.getRole());

        // Criptează parola înainte de salvare
        String encodedPassword = passwordEncoder.encode(registerRequest.getPassword());
        user.setPassword(encodedPassword);

        // Save and return
        User savedUser = userRepository.save(user);
        System.out.println("AuthService: User registered successfully with encrypted password: " + savedUser.getUsername());
        System.out.println("AuthService: User role saved as: " + savedUser.getRole());

        return savedUser;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean validateToken(String token) {
        // TODO: Implement JWT token validation logic here
        // For now, return true as we're not using JWT yet
        return token != null && !token.isEmpty();
    }

    @Override
    public void logout(String token) {
        // TODO: Implement token invalidation logic here
        // For JWT, you might want to add token to a blacklist
        System.out.println("AuthService: User logged out");
    }
}
package com.example.demo.controller;

import com.example.demo.dto.UserDto;
import com.example.demo.dto.RentalContractDto;
import com.example.demo.mapper.UserMapper;
import com.example.demo.mapper.RentalContractMapper;
import com.example.demo.model.User;
import com.example.demo.model.RentalContract;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;
    private final RentalContractMapper contractMapper;

    public UserController(UserService userService,
                          UserMapper userMapper,
                          RentalContractMapper contractMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.contractMapper = contractMapper;
    }

    @PostMapping
    public ResponseEntity<UserDto> addUser(@Valid @RequestBody UserDto userDto) {
        log.info("Creating new user: {}", userDto.getUsername());

        User user = userMapper.toEntity(userDto);
        User savedUser = userService.addUser(user);
        UserDto responseDto = userMapper.toDto(savedUser);

        log.info("Successfully created user with ID: {}", savedUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        log.info("Fetching all users");

        List<User> users = userService.getAllUsers();
        List<UserDto> userDtos = users.stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());

        log.info("Successfully retrieved {} users", userDtos.size());
        return ResponseEntity.ok(userDtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        log.info("Fetching user with ID: {}", id);

        User user = userService.getUserById(id);
        UserDto userDto = userMapper.toDto(user);

        log.info("Successfully retrieved user: {}", user.getUsername());
        return ResponseEntity.ok(userDto);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserDto updatedUserDto) {

        log.info("Updating user with ID: {}", id);

        // Get existing user to preserve certain fields
        User existingUser = userService.getUserById(id);

        // Update only allowed fields
        existingUser.setName(updatedUserDto.getName());
        existingUser.setEmail(updatedUserDto.getEmail());
        existingUser.setPhone(updatedUserDto.getPhone());
        existingUser.setAddress(updatedUserDto.getAddress());

        User savedUser = userService.updateUser(existingUser);
        UserDto responseDto = userMapper.toDto(savedUser);

        log.info("Successfully updated user: {}", savedUser.getUsername());
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("Deleting user with ID: {}", id);

        userService.deleteUser(id);

        log.info("Successfully deleted user with ID: {}", id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/contracts")
    public ResponseEntity<List<RentalContractDto>> getUserContracts(@PathVariable Long id) {
        log.info("Fetching contracts for user ID: {}", id);

        User user = userService.getUserById(id);
        if (user.getRole() == User.UserRole.TENANT) {
            List<RentalContract> contracts = userService.getUserContracts(id);
            List<RentalContractDto> contractDtos = contracts.stream()
                    .map(contractMapper::toDto)
                    .collect(Collectors.toList());

            log.info("Successfully retrieved {} contracts for user {}", contractDtos.size(), id);
            return ResponseEntity.ok(contractDtos);
        }

        log.info("User {} is not a tenant, returning empty contract list", id);
        return ResponseEntity.ok(List.of());
    }

    @PostMapping("/api/users/login")
    public ResponseEntity<UserDto> login(@Valid @RequestBody UserDto loginUserDto) {
        log.info("Login attempt for username: {}", loginUserDto.getUsername());

        User loginUser = userMapper.toEntity(loginUserDto);
        User user = userService.findByUsername(loginUser.getUsername());

        if (user != null && user.getPassword().equals(loginUser.getPassword())) {
            UserDto responseDto = userMapper.toDto(user);
            log.info("Successful login for user: {}", user.getUsername());
            return ResponseEntity.ok(responseDto);
        }

        log.warn("Failed login attempt for username: {}", loginUserDto.getUsername());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<List<UserDto>> getUsersByRole(@PathVariable String role) {
        log.info("Fetching users by role: {}", role);

        try {
            User.UserRole userRole = User.UserRole.valueOf(role.toUpperCase());
            List<User> users = userService.getUsersByRole(userRole);
            List<UserDto> userDtos = users.stream()
                    .map(userMapper::toDto)
                    .collect(Collectors.toList());

            log.info("Successfully retrieved {} users with role {}", userDtos.size(), role);
            return ResponseEntity.ok(userDtos);

        } catch (IllegalArgumentException e) {
            log.warn("Invalid user role requested: {}", role);
            throw new IllegalArgumentException("Invalid user role: " + role +
                    ". Valid roles are: OWNER, TENANT, ADMIN");
        }
    }

    @GetMapping("/exists/username/{username}")
    public ResponseEntity<Boolean> checkUsernameExists(@PathVariable String username) {
        log.info("Checking if username exists: {}", username);

        boolean exists = userService.existsByUsername(username);

        log.info("Username {} exists: {}", username, exists);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/exists/email/{email}")
    public ResponseEntity<Boolean> checkEmailExists(@PathVariable String email) {
        log.info("Checking if email exists: {}", email);

        boolean exists = userService.existsByEmail(email);

        log.info("Email {} exists: {}", email, exists);
        return ResponseEntity.ok(exists);
    }
}
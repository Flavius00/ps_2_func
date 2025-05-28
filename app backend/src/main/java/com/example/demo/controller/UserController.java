package com.example.demo.controller;

import com.example.demo.dto.UserDto;
import com.example.demo.dto.RentalContractDto;
import com.example.demo.mapper.UserMapper;
import com.example.demo.mapper.RentalContractMapper;
import com.example.demo.model.User;
import com.example.demo.model.RentalContract;
import com.example.demo.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

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
    public ResponseEntity<UserDto> addUser(@RequestBody UserDto userDto) {
        try {
            User user = userMapper.toEntity(userDto);
            User savedUser = userService.addUser(user);
            UserDto responseDto = userMapper.toDto(savedUser);
            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            System.err.println("Error adding user: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        try {
            List<User> users = userService.getAllUsers();
            List<UserDto> userDtos = users.stream()
                    .map(userMapper::toDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(userDtos);
        } catch (Exception e) {
            System.err.println("Error getting all users: " + e.getMessage());
            return ResponseEntity.ok(List.of());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.err.println("Error deleting user: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}/contracts")
    public ResponseEntity<List<RentalContractDto>> getUserContracts(@PathVariable Long id) {
        try {
            User user = userService.getUserById(id);
            if (user.getRole() == User.UserRole.TENANT) {
                List<RentalContract> contracts = userService.getUserContracts(id);
                List<RentalContractDto> contractDtos = contracts.stream()
                        .map(contractMapper::toDto)
                        .collect(Collectors.toList());
                return ResponseEntity.ok(contractDtos);
            }
            return ResponseEntity.ok(List.of());
        } catch (Exception e) {
            System.err.println("Error getting user contracts: " + e.getMessage());
            return ResponseEntity.ok(List.of());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable("id") Long id) {
        try {
            User user = userService.getUserById(id);
            UserDto userDto = userMapper.toDto(user);
            return ResponseEntity.ok(userDto);
        } catch (Exception e) {
            System.err.println("Error getting user by id: " + e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/api/users/login")
    public ResponseEntity<UserDto> login(@RequestBody UserDto loginUserDto) {
        try {
            User loginUser = userMapper.toEntity(loginUserDto);
            User user = userService.findByUsername(loginUser.getUsername());

            if (user != null && user.getPassword().equals(loginUser.getPassword())) {
                UserDto responseDto = userMapper.toDto(user);
                return ResponseEntity.ok(responseDto);
            }

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            System.err.println("Error during login: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable("id") Long id, @RequestBody UserDto updatedUserDto) {
        try {
            User user = userService.getUserById(id);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }

            // Map DTO to entity for update
            User updatedUser = userMapper.toEntity(updatedUserDto);
            updatedUser.setId(id);

            // Preserve some fields that shouldn't be updated via this endpoint
            updatedUser.setUsername(user.getUsername());
            updatedUser.setPassword(user.getPassword());
            updatedUser.setRole(user.getRole());

            User savedUser = userService.updateUser(updatedUser);
            UserDto responseDto = userMapper.toDto(savedUser);
            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            System.err.println("Error updating user: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}
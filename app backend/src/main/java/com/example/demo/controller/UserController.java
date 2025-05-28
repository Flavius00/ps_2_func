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
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    // FIXED: Update user endpoint with better error handling
    @PutMapping("/update/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable("id") Long id, @RequestBody UserDto updatedUserDto) {
        try {
            System.out.println("=== UPDATE USER DEBUG ===");
            System.out.println("User ID: " + id);
            System.out.println("Request body: " + updatedUserDto);

            // Verifică dacă utilizatorul există
            User existingUser = userService.getUserById(id);
            if (existingUser == null) {
                System.err.println("User not found with id: " + id);
                return ResponseEntity.notFound().build();
            }

            System.out.println("Existing user: " + existingUser.getName());

            // Actualizează doar câmpurile permise
            existingUser.setName(updatedUserDto.getName());
            existingUser.setEmail(updatedUserDto.getEmail());
            existingUser.setPhone(updatedUserDto.getPhone());
            existingUser.setAddress(updatedUserDto.getAddress());

            // NU actualiza username, password, sau role prin acest endpoint
            // acestea rămân neschimbate

            System.out.println("Updated user data before save: " + existingUser.getName());

            User savedUser = userService.updateUser(existingUser);

            System.out.println("User saved successfully: " + savedUser.getName());

            UserDto responseDto = userMapper.toDto(savedUser);

            System.out.println("Response DTO: " + responseDto);
            System.out.println("=== END UPDATE USER DEBUG ===");

            return ResponseEntity.ok(responseDto);

        } catch (IllegalArgumentException e) {
            System.err.println("Validation error updating user: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            System.err.println("Error updating user: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
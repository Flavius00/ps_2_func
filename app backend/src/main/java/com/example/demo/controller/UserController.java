package com.example.demo.controller;

import com.example.demo.model.RentalContract;
import com.example.demo.model.User;
import com.example.demo.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User addUser(@RequestBody User user) {
        return userService.addUser(user);
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

//    @PutMapping
//    public User updateUser(@RequestBody User user) {
//        return userService.updateUser(user);
//    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable("id") Long id) {
        userService.deleteUser(id);
    }

    @GetMapping("/{id}/contracts")
    public List<RentalContract> getUserContracts(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user.getRole() == User.UserRole.TENANT) {
            return userService.getUserContracts(id);
        }
        return List.of();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable("id") Long id) {
        return userService.getUserById(id);
    }

    @PostMapping("/api/users/login")
    public ResponseEntity<User> login(@RequestBody User loginUser) {
        User user = userService.findByUsername(loginUser.getUsername());

        if (user != null && user.getPassword().equals(loginUser.getPassword())) {
            return ResponseEntity.ok(user);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PutMapping("/update/{id}")
    public User updateUser(@PathVariable("id") Long id, @RequestBody User updatedUser) {
        User user = userService.getUserById(id);
        if (user == null) throw new RuntimeException("User not found");

        user.setName(updatedUser.getName());
        user.setEmail(updatedUser.getEmail());
        user.setPhone(updatedUser.getPhone());
        user.setAddress(updatedUser.getAddress());
        user.setProfilePictureUrl(updatedUser.getProfilePictureUrl());

        return userService.updateUser(user);
    }
}
package com.example.demo.service;

import com.example.demo.model.RentalContract;
import com.example.demo.model.User;
import java.util.List;

public interface UserService {
    User addUser(User user);
    List<User> getAllUsers();
    User getUserById(Long id);
    void deleteUser(Long id);
    User updateUser(User user);
    User findByUsername(String username);
    List<RentalContract> getUserContracts(Long userId);

    // Metode noi pentru JPA
    List<User> getUsersByRole(User.UserRole role);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
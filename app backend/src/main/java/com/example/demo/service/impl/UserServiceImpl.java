package com.example.demo.service.impl;

import com.example.demo.exception.*;
import com.example.demo.mapper.UserMapper;
import com.example.demo.mapper.RentalContractMapper;
import com.example.demo.model.RentalContract;
import com.example.demo.model.User;
import com.example.demo.repository.RentalContractRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RentalContractRepository rentalContractRepository;
    private final UserMapper userMapper;
    private final RentalContractMapper contractMapper;

    public UserServiceImpl(UserRepository userRepository,
                           RentalContractRepository rentalContractRepository,
                           UserMapper userMapper,
                           RentalContractMapper contractMapper) {
        this.userRepository = userRepository;
        this.rentalContractRepository = rentalContractRepository;
        this.userMapper = userMapper;
        this.contractMapper = contractMapper;
    }

    @Override
    public User addUser(User user) {
        try {
            validateUserForCreation(user);

            if (userRepository.existsByUsername(user.getUsername())) {
                throw new DuplicateResourceException("User", user.getUsername());
            }

            if (userRepository.existsByEmail(user.getEmail())) {
                throw new DuplicateResourceException("User with email", user.getEmail());
            }

            return userRepository.save(user);

        } catch (DataIntegrityViolationException ex) {
            log.error("Data integrity violation while creating user: {}", ex.getMessage());

            if (ex.getMessage() != null && ex.getMessage().contains("username")) {
                throw new DuplicateResourceException("User", user.getUsername());
            } else if (ex.getMessage() != null && ex.getMessage().contains("email")) {
                throw new DuplicateResourceException("User with email", user.getEmail());
            }

            throw new DatabaseOperationException("create user", "Failed to create user due to data constraints", ex);

        } catch (DataAccessException ex) {
            throw new DatabaseOperationException("create user", "Failed to save user", ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        try {
            return userRepository.findAll();
        } catch (DataAccessException ex) {
            throw new DatabaseOperationException("fetch all users", "Failed to retrieve users", ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("User ID must be a positive number");
        }

        try {
            return userRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        } catch (DataAccessException ex) {
            throw new DatabaseOperationException("fetch user by ID", "Failed to retrieve user", ex);
        }
    }

    @Override
    public User updateUser(User user) {
        if (user.getId() == null) {
            throw new IllegalArgumentException("User ID cannot be null for update operation");
        }

        try {
            validateUserForUpdate(user);

            if (!userRepository.existsById(user.getId())) {
                throw new ResourceNotFoundException("User not found with id: " + user.getId());
            }

            // Check if username is being changed and if it already exists
            if (user.getUsername() != null) {
                User existingUserByUsername = userRepository.findByUsername(user.getUsername()).orElse(null);
                if (existingUserByUsername != null && !existingUserByUsername.getId().equals(user.getId())) {
                    throw new DuplicateResourceException("User", user.getUsername());
                }
            }

            // Check if email is being changed and if it already exists
            if (user.getEmail() != null) {
                User existingUserByEmail = userRepository.findByEmail(user.getEmail()).orElse(null);
                if (existingUserByEmail != null && !existingUserByEmail.getId().equals(user.getId())) {
                    throw new DuplicateResourceException("User with email", user.getEmail());
                }
            }

            return userRepository.save(user);

        } catch (DataIntegrityViolationException ex) {
            log.error("Data integrity violation while updating user: {}", ex.getMessage());

            if (ex.getMessage() != null && ex.getMessage().contains("username")) {
                throw new DuplicateResourceException("User", user.getUsername());
            } else if (ex.getMessage() != null && ex.getMessage().contains("email")) {
                throw new DuplicateResourceException("User with email", user.getEmail());
            }

            throw new DatabaseOperationException("update user", "Failed to update user due to data constraints", ex);

        } catch (DataAccessException ex) {
            throw new DatabaseOperationException("update user", "Failed to update user", ex);
        }
    }

    @Override
    public void deleteUser(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("User ID must be a positive number");
        }

        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

            // Check if user has active contracts (for tenants)
            if (user.getRole() == User.UserRole.TENANT) {
                List<RentalContract> activeContracts = rentalContractRepository.findActiveContractsByTenantId(id);
                if (!activeContracts.isEmpty()) {
                    throw new InvalidOperationException("delete user",
                            "Cannot delete tenant with active contracts. Please terminate all contracts first.");
                }
            }

            // Check if user owns spaces with active contracts (for owners)
            if (user.getRole() == User.UserRole.OWNER) {
                List<RentalContract> ownerContracts = rentalContractRepository.findActiveContractsByOwnerId(id);
                if (!ownerContracts.isEmpty()) {
                    throw new InvalidOperationException("delete user",
                            "Cannot delete owner with active contracts on their properties. Please terminate all contracts first.");
                }
            }

            userRepository.deleteById(id);

        } catch (DataAccessException ex) {
            throw new DatabaseOperationException("delete user", "Failed to delete user", ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }

        try {
            return userRepository.findByUsername(username).orElse(null);
        } catch (DataAccessException ex) {
            throw new DatabaseOperationException("find user by username", "Failed to find user by username", ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<RentalContract> getUserContracts(Long userId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("User ID must be a positive number");
        }

        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

            if (user.getRole() != User.UserRole.TENANT) {
                throw new InvalidOperationException("get user contracts",
                        "Only tenants can have rental contracts");
            }

            return rentalContractRepository.findByTenantId(userId);

        } catch (DataAccessException ex) {
            throw new DatabaseOperationException("fetch user contracts", "Failed to retrieve user contracts", ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getUsersByRole(User.UserRole role) {
        if (role == null) {
            throw new IllegalArgumentException("User role cannot be null");
        }

        try {
            return userRepository.findByRole(role);
        } catch (DataAccessException ex) {
            throw new DatabaseOperationException("fetch users by role", "Failed to retrieve users by role", ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }

        try {
            return userRepository.existsByUsername(username);
        } catch (DataAccessException ex) {
            throw new DatabaseOperationException("check username existence", "Failed to check username existence", ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }

        try {
            return userRepository.existsByEmail(email);
        } catch (DataAccessException ex) {
            throw new DatabaseOperationException("check email existence", "Failed to check email existence", ex);
        }
    }

    // Private validation methods
    private void validateUserForCreation(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        if (user.getName() == null || user.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("User name cannot be null or empty");
        }

        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("User email cannot be null or empty");
        }

        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }

        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }

        if (user.getRole() == null) {
            throw new IllegalArgumentException("User role cannot be null");
        }

        // Basic email validation
        if (!user.getEmail().matches("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$")) {
            throw new IllegalArgumentException("Invalid email format");
        }

        // Username validation
        if (user.getUsername().length() < 3 || user.getUsername().length() > 50) {
            throw new IllegalArgumentException("Username must be between 3 and 50 characters");
        }

        // Password validation
        if (user.getPassword().length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long");
        }
    }

    private void validateUserForUpdate(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        if (user.getName() != null && user.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("User name cannot be empty");
        }

        if (user.getEmail() != null) {
            if (user.getEmail().trim().isEmpty()) {
                throw new IllegalArgumentException("User email cannot be empty");
            }

            // Basic email validation
            if (!user.getEmail().matches("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$")) {
                throw new IllegalArgumentException("Invalid email format");
            }
        }

        if (user.getUsername() != null) {
            if (user.getUsername().trim().isEmpty()) {
                throw new IllegalArgumentException("Username cannot be empty");
            }

            if (user.getUsername().length() < 3 || user.getUsername().length() > 50) {
                throw new IllegalArgumentException("Username must be between 3 and 50 characters");
            }
        }

        if (user.getPassword() != null) {
            if (user.getPassword().trim().isEmpty()) {
                throw new IllegalArgumentException("Password cannot be empty");
            }

            if (user.getPassword().length() < 6) {
                throw new IllegalArgumentException("Password must be at least 6 characters long");
            }
        }
    }
}
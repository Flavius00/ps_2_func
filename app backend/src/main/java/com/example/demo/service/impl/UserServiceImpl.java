package com.example.demo.service.impl;

import com.example.demo.mapper.UserMapper;
import com.example.demo.mapper.RentalContractMapper;
import com.example.demo.model.RentalContract;
import com.example.demo.model.User;
import com.example.demo.repository.RentalContractRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import com.example.demo.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username already exists: " + user.getUsername());
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + user.getEmail());
        }
        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    @Override
    public User updateUser(User user) {
        try {
            System.out.println("=== UserService.updateUser DEBUG ===");
            System.out.println("Updating user with ID: " + user.getId());

            if (!userRepository.existsById(user.getId())) {
                throw new ResourceNotFoundException("User not found with id: " + user.getId());
            }

            // Verifică dacă username-ul nu este deja folosit de alt utilizator
            if (user.getUsername() != null) {
                User existingUserByUsername = userRepository.findByUsername(user.getUsername()).orElse(null);
                if (existingUserByUsername != null && !existingUserByUsername.getId().equals(user.getId())) {
                    throw new IllegalArgumentException("Username already exists: " + user.getUsername());
                }
            }

            // Verifică dacă email-ul nu este deja folosit de alt utilizator
            if (user.getEmail() != null) {
                User existingUserByEmail = userRepository.findByEmail(user.getEmail()).orElse(null);
                if (existingUserByEmail != null && !existingUserByEmail.getId().equals(user.getId())) {
                    throw new IllegalArgumentException("Email already exists: " + user.getEmail());
                }
            }

            System.out.println("Saving user: " + user.getName());
            User savedUser = userRepository.save(user);
            System.out.println("User saved successfully with ID: " + savedUser.getId());
            System.out.println("=== END UserService.updateUser DEBUG ===");

            return savedUser;

        } catch (Exception e) {
            System.err.println("Error in UserService.updateUser: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RentalContract> getUserContracts(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }
        return rentalContractRepository.findByTenantId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getUsersByRole(User.UserRole role) {
        return userRepository.findByRole(role);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
package com.example.demo.service.impl;

import com.example.demo.model.RentalContract;
import com.example.demo.model.User;
import com.example.demo.repository.RentalContractRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import com.example.demo.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RentalContractRepository rentalContractRepository;

    public UserServiceImpl(UserRepository userRepository, RentalContractRepository rentalContractRepository) {
        this.userRepository = userRepository;
        this.rentalContractRepository = rentalContractRepository;
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
        if (!userRepository.existsById(user.getId())) {
            throw new ResourceNotFoundException("User not found with id: " + user.getId());
        }

        // Verifică dacă username-ul sau email-ul sunt deja folosite de alt utilizator
        User existingUserByUsername = userRepository.findByUsername(user.getUsername()).orElse(null);
        if (existingUserByUsername != null && !existingUserByUsername.getId().equals(user.getId())) {
            throw new IllegalArgumentException("Username already exists: " + user.getUsername());
        }

        User existingUserByEmail = userRepository.findByEmail(user.getEmail()).orElse(null);
        if (existingUserByEmail != null && !existingUserByEmail.getId().equals(user.getId())) {
            throw new IllegalArgumentException("Email already exists: " + user.getEmail());
        }

        return userRepository.save(user);
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
        // Verifică dacă utilizatorul există
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
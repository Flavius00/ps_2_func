package com.example.demo.service.impl;

import com.example.demo.model.RentalContract;
import com.example.demo.model.Tenant;
import com.example.demo.model.User;
import com.example.demo.repository.RentalContractRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RentalContractRepository rentalContractRepository;

    public UserServiceImpl(UserRepository userRepository, RentalContractRepository rentalContractRepository) {
        this.userRepository = userRepository;
        this.rentalContractRepository = rentalContractRepository;
    }

    @Override
    public User addUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public User updateUser(User user) {
        return userRepository.update(user);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findAll().stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<RentalContract> getUserContracts(Long userId) {
        return rentalContractRepository.findByUserId(userId);
    }
}
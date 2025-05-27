package com.example.demo.service.impl;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.LoginService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LoginServiceImpl implements LoginService {
    private final UserRepository userRepository;

    public LoginServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User login(String username, String password) {
        System.out.println("Login attempt - Username: " + username + ", Password: " + password);

        // Debug: Print all available users
        List<User> allUsers = userRepository.findAll();
        System.out.println("All users in repository (" + allUsers.size() + "):");
        allUsers.forEach(user ->
                System.out.println("User: " + user.getUsername() + ", Password: " + user.getPassword() + ", ID: " + user.getId())
        );

        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            System.out.println("User found, checking password...");
            System.out.println("Stored password: '" + user.getPassword() + "'");
            System.out.println("Provided password: '" + password + "'");

            if (user.getPassword().equals(password)) {
                System.out.println("Password match, login successful");
                return user;
            } else {
                System.out.println("Password mismatch - '" + password + "' != '" + user.getPassword() + "'");
            }
        } else {
            System.out.println("User not found with username: " + username);
        }

        return null;
    }
}
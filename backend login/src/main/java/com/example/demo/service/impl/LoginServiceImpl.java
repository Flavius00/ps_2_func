package com.example.demo.service.impl;

import com.example.demo.model.UserLogIn;
import com.example.demo.repository.UserLogInRepository;
import com.example.demo.service.LoginService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LoginServiceImpl implements LoginService {
    private final UserLogInRepository userRepository;

    public LoginServiceImpl(UserLogInRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserLogIn login(String username, String password) {
        System.out.println("Login attempt - Username: " + username + ", Password: " + password);

        // Debug: Print all available users
        List<UserLogIn> allUsers = userRepository.getAllUsers();
        System.out.println("All users in repository (" + allUsers.size() + "):");
        allUsers.forEach(user ->
                System.out.println("User: " + user.getUsername() + ", Password: " + user.getPassword() + ", ID: " + user.getId())
        );

        UserLogIn user = userRepository.findByUsername(username);

        if (user != null) {
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
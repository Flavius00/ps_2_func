package com.example.demo.repository;

import com.example.demo.model.UserLogIn;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class UserLogInRepository {
    private final List<UserLogIn> users = new ArrayList<>();

    public UserLogInRepository() {
        // We'll let MockDataServiceLogInImpl handle the initial data
    }

    public UserLogIn findByUsername(String username) {
        System.out.println("Looking for username: " + username);
        System.out.println("Available users: ");
        users.forEach(u -> System.out.println("- " + u.getUsername()));

        UserLogIn foundUser = users.stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst().orElse(null);

        System.out.println("Found user: " + (foundUser != null ? foundUser.getUsername() : "null"));
        return foundUser;
    }

    public void saveAll(List<UserLogIn> usersToAdd) {
        if (usersToAdd != null && !usersToAdd.isEmpty()) {
            // Clear the list first to avoid duplicates when called multiple times
            if (users.isEmpty()) {
                users.addAll(usersToAdd);
            } else {
                // Add only users that don't exist yet (by username)
                for (UserLogIn newUser : usersToAdd) {
                    boolean exists = users.stream()
                            .anyMatch(user -> user.getUsername().equals(newUser.getUsername()));
                    if (!exists) {
                        users.add(newUser);
                    }
                }
            }
        }
    }

    // For debugging
    public List<UserLogIn> getAllUsers() {
        return new ArrayList<>(users);
    }
}
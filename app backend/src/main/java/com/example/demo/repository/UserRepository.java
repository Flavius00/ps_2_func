package com.example.demo.repository;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.User;
import lombok.Getter;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@Getter
public class UserRepository {
    private List<User> users = new ArrayList<>();

    public User save(User user) {
        if (user == null) {
            throw new IllegalArgumentException("The user cannot be null!");
        }
        users.add(user);
        return user;
    }

    public void saveAll(List<User> newUsers) {
        users.addAll(newUsers);
    }

    public List<User> findAll() {
        return users;
    }

    public User findById(Long id) {
        return users.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public User findByUsername(String username) {
        return users.stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    public User update(User user) {
        User oldUser = findById(user.getId());

        if (oldUser == null) {
            throw new ResourceNotFoundException("User with the following id not found: " + user.getId());
        }

        oldUser.setName(user.getName());
        oldUser.setUsername(user.getUsername());
        oldUser.setPassword(user.getPassword());
        oldUser.setRole(user.getRole());
        return oldUser;
    }

    public void deleteById(Long id) {
        users.removeIf(user -> user.getId().equals(id));
    }
}
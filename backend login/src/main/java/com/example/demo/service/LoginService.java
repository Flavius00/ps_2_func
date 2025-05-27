package com.example.demo.service;

import com.example.demo.model.User;

public interface LoginService {
    User login(String username, String password);
}
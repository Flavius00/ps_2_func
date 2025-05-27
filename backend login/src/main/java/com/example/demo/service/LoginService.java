package com.example.demo.service;

import com.example.demo.model.UserLogIn;

public interface LoginService {
    UserLogIn login(String username, String password);
}

package com.example.demo.service;

import com.example.demo.dto.request.LoginRequestDTO;
import com.example.demo.dto.request.RegisterRequestDTO;
import com.example.demo.dto.response.LoginResponseDTO;
import com.example.demo.model.User;

public interface AuthService {
    LoginResponseDTO login(LoginRequestDTO loginRequest);
    User register(RegisterRequestDTO registerRequest);
    boolean validateToken(String token);
    void logout(String token);
}
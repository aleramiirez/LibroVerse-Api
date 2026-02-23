package com.biblioteca.backend.service;

import com.biblioteca.backend.dto.auth.AuthResponse;
import com.biblioteca.backend.dto.auth.LoginRequest;
import com.biblioteca.backend.dto.auth.RegisterRequest;

public interface AuthServiceI {
    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}

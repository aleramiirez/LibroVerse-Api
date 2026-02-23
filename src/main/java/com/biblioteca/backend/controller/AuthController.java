package com.biblioteca.backend.controller;

import com.biblioteca.backend.dto.auth.AuthResponse;
import com.biblioteca.backend.dto.auth.LoginRequest;
import com.biblioteca.backend.dto.auth.RegisterRequest;
import com.biblioteca.backend.service.AuthServiceI;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*") // Controlado por el WebConfig global, pero añadido por redundancia si fuese
                            // necesario
public class AuthController {

    private final AuthServiceI authService;

    public AuthController(AuthServiceI authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}

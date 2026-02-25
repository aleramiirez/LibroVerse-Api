package com.biblioteca.backend.controller;

import com.biblioteca.backend.dto.auth.AuthResponse;
import com.biblioteca.backend.dto.auth.LoginRequest;
import com.biblioteca.backend.dto.auth.RegisterRequest;
import com.biblioteca.backend.service.AuthServiceI;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST encargado de gestionar la autenticación de la aplicación.
 * <p>
 * Proporciona los endpoints públicos necesarios para que los usuarios puedan
 * registrarse e iniciar sesión, devolviendo en ambos casos un token JWT
 * para las futuras peticiones autenticadas.
 * </p>
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthServiceI authService;

    /**
     * Constructor del controlador que inyecta el servicio de autenticación.
     * * @param authService Servicio que contiene la lógica de negocio para registro y login.
     */
    public AuthController(AuthServiceI authService) {
        this.authService = authService;
    }

    /**
     * Endpoint para registrar un nuevo usuario en la plataforma.
     * * @param request Objeto que contiene los datos requeridos para el registro (email, contraseña, etc.).
     * Se valida automáticamente con la anotación @Valid.
     * @return ResponseEntity con el objeto AuthResponse que incluye el token JWT generado,
     * junto con un código de estado 200 OK si el registro es exitoso.
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    /**
     * Endpoint para autenticar a un usuario existente.
     * * @param request Objeto que contiene las credenciales del usuario (email/username y contraseña).
     * Se valida automáticamente con la anotación @Valid.
     * @return ResponseEntity con el objeto AuthResponse que incluye el token JWT necesario
     * para consumir el resto de la API, junto con un código de estado 200 OK.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}

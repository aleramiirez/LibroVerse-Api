package com.biblioteca.backend.controller;

import com.biblioteca.backend.dto.auth.AuthResponse;
import com.biblioteca.backend.dto.auth.LoginRequest;
import com.biblioteca.backend.dto.auth.RegisterRequest;
import com.biblioteca.backend.service.AuthServiceI;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "Endpoints para el registro y acceso de usuarios")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthServiceI authService;

    /**
     * Endpoint para registrar un nuevo usuario en la plataforma.
     * * @param request Objeto que contiene los datos requeridos para el registro (email, contraseña, etc.).
     * Se valida automáticamente con la anotación @Valid.
     * @return ResponseEntity con el objeto AuthResponse que incluye el token JWT generado,
     * junto con un código de estado 200 OK si el registro es exitoso.
     */
    @Operation(summary = "Registrar nuevo usuario", description = "Crea una cuenta y devuelve un token JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario registrado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de registro inválidos o email ya existente")
    })
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody final RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    /**
     * Endpoint para autenticar a un usuario existente.
     * * @param request Objeto que contiene las credenciales del usuario (email/username y contraseña).
     * Se valida automáticamente con la anotación @Valid.
     * @return ResponseEntity con el objeto AuthResponse que incluye el token JWT necesario
     * para consumir el resto de la API, junto con un código de estado 200 OK.
     */
    @Operation(summary = "Iniciar sesión", description = "Valida credenciales y devuelve un token JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Autenticación exitosa"),
            @ApiResponse(responseCode = "401", description = "Credenciales incorrectas")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody final LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}

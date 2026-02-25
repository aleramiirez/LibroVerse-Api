package com.biblioteca.backend.service;

import com.biblioteca.backend.dto.auth.AuthResponse;
import com.biblioteca.backend.dto.auth.LoginRequest;
import com.biblioteca.backend.dto.auth.RegisterRequest;

/**
 * Interfaz que define los servicios de autenticación y gestión de usuarios.
 * <p>
 * Establece los métodos necesarios para el flujo de acceso a la aplicación,
 * incluyendo el registro de nuevos perfiles y la validación de credenciales existentes.
 * </p>
 */
public interface AuthServiceI {

    /**
     * Procesa el registro de un nuevo usuario en el sistema.
     * @param request Objeto DTO con los datos de registro (nombre, email, password).
     * @return AuthResponse que contiene el token JWT generado y los datos básicos del usuario.
     */
    AuthResponse register(RegisterRequest request);

    /**
     * Valida las credenciales de un usuario para permitirle el acceso.
     * @param request Objeto DTO con las credenciales de inicio de sesión.
     * @return AuthResponse con el token de acceso JWT si la autenticación es correcta.
     */
    AuthResponse login(LoginRequest request);
}

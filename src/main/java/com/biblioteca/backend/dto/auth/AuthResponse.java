package com.biblioteca.backend.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Data Transfer Object (DTO) para la respuesta de autenticación.
 * <p>
 * Contiene el token de seguridad necesario para las peticiones subsecuentes
 * y la información básica del perfil del usuario para personalizar la interfaz.
 * </p>
 */
@Data
@AllArgsConstructor
public class AuthResponse {

    /** Token JWT generado por el servidor para autorizar las peticiones del cliente. */
    private String token;

    /** Nombre del usuario autenticado para mostrar en el encabezado de la aplicación. */
    private String name;

    /** Correo electrónico del usuario vinculado a la sesión actual. */
    private String email;
}

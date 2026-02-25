package com.biblioteca.backend.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO que encapsula las credenciales necesarias para iniciar sesión.
 * <p>
 * Incluye validaciones de Jakarta Bean Validation para asegurar que los datos
 * tengan el formato correcto antes de ser procesados por el servicio de seguridad.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    /** Correo electrónico del usuario. Debe tener un formato válido y no estar vacío. */
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Debe ser un email válido")
    private String email;

    /** Contraseña del usuario. Campo obligatorio para la validación de seguridad. */
    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
}

package com.biblioteca.backend.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO que contiene los datos requeridos para dar de alta a un nuevo usuario.
 * <p>
 * Define la estructura del JSON esperado por el endpoint de registro,
 * garantizando la integridad de los datos mínimos necesarios (nombre, email y password).
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    /** Nombre completo o alias que el usuario desea mostrar en su perfil. */
    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    /** Correo electrónico único para la nueva cuenta. Se valida el formato de red. */
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Debe ser un email válido")
    private String email;

    /** Contraseña elegida por el usuario. Se almacenará encriptada en la base de datos. */
    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
}

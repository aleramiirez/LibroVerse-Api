package com.biblioteca.backend.exception;

/**
 * Excepción personalizada que se lanza cuando se intenta registrar un usuario con un email duplicado.
 * <p>
 * Se utiliza en la capa de servicio de autenticación para validar la unicidad del identificador
 * de usuario (email) antes de proceder con la persistencia en la base de datos.
 * </p>
 */
public class UserAlreadyExistsException extends RuntimeException {

    /**
     * Construye una nueva excepción con un mensaje que indica que el usuario ya existe.
     * @param message Mensaje detallado que será capturado por el {@link GlobalExceptionHandler}
     * para devolver un código de estado 409 (Conflict).
     */
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}

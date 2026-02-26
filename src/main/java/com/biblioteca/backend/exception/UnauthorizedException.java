package com.biblioteca.backend.exception;

/**
 * Excepción personalizada para representar fallos de autorización y falta de contexto de seguridad.
 * <p>
 * Se dispara principalmente cuando se intenta realizar una operación protegida (como obtener el ID
 * del usuario actual) pero no existe una sesión válida o el token JWT ha expirado.
 * </p>
 */
public class UnauthorizedException extends RuntimeException {

    /**
     * Construye una nueva excepción con un mensaje explicativo sobre la falta de autorización.
     * @param message Mensaje que será procesado por el manejador global para devolver
     * un código de estado 401 (Unauthorized).
     */
    public UnauthorizedException(String message) {
        super(message);
    }
}

package com.biblioteca.backend.exception;

import com.biblioteca.backend.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

/**
 * Manejador global de excepciones para la API.
 * <p>
 * Utiliza la anotación {@code @RestControllerAdvice} para interceptar las excepciones lanzadas
 * por cualquier controlador y transformarlas en respuestas HTTP estructuradas en formato JSON.
 * Esto garantiza que el frontend siempre reciba un formato de error consistente.
 * </p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Captura las excepciones de tipo {@link ResourceNotFoundException}.
     * <p>
     * Se activa cuando un usuario intenta acceder a un libro, saga o recurso que no existe.
     * </p>
     * @param ex La excepción capturada.
     * @return Una respuesta con código de estado 404 (Not Found) y los detalles del error.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    /**
     * Captura cualquier excepción genérica no controlada que ocurra en el sistema.
     * <p>
     * Actúa como una red de seguridad para errores inesperados (Error 500), evitando
     * que se filtre información técnica sensible hacia el cliente.
     * </p>
     * @param ex La excepción de tipo Exception capturada.
     * @return Una respuesta con código de estado 500 (Internal Server Error) y un mensaje genérico.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(final Exception ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Ocurrió un error inesperado en el servidor",
                LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

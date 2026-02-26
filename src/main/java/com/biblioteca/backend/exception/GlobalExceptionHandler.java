package com.biblioteca.backend.exception;

import com.biblioteca.backend.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

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
     * Captura las excepciones de tipo {@link UserAlreadyExistsException}.
     * <p>
     * Se dispara durante el proceso de registro si el correo electrónico ya se encuentra
     * en uso por otro usuario en la base de datos.
     * </p>
     * @param ex La excepción de duplicidad de usuario capturada.
     * @return Una respuesta con código de estado 409 (Conflict) y el mensaje explicativo.
     */
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExists(UserAlreadyExistsException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                ex.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    /**
     * Captura las excepciones de tipo {@link UnauthorizedException}.
     * <p>
     * Se lanza cuando una operación requiere un usuario autenticado y este no se encuentra
     * presente en el contexto de seguridad o el token es inválido.
     * </p>
     * @param ex La excepción de falta de autorización capturada.
     * @return Una respuesta con código de estado 401 (Unauthorized) y los detalles del fallo.
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(UnauthorizedException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                ex.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Maneja los errores de validación de los campos de entrada (@Valid).
     * <p>
     * Intercepta fallos en las restricciones aplicadas a los DTOs (como @NotBlank o @Email),
     * recopilando todos los errores de campo en un único mensaje legible para el cliente.
     * </p>
     * @param ex Excepción lanzada cuando los argumentos de un endpoint no son válidos.
     * @return Una respuesta con código de estado 400 (Bad Request) detallando los campos erróneos.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        // Extraemos todos los errores de los campos y los unimos en un solo String
        String errorMessages = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Error en los datos enviados: [" + errorMessages + "]",
                LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
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
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Ocurrió un error inesperado en el servidor: " + ex.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

package com.biblioteca.backend.dto;

import java.time.LocalDateTime;

/**
 * DTO estandarizado para las respuestas de error de la API.
 * <p>
 * Se utiliza dentro del GlobalExceptionHandler para devolver una estructura JSON
 * consistente cuando ocurre una excepción, facilitando el manejo de errores en el Frontend.
 * </p>
 * @param status Código de estado HTTP (ej. 404, 500).
 * @param message Mensaje descriptivo del error ocurrido.
 * @param timestamp Fecha y hora exacta en la que se produjo la excepción.
 */
public record ErrorResponse(
        int status,
        String message,
        LocalDateTime timestamp
) {}

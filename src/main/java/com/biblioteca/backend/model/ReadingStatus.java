package com.biblioteca.backend.model;

import lombok.Getter;

/**
 * Representa el estado actual de lectura de un libro en la biblioteca.
 * Se utiliza para filtrar y calcular estadísticas en el Dashboard.
 */
@Getter
public enum ReadingStatus {
    PENDING("Pendiente"),
    READING("Leyendo"),
    FINISHED("Terminado");

    private final String displayValue;

    ReadingStatus(String displayValue) {
        this.displayValue = displayValue;
    }
}
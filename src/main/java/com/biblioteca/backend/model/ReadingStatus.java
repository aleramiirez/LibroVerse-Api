package com.biblioteca.backend.model;

import lombok.Getter;

/**
 * Enumeración que define los posibles estados de lectura de un libro en la biblioteca.
 * <p>
 * Cada estado incluye un valor de visualización (displayValue) amigable para el usuario
 * que puede ser utilizado directamente por el frontend.
 * </p>
 */
@Getter
public enum ReadingStatus {
    /** El libro ha sido añadido a la colección pero la lectura aún no ha comenzado. */
    PENDING("Pendiente"),

    /** El usuario se encuentra leyendo el libro actualmente. */
    READING("Leyendo"),

    /** El usuario ha completado la lectura del libro. */
    FINISHED("Terminado");

    /**
     * Valor textual legible del estado para mostrar en la interfaz de usuario.
     */
    private final String displayValue;

    /**
     * Constructor del enumerado para asignar el texto descriptivo a cada estado.
     * @param displayValue Texto descriptivo en español.
     */
    ReadingStatus(String displayValue) {
        this.displayValue = displayValue;
    }
}
package com.biblioteca.backend.dto;

import com.biblioteca.backend.model.Book;

/**
 * Data Transfer Object (DTO) que agrupa las métricas principales para el Dashboard.
 * <p>
 * Este record encapsula la información estadística de lectura del usuario,
 * permitiendo que el Frontend renderice la pantalla de inicio con un único objeto.
 * </p>
 * @param currentBook El libro que el usuario está leyendo actualmente (si existe).
 * @param totalBooksFinished Cantidad total de libros completados en la biblioteca.
 * @param averageReadingDays Promedio de días invertidos por el usuario en terminar un libro.
 * @param favoriteGenre El género literario que más veces aparece en los libros terminados.
 */
public record DashboardResponse(
                Book currentBook,
                long totalBooksFinished,
                Long averageReadingDays,
                String favoriteGenre) {
}

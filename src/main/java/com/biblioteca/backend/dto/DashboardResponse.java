package com.biblioteca.backend.dto;

import com.biblioteca.backend.model.Book;

/**
 * DTO que agrupa las métricas principales para la pantalla de inicio del
 * Frontend.
 */
public record DashboardResponse(
                Book currentBook,
                long totalBooksFinished,
                Long averageReadingDays,
                String favoriteGenre) {
}
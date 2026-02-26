package com.biblioteca.backend.service.impl;

import com.biblioteca.backend.dto.DashboardResponse;
import com.biblioteca.backend.model.Book;
import com.biblioteca.backend.model.ReadingStatus;
import com.biblioteca.backend.repository.BookRepository;
import com.biblioteca.backend.security.SecurityUtils;
import com.biblioteca.backend.service.DashboardServiceI;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementación del servicio de métricas para la pantalla de inicio (Dashboard).
 * <p>
 * Esta clase se encarga de agrupar y recopilar las estadísticas.
 * Ha sido optimizada delegando las operaciones matemáticas pesadas (promedios y agrupaciones)
 * directamente a la base de datos (SQL) para reducir el consumo de RAM en la máquina virtual.
 * </p>
 */
@RequiredArgsConstructor
@Service
public class DashboardServiceImpl implements DashboardServiceI {

    /** Repositorio para el acceso a los datos de los libros. */
    private final BookRepository bookRepository;


    /**
     * Calcula y recopila las estadísticas de lectura para el panel de control del usuario.
     * <p>
     * El proceso sigue una estrategia de optimización híbrida:
     * 1. Recupera el libro actual mediante una consulta filtrada por estado.
     * 2. Delega el conteo de libros finalizados, el cálculo del promedio de días de lectura
     * y la determinación del género favorito al motor de la base de datos (PostgreSQL).
     * </p>
     * <p>
     * Esta delegación a SQL evita la carga masiva de objetos en la memoria RAM y reduce
     * drásticamente el uso de CPU en la aplicación Java, permitiendo un rendimiento
     * fluido en entornos con recursos limitados (Micro VMs).
     * </p>
     * * @return Un objeto {@link DashboardResponse} con el resumen de actividad del usuario.
     */
    @Override
    public DashboardResponse getDashboardStats() {
        Long currentUserId = SecurityUtils.getCurrentUserId();

        // 1. Obtener el libro actual
        List<Book> readingBooks = bookRepository.findByUserIdAndStatus(currentUserId, ReadingStatus.READING);
        Book currentBook = readingBooks.isEmpty() ? null : readingBooks.get(0);

        // 2. Contar libros terminados (Resolución instantánea en SQL, no carga objetos a memoria)
        long totalBooksFinished = bookRepository.countByUserIdAndStatus(currentUserId, ReadingStatus.FINISHED);

        // 3. Promedio de días (Procesado matemáticamente por PostgreSQL)
        Double avgDaysSql = bookRepository.getAverageReadingDays(currentUserId);
        Long averageReadingDays = null;
        if (avgDaysSql != null) {
            // Redondear el resultado y asegurar un mínimo de 1 día (para libros leídos el mismo día)
            averageReadingDays = Math.max(Math.round(avgDaysSql), 1L);
        }

        // 4. Género favorito (PostgreSQL hace los JOINs y el ordenamiento)
        String favoriteGenre = bookRepository.findFavoriteGenreByUserId(currentUserId);

        // Retornamos el objeto montado
        return new DashboardResponse(currentBook, totalBooksFinished, averageReadingDays, favoriteGenre);
    }
}

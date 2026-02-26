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
 * Esta clase se encarga de agrupar, procesar y calcular las estadísticas de lectura
 * personalizadas del usuario, interactuando con la base de datos a través de
 * {@link BookRepository}.
 * </p>
 */
@RequiredArgsConstructor
@Service
public class DashboardServiceImpl implements DashboardServiceI {

    /** Repositorio para el acceso a los datos de los libros. */
    private final BookRepository bookRepository;

    /**
     * Calcula y recopila las estadísticas principales del usuario en tiempo real.
     * <p>
     * El proceso realiza las siguientes operaciones:
     * 1. Recupera el libro que el usuario está leyendo actualmente.
     * 2. Contabiliza el total de libros finalizados.
     * 3. Calcula el promedio de días invertidos en cada lectura basándose en las fechas de inicio y fin.
     * 4. Identifica el género literario más frecuente entre los libros terminados.
     * </p>
     * * @return Un objeto {@link DashboardResponse} que contiene las métricas calculadas
     * para ser visualizadas en el frontend.
     */
    public DashboardResponse getDashboardStats() {
        // Obtención del ID del usuario autenticado en el contexto de seguridad
        Long currentUserId = SecurityUtils.getCurrentUserId();

        // 1. Obtener el libro que se está leyendo actualmente
        List<Book> readingBooks = bookRepository.findByUserIdAndStatus(currentUserId, ReadingStatus.READING);
        Book currentBook = readingBooks.isEmpty() ? null : readingBooks.get(0);

        // 2. Obtener todos los libros terminados para el cálculo de estadísticas
        List<Book> finishedBooks = bookRepository.findByUserIdAndStatus(currentUserId, ReadingStatus.FINISHED);
        long totalBooksFinished = finishedBooks.size();

        // variables auxiliares para el cálculo de promedio y género favorito
        long totalReadingDays = 0;
        int booksWithDates = 0;
        Map<String, Long> genreCounts = new HashMap<>();

        // 3. Procesamiento de libros terminados
        for (Book book : finishedBooks) {
            // Cálculo de días de lectura si existen fechas de inicio y fin
            if (book.getStartDate() != null && book.getEndDate() != null) {
                long days = ChronoUnit.DAYS.between(book.getStartDate(), book.getEndDate());
                // Se garantiza al menos 1 día para evitar promedios de cero en lecturas rápidas
                totalReadingDays += Math.max(days, 1);
                booksWithDates++;
            }

            // Conteo de frecuencias por género
            if (book.getGenres() != null) {
                for (com.biblioteca.backend.model.Genre genre : book.getGenres()) {
                    genreCounts.put(genre.getName(), genreCounts.getOrDefault(genre.getName(), 0L) + 1);
                }
            }
        }

        // Cálculo del promedio de días de lectura
        Long averageReadingDays = booksWithDates > 0 ? totalReadingDays / booksWithDates : null;

        // Determinación del género favorito (el de mayor frecuencia)
        String favoriteGenre = null;
        long maxCount = 0;
        for (Map.Entry<String, Long> entry : genreCounts.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                favoriteGenre = entry.getKey();
            }
        }

        // Empaquetado y retorno de los resultados calculados
        return new DashboardResponse(currentBook, totalBooksFinished, averageReadingDays, favoriteGenre);
    }
}

package com.biblioteca.backend.service.impl;

import com.biblioteca.backend.dto.DashboardResponse;
import com.biblioteca.backend.model.Book;
import com.biblioteca.backend.model.ReadingStatus;
import com.biblioteca.backend.repository.BookRepository;
import com.biblioteca.backend.security.SecurityUtils;
import com.biblioteca.backend.service.DashboardServiceI;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementación del servicio de métricas para la pantalla de inicio
 * (Dashboard).
 * Se encarga de agrupar y calcular las estadísticas de lectura del usuario
 * interactuando con la base de datos a través de BookRepository.
 */
@Service
public class DashboardServiceImpl implements DashboardServiceI {

    private final BookRepository bookRepository;

    /**
     * Constructor para la inyección de dependencias.
     * * @param bookRepository Repositorio para acceder a los datos de los libros.
     */
    public DashboardServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    /**
     * Calcula y recopila las estadísticas principales del usuario en tiempo real.
     * <p>
     * Las operaciones incluyen:
     * 1. Buscar el libro que se está leyendo actualmente.
     * 2. Contar los libros que se han terminado en el año en curso.
     * 3. Determinar cuál es el siguiente volumen si el libro actual pertenece a una
     * saga.
     * </p>
     * * @return Un objeto {@link DashboardResponse} que empaqueta todos los datos
     * calculados
     * listos para ser consumidos por el frontend.
     */
    public DashboardResponse getDashboardStats() {
        Long currentUserId = SecurityUtils.getCurrentUserId();

        // 1. Obtener el libro que se está leyendo actualmente
        List<Book> readingBooks = bookRepository.findByUserIdAndStatus(currentUserId, ReadingStatus.READING);
        Book currentBook = readingBooks.isEmpty() ? null : readingBooks.get(0);

        // 2. Obtener todos los libros terminados
        List<Book> finishedBooks = bookRepository.findByUserIdAndStatus(currentUserId, ReadingStatus.FINISHED);
        long totalBooksFinished = finishedBooks.size();

        // 3. Calcular promedio de días de lectura y el género favorito
        long totalReadingDays = 0;
        int booksWithDates = 0;
        Map<String, Long> genreCounts = new HashMap<>();

        for (Book book : finishedBooks) {
            // Promedio de lectura
            if (book.getStartDate() != null && book.getEndDate() != null) {
                long days = ChronoUnit.DAYS.between(book.getStartDate(), book.getEndDate());
                totalReadingDays += Math.max(days, 1); // Al menos 1 día de lectura
                booksWithDates++;
            }

            // Género favorito
            if (book.getGenres() != null) {
                for (com.biblioteca.backend.model.Genre genre : book.getGenres()) {
                    genreCounts.put(genre.getName(), genreCounts.getOrDefault(genre.getName(), 0L) + 1);
                }
            }
        }

        Long averageReadingDays = booksWithDates > 0 ? totalReadingDays / booksWithDates : null;

        String favoriteGenre = null;
        long maxCount = 0;
        for (Map.Entry<String, Long> entry : genreCounts.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                favoriteGenre = entry.getKey();
            }
        }

        // Devolver todo empaquetado
        return new DashboardResponse(currentBook, totalBooksFinished, averageReadingDays, favoriteGenre);
    }
}
package com.biblioteca.backend.service.impl;

import com.biblioteca.backend.dto.DashboardResponse;
import com.biblioteca.backend.model.Book;
import com.biblioteca.backend.model.ReadingStatus;
import com.biblioteca.backend.repository.BookRepository;
import com.biblioteca.backend.service.DashboardServiceI;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

/**
 * Implementación del servicio de métricas para la pantalla de inicio (Dashboard).
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
     * 3. Determinar cuál es el siguiente volumen si el libro actual pertenece a una saga.
     * </p>
     * * @return Un objeto {@link DashboardResponse} que empaqueta todos los datos calculados
     * listos para ser consumidos por el frontend.
     */
    @Override
    public DashboardResponse getDashboardStats() {
        // 1. Obtener el libro que se está leyendo actualmente
        List<Book> readingBooks = bookRepository.findByStatus(ReadingStatus.READING);
        Book currentBook = readingBooks.isEmpty() ? null : readingBooks.get(0);

        // 2. Calcular los libros leídos este año (1 de Enero al 31 de Diciembre)
        LocalDate startOfYear = LocalDate.now().with(Month.JANUARY).withDayOfMonth(1);
        LocalDate endOfYear = LocalDate.now().with(Month.DECEMBER).withDayOfMonth(31);
        long booksReadThisYear = bookRepository.countByStatusAndEndDateBetween(
                ReadingStatus.FINISHED, startOfYear, endOfYear
        );

        // 3. Buscar el siguiente volumen de la saga (si aplica)
        Book nextBook = null;
        if (currentBook != null && currentBook.getSaga() != null && currentBook.getIndexInSaga() != null) {
            nextBook = bookRepository.findFirstBySagaIdAndIndexInSagaGreaterThanOrderByIndexInSagaAsc(
                    currentBook.getSaga().getId(),
                    currentBook.getIndexInSaga()
            ).orElse(null);
        }

        // Devolver todo empaquetado
        return new DashboardResponse(currentBook, booksReadThisYear, nextBook);
    }
}
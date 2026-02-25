package com.biblioteca.backend.repository;

import com.biblioteca.backend.model.Book;
import com.biblioteca.backend.model.ReadingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para la entidad Book.
 * <p>
 * Contiene la lógica de acceso a datos para los libros, incluyendo consultas
 * complejas para el Dashboard y optimizaciones de carga mediante EntityGraph.
 * </p>
 */
public interface BookRepository extends JpaRepository<Book, Long> {

    /**
     * Obtiene los libros filtrados por usuario y su estado de lectura.
     * @param userId ID del usuario propietario.
     * @param status Estado (PENDING, READING, FINISHED).
     * @return Lista de libros que coinciden con el estado.
     */
    List<Book> findByUserIdAndStatus(Long userId, ReadingStatus status);

    /**
     * Calcula la cantidad de libros terminados en un rango de fechas para un
     * usuario.
     */
    long countByUserIdAndStatusAndEndDateBetween(Long userId, ReadingStatus status, LocalDate start, LocalDate end);

    /**
     * Busca libros por título y usuario.
     */
    List<Book> findByUserIdAndTitleContainingIgnoreCase(Long userId, String title);

    /**
     * Encuentra el siguiente volumen disponible de una saga que el usuario está
     * leyendo.
     */
    Optional<Book> findFirstByUserIdAndSagaIdAndIndexInSagaGreaterThanOrderByIndexInSagaAsc(Long userId, Long sagaId,
            Integer currentIndex);

    /**
     * Obtiene todos los libros de un usuario.
     */
    @EntityGraph(attributePaths = {"author", "saga", "genres"})
    List<Book> findByUserId(Long userId);
}

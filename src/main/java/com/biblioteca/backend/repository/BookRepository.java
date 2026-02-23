package com.biblioteca.backend.repository;

import com.biblioteca.backend.model.Book;
import com.biblioteca.backend.model.ReadingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para la entidad Book.
 * Contiene métodos de consulta optimizados para alimentar el Dashboard
 * principal.
 */
public interface BookRepository extends JpaRepository<Book, Long> {

    /**
     * Obtiene los libros según su estado actual y el id del usuario.
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
    List<Book> findByUserId(Long userId);
}
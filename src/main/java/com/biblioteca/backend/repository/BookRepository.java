package com.biblioteca.backend.repository;

import com.biblioteca.backend.model.Book;
import com.biblioteca.backend.model.ReadingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;

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
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    /**
     * Obtiene los libros filtrados por usuario y su estado de lectura.
     * @param userId ID del usuario propietario.
     * @param status Estado (PENDING, READING, FINISHED).
     * @return Lista de libros que coinciden con el estado.
     */
    @EntityGraph(attributePaths = {"author", "saga", "genres"})
    List<Book> findByUserIdAndStatus(Long userId, ReadingStatus status);

    /**
     * Cuenta cuántos libros ha terminado un usuario en un periodo de tiempo.
     * Útil para las estadísticas de "Libros leídos este mes/año" en el Dashboard.
     * @param userId ID del usuario.
     * @param status Debe ser ReadingStatus.FINISHED.
     * @param start Fecha de inicio del periodo.
     * @param end Fecha de fin del periodo.
     * @return Cantidad total de libros terminados.
     */
    long countByUserIdAndStatusAndEndDateBetween(Long userId, ReadingStatus status, LocalDate start, LocalDate end);

    /**
     * Busca libros por una coincidencia parcial en el título, ignorando mayúsculas.
     * @param userId ID del usuario.
     * @param title Texto a buscar en el título.
     * @return Lista de libros que contienen el texto.
     */
    List<Book> findByUserIdAndTitleContainingIgnoreCase(Long userId, String title);

    /**
     * Encuentra el siguiente volumen de una saga.
     * Busca el libro con el índice inmediatamente superior al actual dentro de la misma saga.
     * @param userId ID del usuario.
     * @param sagaId ID de la saga.
     * @param currentIndex Índice del libro actual.
     * @return El siguiente libro en el orden de la saga.
     */
    Optional<Book> findFirstByUserIdAndSagaIdAndIndexInSagaGreaterThanOrderByIndexInSagaAsc(Long userId, Long sagaId,
            Integer currentIndex);

    /**
     * Recupera la biblioteca completa de un usuario.
     * Se utiliza EntityGraph para cargar Autor, Saga y Géneros en una sola consulta SQL (JOIN).
     * @param userId ID del usuario.
     * @return Lista completa de libros del usuario.
     */
    @EntityGraph(attributePaths = {"author", "saga", "genres"})
    List<Book> findByUserId(Long userId);
}

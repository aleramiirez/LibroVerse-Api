package com.biblioteca.backend.repository;

import com.biblioteca.backend.model.Book;
import com.biblioteca.backend.model.ReadingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
     * Recupera la biblioteca de un usuario de forma paginada y optimizada.
     * <p>
     * Se utiliza EntityGraph para cargar el Autor y la Saga en una sola consulta SQL (JOIN).
     * </p>
     * @param userId ID del usuario propietario.
     * @param pageable Configuración de paginación (número de página, tamaño y orden).
     * @return Página de libros ({@link Page}) del usuario.
     */
    @EntityGraph(attributePaths = {"author", "saga"})
    Page<Book> findByUserId(Long userId, Pageable pageable);

    /**
     * Cuenta el total de libros que están en un estado específico.
     * @param userId ID del usuario.
     * @param status Estado de lectura (ej. FINISHED).
     * @return Cantidad de libros.
     */
    long countByUserIdAndStatus(Long userId, ReadingStatus status);

    /**
     * Calcula el promedio de días de lectura restando la fecha de fin y la de inicio
     * directamente en el motor de la base de datos (PostgreSQL).
     * @param userId ID del usuario.
     * @return Promedio de días (puede ser nulo si no hay fechas).
     */
    @Query(value = "SELECT AVG(end_date - start_date) FROM books " +
            "WHERE user_id = :userId AND status = 'FINISHED' " +
            "AND start_date IS NOT NULL AND end_date IS NOT NULL",
            nativeQuery = true)
    Double getAverageReadingDays(@Param("userId") Long userId);

    /**
     * Obtiene el género más repetido entre los libros terminados del usuario.
     * Agrupa, ordena por los más leídos y devuelve solo el primer resultado (LIMIT 1).
     * @param userId ID del usuario.
     * @return Nombre del género favorito o null si no hay datos.
     */
    @Query(value = "SELECT g.name FROM genres g " +
            "JOIN book_genre bg ON g.id = bg.genre_id " +
            "JOIN books b ON b.id = bg.book_id " +
            "WHERE b.user_id = :userId AND b.status = 'FINISHED' " +
            "GROUP BY g.name ORDER BY COUNT(g.id) DESC LIMIT 1",
            nativeQuery = true)
    String findFavoriteGenreByUserId(@Param("userId") Long userId);

    /**
     * Desvincula todos los libros asociados a una saga de forma masiva.
     * @param sagaId ID de la saga que se va a eliminar.
     */
    @Modifying
    @Query("UPDATE Book b SET b.saga = null, b.indexInSaga = null WHERE b.saga.id = :sagaId")
    void unlinkBooksFromSaga(@Param("sagaId") Long sagaId);
}

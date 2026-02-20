package com.biblioteca.backend.repository;

import com.biblioteca.backend.model.Book;
import com.biblioteca.backend.model.ReadingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para la entidad Book.
 * Contiene métodos de consulta optimizados para alimentar el Dashboard principal.
 */
public interface BookRepository extends JpaRepository<Book, Long> {

    /**
     * Obtiene los libros según su estado actual.
     * @param status El estado de lectura (ej. READING para ver el libro actual).
     * @return Lista de libros que coinciden con el estado.
     */
    List<Book> findByStatus(ReadingStatus status);

    /**
     * Calcula la cantidad de libros terminados en un rango de fechas.
     * Ideal para métricas anuales de lectura.
     * @param start Fecha de inicio del periodo.
     * @param end Fecha de fin del periodo.
     * @return Número total de libros leídos.
     */
    long countByStatusAndEndDateBetween(ReadingStatus status, LocalDate start, LocalDate end);

    /**
     * Busca libros cuyo título contenga la cadena proporcionada, ignorando mayúsculas.
     * Utilizado para el buscador interno de la biblioteca en el Frontend.
     * @param title Fragmento del título a buscar.
     * @return Lista de libros coincidentes.
     */
    List<Book> findByTitleContainingIgnoreCase(String title);

    /**
     * Encuentra el siguiente volumen disponible de una saga que el usuario está leyendo.
     * @param sagaId Identificador de la saga.
     * @param currentIndex Índice del volumen actual recién terminado.
     * @return El siguiente libro de la saga, si existe en la biblioteca.
     */
    Optional<Book> findFirstBySagaIdAndIndexInSagaGreaterThanOrderByIndexInSagaAsc(Long sagaId, Integer currentIndex);
}
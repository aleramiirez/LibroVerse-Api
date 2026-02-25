package com.biblioteca.backend.repository;

import com.biblioteca.backend.model.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio JPA para la entidad Genre.
 * <p>
 * Permite gestionar las etiquetas de géneros literarios que se asignan a los libros.
 * </p>
 */
@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {

    /**
     * Busca un género por su nombre único.
     * @param name Nombre del género (ej: "Fantasía").
     * @return Un Optional con el género.
     */
    Optional<Genre> findByName(String name);
}

package com.biblioteca.backend.repository;

import com.biblioteca.backend.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repositorio encargado de gestionar las operaciones de persistencia para la entidad Author.
 * <p>
 * Proporciona métodos para interactuar con la tabla de autores en la base de datos,
 * permitiendo la búsqueda y almacenamiento de escritores de forma global.
 * </p>
 */
public interface AuthorRepository extends JpaRepository<Author, Long> {

    /**
     * Comprueba si ya existe un autor registrado con un nombre determinado.
     * @param name Nombre del autor a verificar.
     * @return true si el autor ya existe, false en caso contrario.
     */
    boolean existsByName(String name);

    /**
     * Busca un autor en la base de datos utilizando su nombre exacto.
     * <p>
     * Se utiliza habitualmente para evitar duplicados al registrar nuevos libros,
     * comprobando si el autor ya existe antes de crear uno nuevo.
     * </p>
     * @param name Nombre completo del autor a buscar.
     * @return Un Optional que contiene el Author si se encuentra, o vacío si no existe.
     */
    Optional<Author> findByName(String name);
}

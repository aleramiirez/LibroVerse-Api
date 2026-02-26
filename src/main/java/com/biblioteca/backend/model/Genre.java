package com.biblioteca.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Entidad que representa un Género literario (ej. Fantasía, Ciencia Ficción).
 * <p>
 * Implementa una relación muchos-a-muchos con los libros, permitiendo que un libro
 * esté categorizado en múltiples géneros simultáneamente.
 * </p>
 */
@Entity
@Table(name = "genres")
@Getter
@Setter
@ToString(exclude = "books")
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Genre {

    /**
     * Identificador único del género.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre único del género. Se almacena en mayúsculas o formato estándar
     * para facilitar la búsqueda.
     */
    @Column(nullable = false, unique = true)
    private String name;

    /**
     * Conjunto de libros que pertenecen a este género.
     * Se utiliza un Set para evitar duplicidad de libros en una misma categoría.
     */
    @ManyToMany(mappedBy = "genres")
    @Builder.Default
    private Set<Book> books = new HashSet<>();
}

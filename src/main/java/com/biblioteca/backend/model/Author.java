package com.biblioteca.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa a un Autor en el sistema.
 * <p>
 * Un autor puede tener asociados múltiples libros. Se utiliza una relación de
 * uno-a-muchos con la entidad Book. La gestión de nombres es global para
 * permitir la reutilización de autores entre diferentes usuarios.
 * </p>
 */
@Entity
@Table(name = "authors")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Author {

    /**
     * Identificador único del autor (Clave primaria).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre completo del autor. Se utiliza para búsquedas y filtrado en la biblioteca.
     */
    @Column(nullable = false)
    private String name;

    /**
     * Lista de libros asociados a este autor.
     * <p>
     * Utiliza 'mappedBy' para indicar que la relación está gobernada por el campo 'author'
     * en la clase Book. Se usa JsonIgnoreProperties para evitar ciclos de recursión
     * al serializar a JSON.
     * </p>
     */
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Book> books = new ArrayList<>();
}

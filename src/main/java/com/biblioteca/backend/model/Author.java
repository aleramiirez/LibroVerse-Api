package com.biblioteca.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa al autor de uno o varios libros.
 */
@Entity
@Table(name = "authors")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    /**
     * Relación bidireccional con los libros.
     * CascadeType.ALL permite que al eliminar un autor, se eliminen sus libros asociados.
     */
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Book> books = new ArrayList<>();
}

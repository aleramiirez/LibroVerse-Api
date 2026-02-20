package com.biblioteca.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Entidad que representa la categoría literaria de un libro.
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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @ManyToMany(mappedBy = "genres")
    @Builder.Default
    private Set<Book> books = new HashSet<>();
}
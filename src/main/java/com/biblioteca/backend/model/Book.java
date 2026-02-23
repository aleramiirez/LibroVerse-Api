package com.biblioteca.backend.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Entidad principal que representa un libro en la biblioteca digital del
 * usuario.
 * <p>
 * Optimizada para un bajo consumo de RAM utilizando FetchType.LAZY en todas las
 * relaciones ManyToOne, evitando cargar grafos de objetos innecesarios.
 * </p>
 */
@Entity
@Table(name = "books")
@Getter
@Setter
@ToString(exclude = { "author", "saga", "genres" })
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    /** Fecha en la que el usuario cambia el estado a READING */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    /** Fecha en la que el usuario cambia el estado a FINISHED */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReadingStatus status;

    /** Puntuación otorgada por el usuario (Rango 1-5) */
    private Integer rating;

    /**
     * URL o ruta del sistema donde se almacena el archivo .epub para descargarlo
     */
    private String epubUrl;

    /**
     * URL remota de la portada obtenida de Google Books para ahorrar almacenamiento
     * local
     */
    private String coverUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    @JsonIgnoreProperties({ "books", "hibernateLazyInitializer", "handler" })
    private Author author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "saga_id")
    @JsonIgnoreProperties({ "books", "hibernateLazyInitializer", "handler" })
    private Saga saga;

    /** Número de volumen dentro de la saga (ej. 1 para el primer libro) */
    @Column(name = "index_in_saga")
    private Integer indexInSaga;

    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(name = "book_genre", joinColumns = @JoinColumn(name = "book_id"), inverseJoinColumns = @JoinColumn(name = "genre_id"))
    @Builder.Default
    @JsonIgnoreProperties("books")
    private Set<Genre> genres = new HashSet<>();

}
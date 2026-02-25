package com.biblioteca.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa una Saga o serie de libros.
 * <p>
 * Permite a los usuarios organizar sus colecciones en grupos lógicos (ej. "Harry Potter").
 * A diferencia de los autores, las sagas son privadas por usuario para evitar que
 * nombres de sagas genéricas se mezclen entre diferentes bibliotecas.
 * </p>
 */
@Entity
@Table(name = "sagas")
@Getter
@Setter
@ToString(exclude = "books")
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Saga {

    /**
     * Identificador único de la saga.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre de la saga (ej. "Nacidos de la Bruma").
     */
    @Column(nullable = false)
    private String name;

    /**
     * URL de la imagen de portada que representa a la saga completa.
     * Generalmente se hereda de la portada del primer libro añadido.
     */
    @Column(name = "cover_url")
    private String coverUrl;

    /**
     * Relación con el usuario propietario de la saga.
     */
    @OneToMany(mappedBy = "saga")
    @OrderBy("indexInSaga ASC")
    @Builder.Default
    @JsonIgnoreProperties("saga")
    private List<Book> books = new ArrayList<>();

    /**
     * Lista de libros que componen esta saga.
     * Los libros se ordenan internamente mediante el campo 'indexInSaga' de la entidad Book.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private User user;
}

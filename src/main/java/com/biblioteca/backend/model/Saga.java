package com.biblioteca.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad para agrupar libros que pertenecen a una misma serie o universo.
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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "cover_url")
    private String coverUrl;

    /**
     * Lista de libros de la saga, ordenados automáticamente por su volumen
     * (indexInSaga)
     * para facilitar la visualización en la interfaz móvil.
     */
    @OneToMany(mappedBy = "saga")
    @OrderBy("indexInSaga ASC")
    @Builder.Default
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties("saga")
    private List<Book> books = new ArrayList<>();

}
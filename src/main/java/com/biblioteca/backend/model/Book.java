package com.biblioteca.backend.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Entidad principal (Modelo de Base de Datos) que representa un libro en la biblioteca digital del usuario.
 * <p>
 * Se mapea directamente a la tabla "books" en PostgreSQL.
 * Está optimizada para un bajo consumo de memoria RAM utilizando FetchType.LAZY en todas las
 * relaciones ManyToOne, lo que evita cargar datos relacionados (como el Autor o la Saga)
 * hasta que no se pidan explícitamente en el código.
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

    /**
     * Identificador único y clave primaria autoincremental en la base de datos.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Título completo del libro. Es un campo obligatorio en la base de datos.
     */
    @Column(nullable = false)
    private String title;

    /**
     * Fecha en la que el usuario marca el libro con el estado READING (Leyendo).
     * Se formatea automáticamente a 'yyyy-MM-dd' para el Frontend.
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    /**
     * Fecha en la que el usuario marca el libro con el estado FINISHED (Terminado).
     * Se formatea automáticamente a 'yyyy-MM-dd' para el Frontend.
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    /**
     * Estado de lectura actual del libro (PENDING, READING o FINISHED).
     * Se guarda como texto (STRING) en la base de datos en lugar de número para mayor legibilidad.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReadingStatus status;

    /**
     * Puntuación otorgada por el usuario (Generalmente un rango de 1 a 5 estrellas).
     */
    private Integer rating;

    /**
     * URL web o ruta local del sistema donde se almacena el archivo electrónico (.epub o .pdf) para su lectura.
     */
    private String epubUrl;

    /**
     * URL remota de la imagen de portada obtenida (habitualmente desde la API de Google Books).
     * Almacenar la URL externa ahorra una gran cantidad de espacio en el disco duro del servidor.
     */
    private String coverUrl;

    /**
     * Relación Muchos-a-Uno con la entidad Author.
     * Muchos libros pueden pertenecer a un mismo autor. La carga es perezosa (LAZY).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    @JsonIgnoreProperties({ "books", "hibernateLazyInitializer", "handler" })
    private Author author;

    /**
     * Relación Muchos-a-Uno con la entidad Saga.
     * Muchos libros pueden agruparse en una misma colección o saga literaria.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "saga_id")
    @JsonIgnoreProperties({ "books", "hibernateLazyInitializer", "handler" })
    private Saga saga;

    /**
     * Número que representa el orden lógico o volumen de este libro dentro de su Saga
     * (ej. 1 para el primer libro, 2 para la secuela, etc.).
     */
    @Column(name = "index_in_saga")
    private Integer indexInSaga;

    /**
     * Relación Muchos-a-Muchos con la entidad Genre (Géneros).
     * Un libro puede tener varios géneros y un género agrupa muchos libros.
     * Crea una tabla intermedia automática llamada 'book_genre' en la base de datos.
     */
    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(name = "book_genre", joinColumns = @JoinColumn(name = "book_id"), inverseJoinColumns = @JoinColumn(name = "genre_id"))
    @Builder.Default
    @JsonIgnoreProperties("books")
    private Set<Genre> genres = new HashSet<>();

    /**
     * Relación Muchos-a-Uno con la entidad User.
     * Indica a qué usuario (dueño) pertenece este libro dentro de la plataforma.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private User user;
}

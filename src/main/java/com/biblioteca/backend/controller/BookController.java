package com.biblioteca.backend.controller;

import com.biblioteca.backend.dto.ErrorResponse;
import com.biblioteca.backend.dto.GoogleBooksResponse;
import com.biblioteca.backend.model.Book;
import com.biblioteca.backend.service.BookServiceI;
import com.biblioteca.backend.service.ExternalBookSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST que expone los endpoints para la gestión de la biblioteca personal.
 * <p>
 * Permite realizar operaciones CRUD sobre los libros del usuario y conectar con
 * servicios externos para la búsqueda de nuevos ejemplares.
 * </p>
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/books")
@Tag(name = "Libros", description = "Gestión de la biblioteca personal y búsquedas en Google Books")
public class BookController {

    private final BookServiceI bookService;
    private final ExternalBookSearchService googleBooksService;

    /**
     * Endpoint para buscar metadatos e información de libros directamente en Google Books.
     * <p>
     * Ejemplo de uso: GET /api/books/search?title=mistborn
     * </p>
     * @param title El título (o parte de él) del libro que el usuario desea buscar.
     * @return ResponseEntity con una lista de hasta 5 coincidencias en un formato JSON ligero.
     */
    @Operation(summary = "Buscar en Google Books", description = "Consulta metadatos de libros externos por título")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Búsqueda realizada con éxito"),
            @ApiResponse(responseCode = "401", description = "No autorizado - Token JWT inválido o ausente",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/search")
    public ResponseEntity<List<GoogleBooksResponse.Item>> searchInGoogle(@RequestParam final String title) {
        List<GoogleBooksResponse.Item> results = googleBooksService.searchByTitle(title);
        return ResponseEntity.ok(results);
    }

    /**
     * Endpoint para recuperar los libros guardados en la biblioteca personal de forma paginada.
     * <p>
     * Ejemplo de uso: GET /api/books?page=0&size=10
     * </p>
     * @param page Número de página a consultar (por defecto 0).
     * @param size Cantidad de libros por página (por defecto 10).
     * @return ResponseEntity conteniendo un objeto Page JSON con los libros y metadatos de navegación.
     */
    @Operation(summary = "Obtener biblioteca paginada",
            description = "Lista los libros del usuario actual divididos en páginas. " +
                    "Ideal para optimizar el rendimiento de la aplicación cliente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Página de libros recuperada con éxito"),
            @ApiResponse(responseCode = "401", description = "No autorizado - Token ausente o inválido",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<Page<Book>> getMyLibrary(
            @Parameter(description = "Número de página a consultar (comienza en 0)")
            @RequestParam(defaultValue = "0") final int page,
            @Parameter(description = "Cantidad de libros por página")
            @RequestParam(defaultValue = "10") final int size) {
        return ResponseEntity.ok(bookService.getAllBooks(page, size));
    }

    /**
     * Endpoint para obtener los detalles completos de un libro específico mediante su identificador.
     * <p>
     * Ejemplo de uso: GET /api/books/1
     * </p>
     * @param id El identificador único (Primary Key) del libro en la base de datos local.
     * @return ResponseEntity con el objeto Book solicitado (200 OK), o lanzará una excepción (404 Not Found) si no existe.
     */
    @Operation(summary = "Obtener libro por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Libro encontrado"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "404", description = "El libro con el ID proporcionado no existe",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<Book> getBook(@PathVariable final Long id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    /**
     * Endpoint para crear y añadir un nuevo libro a la base de datos de PostgreSQL.
     * <p>
     * Ejemplo de uso: POST /api/books (requiere enviar el objeto Book en formato JSON en el cuerpo de la petición).
     * </p>
     * @param book El objeto Book mapeado automáticamente por Spring Boot desde el JSON enviado por el frontend.
     * @return ResponseEntity con el libro guardado (incluyendo el nuevo ID generado) y código de estado 200 OK.
     */
    @Operation(summary = "Añadir libro", description = "Guarda un nuevo libro en la base de datos local")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Libro añadido con éxito"),
            @ApiResponse(responseCode = "400", description = "Datos del libro inválidos o incompletos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @PostMapping
    public ResponseEntity<Book> addBookToLibrary(@Valid @RequestBody final Book book) {
        Book savedBook = bookService.saveBook(book);
        return ResponseEntity.ok(savedBook);
    }

    /**
     * Endpoint para actualizar parcial o totalmente la información de un libro existente
     * (cambios de estado, notas, fechas de lectura, etc.).
     * <p>
     * Ejemplo de uso: PUT /api/books/1
     * </p>
     * @param id El identificador único del libro que se va a modificar.
     * @param bookDetails JSON convertido a objeto Book con los datos nuevos a aplicar.
     * @return ResponseEntity con el libro tras haber sido actualizado y guardado en la base de datos.
     */
    @Operation(summary = "Actualizar libro", description = "Modifica estado, rating o datos de un libro existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Libro actualizado con éxito"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "404", description = "No se pudo encontrar el libro para actualizar",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable final Long id,@Valid @RequestBody final Book bookDetails) {
        return ResponseEntity.ok(bookService.updateBook(id, bookDetails));
    }

    /**
     * Endpoint para eliminar definitivamente un libro de la biblioteca personal del usuario.
     * <p>
     * Ejemplo de uso: DELETE /api/books/1
     * </p>
     * @param id El identificador único del libro a eliminar.
     * @return ResponseEntity vacía con código 204 (No Content), que es el estándar en arquitecturas REST
     * para indicar un borrado exitoso.
     */
    @Operation(summary = "Eliminar libro", description = "Borra físicamente un libro de la biblioteca")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Libro eliminado con éxito"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "404", description = "El libro que se intenta borrar no existe",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable final Long id) {
        bookService.deleteBook(id);
        // Devuelve un 204 No Content, que es el estándar REST para borrados exitosos
        return ResponseEntity.noContent().build();
    }
}

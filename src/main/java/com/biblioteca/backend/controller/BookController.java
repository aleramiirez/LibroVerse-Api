package com.biblioteca.backend.controller;

import com.biblioteca.backend.dto.GoogleBooksResponse;
import com.biblioteca.backend.model.Book;
import com.biblioteca.backend.service.BookServiceI;
import com.biblioteca.backend.service.ExternalBookSearchService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST que expone los endpoints de la API de la Biblioteca Digital.
 * <p>
 * Estos endpoints serán consumidos por el frontend alojado en Vercel.
 * Se utiliza @RestController para devolver automáticamente las respuestas en formato JSON.
 * </p>
 */
@RestController
@RequestMapping("/api/books")
@Tag(name = "Libros", description = "Operaciones para gestionar la biblioteca y búsquedas en Google")
public class BookController {

    private final BookServiceI bookService;
    private final ExternalBookSearchService googleBooksService;

    public BookController(BookServiceI bookService, ExternalBookSearchService googleBooksService) {
        this.bookService = bookService;
        this.googleBooksService = googleBooksService;
    }

    /**
     * Endpoint para buscar metadatos de libros directamente en Google Books.
     * <p>
     * Ejemplo de uso: GET /api/books/search?title=mistborn
     * </p>
     * @param title El título del libro a buscar que el usuario teclea en su móvil.
     * @return Lista de hasta 5 coincidencias en formato JSON ligero.
     */
    @GetMapping("/search")
    public ResponseEntity<List<GoogleBooksResponse.Item>> searchInGoogle(@RequestParam String title) {
        List<GoogleBooksResponse.Item> results = googleBooksService.searchByTitle(title);
        return ResponseEntity.ok(results);
    }

    /**
     * Endpoint para recuperar todos los libros guardados en la biblioteca personal.
     * <p>
     * Ejemplo de uso: GET /api/books
     * </p>
     * @return Lista JSON con todos los libros del usuario.
     */
    @GetMapping
    public ResponseEntity<List<Book>> getMyLibrary() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    /**
     * Endpoint para obtener los detalles completos de un libro específico mediante su ID.
     * <p>
     * Ejemplo de uso: GET /api/books/1
     * </p>
     * @param id El identificador único del libro en la base de datos.
     * @return El libro solicitado con código de estado 200 OK, o un error 404 si no existe.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Book> getBook(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    /**
     * Endpoint para añadir un nuevo libro a la base de datos de PostgreSQL.
     * <p>
     * Ejemplo de uso: POST /api/books con el JSON del libro en el body.
     * </p>
     * @param book El libro mapeado desde el JSON enviado por el frontend.
     * @return El libro guardado con su ID y código de estado 200 OK.
     */
    @PostMapping
    public ResponseEntity<Book> addBookToLibrary(@RequestBody Book book) {
        Book savedBook = bookService.saveBook(book);
        return ResponseEntity.ok(savedBook);
    }

    /**
     * Endpoint para actualizar el estado, nota o fechas de un libro.
     * <p>
     * Ejemplo de uso: PUT /api/books/1
     * </p>
     * @param id Identificador del libro.
     * @param bookDetails JSON con los datos a actualizar.
     * @return El libro actualizado con código 200 OK.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable Long id, @RequestBody Book bookDetails) {
        return ResponseEntity.ok(bookService.updateBook(id, bookDetails));
    }

    /**
     * Endpoint para eliminar un libro de la biblioteca.
     * <p>
     * Ejemplo de uso: DELETE /api/books/1
     * </p>
     * @param id Identificador del libro.
     * @return Respuesta vacía con código 204 (No Content) indicando éxito.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        // Devuelve un 204 No Content, que es el estándar REST para borrados exitosos
        return ResponseEntity.noContent().build();
    }
}
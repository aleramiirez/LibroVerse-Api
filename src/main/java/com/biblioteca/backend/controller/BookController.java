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
 * Controlador REST que expone los endpoints principales de la API para la gestión de libros.
 * <p>
 * Actúa como intermediario entre las peticiones HTTP realizadas por el Frontend (Vercel)
 * y la lógica de negocio subyacente (Capa Service). Al usar @RestController, todos los
 * métodos serializan automáticamente sus retornos a formato JSON.
 * </p>
 */
@RestController
@RequestMapping("/api/books")
@Tag(name = "Libros", description = "Operaciones para gestionar la biblioteca y búsquedas en Google")
public class BookController {

    private final BookServiceI bookService;
    private final ExternalBookSearchService googleBooksService;

    /**
     * Constructor para la inyección de dependencias de los servicios necesarios.
     * @param bookService Servicio con la lógica CRUD para los libros locales.
     * @param googleBooksService Servicio para interactuar con la API externa de Google Books.
     */
    public BookController(BookServiceI bookService, ExternalBookSearchService googleBooksService) {
        this.bookService = bookService;
        this.googleBooksService = googleBooksService;
    }

    /**
     * Endpoint para buscar metadatos e información de libros directamente en Google Books.
     * <p>
     * Ejemplo de uso: GET /api/books/search?title=mistborn
     * </p>
     * @param title El título (o parte de él) del libro que el usuario desea buscar.
     * @return ResponseEntity con una lista de hasta 5 coincidencias en un formato JSON ligero.
     */
    @GetMapping("/search")
    public ResponseEntity<List<GoogleBooksResponse.Item>> searchInGoogle(@RequestParam String title) {
        List<GoogleBooksResponse.Item> results = googleBooksService.searchByTitle(title);
        return ResponseEntity.ok(results);
    }

    /**
     * Endpoint para recuperar todos los libros guardados en la biblioteca personal del usuario autenticado.
     * <p>
     * Ejemplo de uso: GET /api/books
     * </p>
     * @return ResponseEntity conteniendo una lista JSON con todos los libros asociados al usuario.
     */
    @GetMapping
    public ResponseEntity<List<Book>> getMyLibrary() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    /**
     * Endpoint para obtener los detalles completos de un libro específico mediante su identificador.
     * <p>
     * Ejemplo de uso: GET /api/books/1
     * </p>
     * @param id El identificador único (Primary Key) del libro en la base de datos local.
     * @return ResponseEntity con el objeto Book solicitado (200 OK), o lanzará una excepción (404 Not Found) si no existe.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Book> getBook(@PathVariable Long id) {
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
    @PostMapping
    public ResponseEntity<Book> addBookToLibrary(@RequestBody Book book) {
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
    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable Long id, @RequestBody Book bookDetails) {
        return ResponseEntity.ok(bookService.updateBook(id, bookDetails));
    }

    /**
     * Endpoint para eliminar definitivamente un libro de la biblioteca personal del usuario.
     * <p>
     * Ejemplo de uso: DELETE /api/books/1
     * </p>
     * @param id El identificador único del libro a eliminar.
     * @return ResponseEntity vacía con código 204 (No Content), que es el estándar en arquitecturas REST para indicar un borrado exitoso.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        // Devuelve un 204 No Content, que es el estándar REST para borrados exitosos
        return ResponseEntity.noContent().build();
    }
}
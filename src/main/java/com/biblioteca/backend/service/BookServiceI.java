package com.biblioteca.backend.service;

import com.biblioteca.backend.model.Book;
import java.util.List;

/**
 * Interfaz para la gestión de la lógica de negocio de los libros.
 * <p>
 * Define las operaciones CRUD (Crear, Leer, Actualizar, Borrar) y las reglas
 * de gestión de la biblioteca personal de cada usuario.
 * </p>
 */
public interface BookServiceI {

    /**
     * Registra un nuevo libro o guarda los cambios de uno existente.
     * @param book Entidad libro con la información a persistir.
     * @return El libro guardado con su ID y relaciones actualizadas.
     */
    Book saveBook(Book book);

    /**
     * Recupera la información detallada de un libro por su identificador.
     * @param id Identificador único del libro.
     * @return La entidad Book encontrada.
     */
    Book getBookById(Long id);

    /**
     * Obtiene la colección completa de libros del usuario autenticado actualmente.
     * @return Lista de libros pertenecientes al usuario.
     */
    List<Book> getAllBooks();

    /**
     * Actualiza las propiedades de un libro ya existente en la base de datos.
     * @param id Identificador del libro a modificar.
     * @param bookDetails Objeto con los nuevos datos a aplicar.
     * @return El libro tras aplicar y persistir los cambios.
     */
    Book updateBook(Long id, Book bookDetails);

    /**
     * Elimina un libro de la base de datos de forma permanente.
     * @param id Identificador del libro a borrar.
     */
    void deleteBook(Long id);
}

package com.biblioteca.backend.service;

import com.biblioteca.backend.model.Book;
import java.util.List;

/**
 * Puerto de entrada (Interfaz) para los casos de uso de la Biblioteca.
 * Define el contrato que la capa de presentación puede consumir.
 */
public interface BookServiceI {

    /**
     * Guarda un nuevo libro en la base de datos o actualiza uno existente.
     * * @param book Entidad del libro con los datos a guardar.
     * @return El libro persistido con su ID generado.
     */
    Book saveBook(Book book);

    /**
     * Busca y recupera un libro específico utilizando su identificador único.
     * * @param id El identificador único del libro en la base de datos.
     * @return La entidad del libro encontrada.
     */
    Book getBookById(Long id);

    /**
     * Obtiene todos los libros almacenados en la biblioteca del usuario.
     * * @return Lista completa de libros.
     */
    List<Book> getAllBooks();

    /**
     * Actualiza los metadatos y el estado de lectura de un libro ya existente.
     * * @param id El identificador único del libro a actualizar.
     * @param bookDetails Objeto con los nuevos datos (estado, nota, fechas, etc.).
     * @return El libro actualizado y persistido en la base de datos.
     */
    Book updateBook(Long id, Book bookDetails);

    /**
     * Elimina permanentemente un libro de la biblioteca personal.
     * * @param id El identificador único del libro a eliminar.
     */
    void deleteBook(Long id);
}
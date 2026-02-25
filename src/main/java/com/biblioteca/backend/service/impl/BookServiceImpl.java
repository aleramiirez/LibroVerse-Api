package com.biblioteca.backend.service.impl;

import com.biblioteca.backend.exception.ResourceNotFoundException;
import com.biblioteca.backend.model.Author;
import com.biblioteca.backend.model.Book;
import com.biblioteca.backend.model.ReadingStatus;
import com.biblioteca.backend.model.Saga;
import com.biblioteca.backend.repository.AuthorRepository;
import com.biblioteca.backend.repository.BookRepository;
import com.biblioteca.backend.model.Genre;
import com.biblioteca.backend.repository.GenreRepository;
import com.biblioteca.backend.repository.SagaRepository;
import com.biblioteca.backend.service.BookServiceI;
import com.biblioteca.backend.model.User;
import com.biblioteca.backend.security.SecurityUtils;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementación del servicio interno para gestionar las operaciones CRUD de la biblioteca.
 * <p>
 * Esta clase centraliza la lógica de negocio para interactuar con PostgreSQL de manera
 * eficiente y transaccional, aislando al controlador de los detalles de persistencia.
 * Gestiona automáticamente la relación entre libros, autores, sagas y géneros.
 * </p>
 */
@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookServiceI {

    /** Repositorio para la persistencia de libros. */
    private final BookRepository bookRepository;

    /** Repositorio para la gestión de autores. */
    private final AuthorRepository authorRepository;

    /** Repositorio para la gestión de sagas o series. */
    private final SagaRepository sagaRepository;

    /** Repositorio para la gestión de géneros literarios. */
    private final GenreRepository genreRepository;

    /**
     * Guarda un nuevo libro o actualiza uno existente en la base de datos.
     * <p>
     * El proceso incluye la vinculación automática al usuario autenticado, la asignación
     * de un estado por defecto (PENDING) y la gestión de la existencia previa de autores
     * y sagas para evitar duplicados.
     * </p>
     * @param book El objeto libro con sus metadatos enviado desde el cliente.
     * @return El libro persistido con su ID generado y relaciones actualizadas.
     */
    @Override
    @Transactional
    public Book saveBook(final Book book) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        book.setUser(User.builder().id(currentUserId).build());

        // Asigna estado pendiente si el libro es nuevo y no tiene uno definido
        if (book.getId() == null && book.getStatus() == null) {
            book.setStatus(ReadingStatus.PENDING);
        }

        // Gestión de Autor: Busca por nombre exacto o crea uno nuevo si no existe
        if (book.getAuthor() != null && book.getAuthor().getName() != null) {
            String authorName = book.getAuthor().getName();
            Optional<Author> existingAuthor = authorRepository.findByName(authorName);
            if (existingAuthor.isPresent()) {
                book.setAuthor(existingAuthor.get());
            } else {
                Author newAuthor = authorRepository.save(book.getAuthor());
                book.setAuthor(newAuthor);
            }
        }

        // Gestión de Saga: Busca la saga del usuario por nombre o la crea
        if (book.getSaga() != null && book.getSaga().getName() != null) {
            String sagaName = book.getSaga().getName();
            Optional<Saga> existingSaga = sagaRepository.findByUserIdAndName(currentUserId, sagaName);

            Saga targetSaga;
            if (existingSaga.isPresent()) {
                targetSaga = existingSaga.get();
                // Asignar portada a la saga si no tiene y el libro sí
                if (targetSaga.getCoverUrl() == null && book.getCoverUrl() != null) {
                    targetSaga.setCoverUrl(book.getCoverUrl());
                    sagaRepository.save(targetSaga);
                }
            } else {
                targetSaga = Saga.builder()
                        .name(sagaName)
                        .user(User.builder().id(currentUserId).build())
                        .build();
                if (book.getCoverUrl() != null) {
                    targetSaga.setCoverUrl(book.getCoverUrl());
                }
                targetSaga = sagaRepository.save(targetSaga);
            }
            book.setSaga(targetSaga);
        }

        return bookRepository.save(book);
    }

    /**
     * Busca un libro en la base de datos por su identificador único.
     * @param id El identificador único del libro a buscar.
     * @return El objeto {@link Book} encontrado.
     * @throws ResourceNotFoundException Si no se encuentra ningún libro con el ID proporcionado.
     */
    @Override
    public Book getBookById(final Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("El libro con ID " + id + " no existe"));
    }

    /**
     * Recupera todos los libros almacenados en la biblioteca del usuario autenticado.
     * @return Lista completa de libros pertenecientes al usuario actual.
     */
    @Override
    public List<Book> getAllBooks() {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        return bookRepository.findByUserId(currentUserId);
    }

    /**
     * Actualiza los datos de seguimiento y metadatos de un libro existente.
     * <p>
     * Permite modificar campos individuales como el título, estado de lectura, calificación,
     * fechas, géneros y vinculación a sagas.
     * </p>
     * * @param id El identificador del libro a actualizar.
     * @param bookDetails Objeto con los nuevos datos enviados desde el frontend.
     * @return El libro actualizado y persistido.
     */
    @Override
    @Transactional
    public Book updateBook(final Long id, final Book bookDetails) {
        Book existingBook = getBookById(id);

        // Actualización de campos básicos si están presentes en la petición
        if (bookDetails.getTitle() != null) existingBook.setTitle(bookDetails.getTitle());
        if (bookDetails.getStatus() != null) existingBook.setStatus(bookDetails.getStatus());
        if (bookDetails.getRating() != null) existingBook.setRating(bookDetails.getRating());
        if (bookDetails.getStartDate() != null) existingBook.setStartDate(bookDetails.getStartDate());
        if (bookDetails.getEndDate() != null) existingBook.setEndDate(bookDetails.getEndDate());
        if (bookDetails.getIndexInSaga() != null) existingBook.setIndexInSaga(bookDetails.getIndexInSaga());

        // Gestión del cambio de Autor
        if (bookDetails.getAuthor() != null && bookDetails.getAuthor().getName() != null) {
            String newAuthorName = bookDetails.getAuthor().getName();
            if (existingBook.getAuthor() == null || !existingBook.getAuthor().getName().equals(newAuthorName)) {
                Optional<Author> existingAuthor = authorRepository.findByName(newAuthorName);
                if (existingAuthor.isPresent()) {
                    existingBook.setAuthor(existingAuthor.get());
                } else {
                    Author newAuthor = authorRepository
                            .save(new Author(null, newAuthorName, new java.util.ArrayList<>()));
                    existingBook.setAuthor(newAuthor);
                }
            }
        }

        // Lógica para determinar la URL de portada efectiva
        String rawCover = bookDetails.getCoverUrl() != null ? bookDetails.getCoverUrl() : existingBook.getCoverUrl();
        String effectiveCover = (rawCover != null && !rawCover.trim().isEmpty()) ? rawCover : null;

        // Gestión del cambio o actualización de Saga
        if (bookDetails.getSaga() != null && bookDetails.getSaga().getName() != null
                && !bookDetails.getSaga().getName().isEmpty()) {
            String sagaName = bookDetails.getSaga().getName();
            Long currentUserId = SecurityUtils.getCurrentUserId();
            Optional<Saga> existingSaga = sagaRepository.findByUserIdAndName(currentUserId, sagaName);

            Saga targetSaga;
            if (existingSaga.isPresent()) {
                targetSaga = existingSaga.get();
                boolean sagaHasNoCover = targetSaga.getCoverUrl() == null || targetSaga.getCoverUrl().trim().isEmpty();

                if (sagaHasNoCover && effectiveCover != null) {
                    System.out.println(
                            ">>> Auto-assigning cover to Saga '" + targetSaga.getName() + "': " + effectiveCover);
                    targetSaga.setCoverUrl(effectiveCover);
                    sagaRepository.save(targetSaga);
                }
            } else {
                targetSaga = Saga.builder()
                        .name(sagaName)
                        .user(User.builder().id(currentUserId).build())
                        .build();
                if (effectiveCover != null) {
                    targetSaga.setCoverUrl(effectiveCover);
                }
                targetSaga = sagaRepository.save(targetSaga);
            }
            existingBook.setSaga(targetSaga);

        } else if (bookDetails.getSaga() == null) {
            // Desvinculación de saga si se envía null explícitamente
            existingBook.setSaga(null);
            existingBook.setIndexInSaga(null);
        }

        // Actualización de la colección de géneros
        if (bookDetails.getGenres() != null) {
            existingBook.getGenres().clear();

            for (Genre genreDTO : bookDetails.getGenres()) {
                if (genreDTO.getName() != null && !genreDTO.getName().isEmpty()) {
                    Optional<Genre> existingGenre = genreRepository.findByName(genreDTO.getName());
                    if (existingGenre.isPresent()) {
                        existingBook.getGenres().add(existingGenre.get());
                    } else {
                        Genre newGenre = genreRepository.save(Genre.builder().name(genreDTO.getName()).build());
                        existingBook.getGenres().add(newGenre);
                    }
                }
            }
        }

        // Actualización de URLs de archivos multimedia
        if (bookDetails.getEpubUrl() != null) existingBook.setEpubUrl(bookDetails.getEpubUrl());
        if (bookDetails.getCoverUrl() != null) existingBook.setCoverUrl(bookDetails.getCoverUrl());

        return bookRepository.save(existingBook);
    }

    /**
     * Elimina un libro de la biblioteca personal del usuario.
     * @param id El identificador único del libro a eliminar.
     */
    @Override
    public void deleteBook(final Long id) {
        Book existingBook = getBookById(id);
        bookRepository.delete(existingBook);
    }
}

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servicio interno para gestionar las operaciones CRUD de la biblioteca.
 * <p>
 * Centraliza la lógica de negocio para interactuar con PostgreSQL de manera
 * eficiente y transaccional, aislando al controlador de los detalles de la base
 * de datos.
 * </p>
 */
@Service
public class BookServiceImpl implements BookServiceI {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final SagaRepository sagaRepository;
    private final GenreRepository genreRepository;

    /**
     * Inyección de dependencias por constructor.
     */
    public BookServiceImpl(BookRepository bookRepository, AuthorRepository authorRepository,
            SagaRepository sagaRepository, GenreRepository genreRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.sagaRepository = sagaRepository;
        this.genreRepository = genreRepository;
    }

    /**
     * Guarda un nuevo libro o actualiza uno existente en la base de datos.
     * Gestiona la creación o reutilización de Autores y Sagas.
     * 
     * @param book El objeto libro con sus metadatos.
     * @return El libro guardado con su ID generado.
     */
    @Override
    @Transactional
    public Book saveBook(final Book book) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        book.setUser(User.builder().id(currentUserId).build());

        if (book.getId() == null && book.getStatus() == null) {
            book.setStatus(ReadingStatus.PENDING);
        }

        // Gestionar Autor (Buscar existente o crear nuevo)
        if (book.getAuthor() != null && book.getAuthor().getName() != null) {
            String authorName = book.getAuthor().getName();
            Optional<Author> existingAuthor = authorRepository.findByName(authorName);
            if (existingAuthor.isPresent()) {
                book.setAuthor(existingAuthor.get());
            } else {
                // Si es nuevo, lo guardamos explícitamente primero si no hay
                // CascadeType.PERSIST
                // Como Author tiene CascadeType.ALL en sus libros pero Book no en Author, mejor
                // guardamos el autor.
                Author newAuthor = authorRepository.save(book.getAuthor());
                book.setAuthor(newAuthor);
            }
        }

        // Gestionar Saga (Buscar existente o crear nueva)
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
     * * @param id El identificador del libro a buscar.
     * 
     * @return El objeto Book encontrado.
     * @throws ResourceNotFoundException Si no se encuentra ningún libro con ese ID.
     */
    @Override
    public Book getBookById(final Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("El libro con ID " + id + " no existe"));
    }

    /**
     * Recupera todos los libros almacenados en la biblioteca.
     * 
     * @return Lista completa de libros.
     */
    @Override
    public List<Book> getAllBooks() {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        return bookRepository.findByUserId(currentUserId);
    }

    /**
     * Actualiza los datos de seguimiento de un libro existente.
     * 
     * @param id          El identificador del libro a actualizar.
     * @param bookDetails Los nuevos datos enviados desde el frontend.
     * @return El libro actualizado.
     */
    @Override
    @Transactional
    public Book updateBook(Long id, Book bookDetails) {
        Book existingBook = getBookById(id);

        if (bookDetails.getTitle() != null) {
            existingBook.setTitle(bookDetails.getTitle());
        }
        if (bookDetails.getStatus() != null) {
            existingBook.setStatus(bookDetails.getStatus());
        }
        if (bookDetails.getRating() != null) {
            existingBook.setRating(bookDetails.getRating());
        }
        if (bookDetails.getStartDate() != null) {
            existingBook.setStartDate(bookDetails.getStartDate());
        }
        if (bookDetails.getEndDate() != null) {
            existingBook.setEndDate(bookDetails.getEndDate());
        }
        if (bookDetails.getIndexInSaga() != null) {
            existingBook.setIndexInSaga(bookDetails.getIndexInSaga());
        }

        if (bookDetails.getAuthor() != null && bookDetails.getAuthor().getName() != null) {
            String newAuthorName = bookDetails.getAuthor().getName();
            // Solo cambiar si es diferente
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

        // Determinar la portada efectiva
        String rawCover = bookDetails.getCoverUrl() != null ? bookDetails.getCoverUrl() : existingBook.getCoverUrl();
        String effectiveCover = (rawCover != null && !rawCover.trim().isEmpty()) ? rawCover : null;

        // Actualizar Saga (Buscar existente o crear nueva)
        if (bookDetails.getSaga() != null && bookDetails.getSaga().getName() != null
                && !bookDetails.getSaga().getName().isEmpty()) {
            String sagaName = bookDetails.getSaga().getName();
            Long currentUserId = SecurityUtils.getCurrentUserId();
            Optional<Saga> existingSaga = sagaRepository.findByUserIdAndName(currentUserId, sagaName);

            Saga targetSaga;
            if (existingSaga.isPresent()) {
                targetSaga = existingSaga.get();
                // Si la saga no tiene portada (null o vacía) y el libro sí, se la asignamos
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
            // Si envían null explícitamente, desvincular saga
            existingBook.setSaga(null);
            existingBook.setIndexInSaga(null);
        }

        // Actualizar Géneros
        if (bookDetails.getGenres() != null) {
            // Limpiar géneros existentes para evitar duplicados/obsoletos
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

        // (Opcional) Si quieres permitir cambiar el epub o la portada manualmente:
        if (bookDetails.getEpubUrl() != null)
            existingBook.setEpubUrl(bookDetails.getEpubUrl());
        if (bookDetails.getCoverUrl() != null)
            existingBook.setCoverUrl(bookDetails.getCoverUrl());

        // 3. Guardamos los cambios
        return bookRepository.save(existingBook);
    }

    /**
     * Elimina un libro de la biblioteca personal.
     * 
     * @param id El identificador del libro a eliminar.
     */
    @Override
    public void deleteBook(Long id) {
        Book existingBook = getBookById(id);
        bookRepository.delete(existingBook);
    }
}
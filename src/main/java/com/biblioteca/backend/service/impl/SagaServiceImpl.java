package com.biblioteca.backend.service.impl;

import com.biblioteca.backend.exception.ResourceNotFoundException;
import com.biblioteca.backend.model.Book;
import com.biblioteca.backend.model.Saga;
import com.biblioteca.backend.repository.BookRepository;
import com.biblioteca.backend.repository.SagaRepository;
import com.biblioteca.backend.service.SagaServiceI;
import com.biblioteca.backend.model.User;
import com.biblioteca.backend.security.SecurityUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SagaServiceImpl implements SagaServiceI {

    private final SagaRepository sagaRepository;
    private final BookRepository bookRepository;

    public SagaServiceImpl(SagaRepository sagaRepository, BookRepository bookRepository) {
        this.sagaRepository = sagaRepository;
        this.bookRepository = bookRepository;
    }

    @Override
    public List<Saga> getAllSagas() {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        return sagaRepository.findByUserId(currentUserId);
    }

    @Override
    public Saga getSagaById(Long id) {
        return sagaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Saga no encontrada con ID: " + id));
    }

    @Override
    public Saga createSaga(Saga saga) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        saga.setUser(User.builder().id(currentUserId).build());
        return sagaRepository.save(saga);
    }

    @Override
    public Saga updateSaga(Long id, Saga sagaDetails) {
        Saga saga = getSagaById(id);
        saga.setName(sagaDetails.getName());
        saga.setCoverUrl(sagaDetails.getCoverUrl());
        return sagaRepository.save(saga);
    }

    @Override
    public void deleteSaga(Long id) {
        Saga saga = getSagaById(id);
        // Desvincular libros para evitar error de FK (o borrado en cascada si no
        // deseado)
        // Aquí optamos por desvincular: los libros quedan sin saga.
        List<Book> books = saga.getBooks();
        if (books != null) {
            for (Book book : books) {
                book.setSaga(null);
                book.setIndexInSaga(null);
                bookRepository.save(book);
            }
        }
        sagaRepository.delete(saga);
    }
}

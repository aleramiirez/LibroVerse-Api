package com.biblioteca.backend.service.impl;

import com.biblioteca.backend.exception.ResourceNotFoundException;
import com.biblioteca.backend.model.Book;
import com.biblioteca.backend.model.Saga;
import com.biblioteca.backend.repository.BookRepository;
import com.biblioteca.backend.repository.SagaRepository;
import com.biblioteca.backend.service.SagaServiceI;
import com.biblioteca.backend.model.User;
import com.biblioteca.backend.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementación del servicio para la gestión de Sagas (series de libros).
 * <p>
 * Esta clase contiene la lógica de negocio para organizar colecciones de libros,
 * permitiendo su creación, consulta, actualización y eliminación de forma segura
 * dentro del contexto del usuario autenticado.
 * </p>
 */
@RequiredArgsConstructor
@Service
public class SagaServiceImpl implements SagaServiceI {

    /** Repositorio para la gestión de persistencia de sagas. */
    private final SagaRepository sagaRepository;

    /** Repositorio para la gestión de libros, necesario para la desvinculación en borrados. */
    private final BookRepository bookRepository;

    /**
     * Recupera todas las sagas pertenecientes al usuario que ha iniciado sesión.
     * @return Lista de entidades {@link Saga} asociadas al usuario actual.
     */
    @Override
    public List<Saga> getAllSagas() {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        return sagaRepository.findByUserId(currentUserId);
    }

    /**
     * Obtiene la información detallada de una saga por su ID.
     * @param id Identificador único de la saga.
     * @return La entidad {@link Saga} encontrada.
     * @throws ResourceNotFoundException Si no existe ninguna saga con el ID proporcionado.
     */
    @Override
    public Saga getSagaById(Long id) {
        return sagaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Saga no encontrada con ID: " + id));
    }

    /**
     * Crea una nueva saga y la vincula automáticamente al usuario actual.
     * @param saga Objeto con la información de la nueva saga.
     * @return La saga persistida con su identificador generado.
     */
    @Override
    public Saga createSaga(Saga saga) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        saga.setUser(User.builder().id(currentUserId).build());
        return sagaRepository.save(saga);
    }

    /**
     * Actualiza los metadatos de una saga existente.
     * @param id Identificador de la saga a modificar.
     * @param sagaDetails Objeto que contiene el nuevo nombre y URL de portada.
     * @return La saga tras persistir los cambios.
     */
    @Override
    public Saga updateSaga(Long id, Saga sagaDetails) {
        Saga saga = getSagaById(id);
        saga.setName(sagaDetails.getName());
        saga.setCoverUrl(sagaDetails.getCoverUrl());
        return sagaRepository.save(saga);
    }

    /**
     * Elimina una saga de la base de datos de forma segura.
     * <p>
     * Para mantener la integridad de la base de datos y evitar errores de claves foráneas (FK),
     * este método primero desvincula todos los libros asociados a la saga, estableciendo
     * su saga e índice de volumen como nulos antes de proceder con el borrado físico.
     * </p>
     * @param id Identificador de la saga a eliminar.
     */
    @Override
    public void deleteSaga(Long id) {
        Saga saga = getSagaById(id);

        // Recuperación y desvinculación de los libros asociados
        List<Book> books = saga.getBooks();
        if (books != null) {
            for (Book book : books) {
                // Los libros se mantienen en la biblioteca pero pierden su pertenencia a la saga
                book.setSaga(null);
                book.setIndexInSaga(null);
                bookRepository.save(book);
            }
        }

        // Eliminación física de la saga en la base de datos
        sagaRepository.delete(saga);
    }
}

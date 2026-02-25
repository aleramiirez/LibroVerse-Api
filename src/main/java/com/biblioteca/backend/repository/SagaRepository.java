package com.biblioteca.backend.repository;

import com.biblioteca.backend.model.Saga;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para la entidad Saga.
 * <p>
 * Gestiona las colecciones de libros (series). Las sagas son privadas por usuario.
 * </p>
 */
@Repository
public interface SagaRepository extends JpaRepository<Saga, Long> {

    /**
     * Busca una saga por nombre dentro de la colección de un usuario concreto.
     * @param userId ID del usuario.
     * @param name Nombre de la saga.
     * @return Un Optional con la saga si existe para ese usuario.
     */
    Optional<Saga> findByUserIdAndName(Long userId, String name);

    /**
     * Lista todas las sagas creadas por un usuario.
     * @param userId ID del usuario.
     * @return Lista de sagas del usuario.
     */
    List<Saga> findByUserId(Long userId);
}

package com.biblioteca.backend.repository;

import com.biblioteca.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio JPA para la entidad User.
 * <p>
 * Esencial para el sistema de seguridad. Permite localizar usuarios durante
 * el proceso de login y validar registros.
 * </p>
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Busca un usuario por su email, que actúa como identificador de inicio de sesión.
     * @param email Correo electrónico del usuario.
     * @return Un Optional con el usuario.
     */
    Optional<User> findByEmail(String email);


    /**
     * Comprueba si un email ya está registrado en el sistema.
     * @param email Email a verificar.
     * @return true si ya existe un usuario con ese email.
     */
    boolean existsByEmail(String email);
}

package com.biblioteca.backend.repository;

import com.biblioteca.backend.model.Saga;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SagaRepository extends JpaRepository<Saga, Long> {
    java.util.Optional<Saga> findByUserIdAndName(Long userId, String name);

    java.util.List<Saga> findByUserId(Long userId);
}
package com.biblioteca.backend.repository;

import com.biblioteca.backend.model.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenreRepository extends JpaRepository<Genre, Long> {
    java.util.Optional<Genre> findByName(String name);
}
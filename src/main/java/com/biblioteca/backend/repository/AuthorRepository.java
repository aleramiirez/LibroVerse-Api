package com.biblioteca.backend.repository;

import com.biblioteca.backend.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorRepository extends JpaRepository<Author, Long> {
    boolean existsByName(String name);

    java.util.Optional<Author> findByName(String name);
}

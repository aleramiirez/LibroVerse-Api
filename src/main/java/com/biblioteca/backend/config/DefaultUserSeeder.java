package com.biblioteca.backend.config;

import com.biblioteca.backend.model.User;
import com.biblioteca.backend.repository.BookRepository;
import com.biblioteca.backend.repository.SagaRepository;
import com.biblioteca.backend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DefaultUserSeeder {

    @Bean
    public CommandLineRunner initDefaultUser(UserRepository userRepository,
            BookRepository bookRepository,
            SagaRepository sagaRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            // Verificar si el usuario por defecto existe
            User defaultUser = userRepository.findById(1L).orElseGet(() -> {
                User user = User.builder()
                        .email("admin@libroverse.com")
                        .password(passwordEncoder.encode("admin123"))
                        .name("Administrador")
                        .build();
                return userRepository.save(user);
            });

            // Migrar libros antiguos que no tengan usuario
            bookRepository.findAll().forEach(book -> {
                if (book.getUser() == null) {
                    book.setUser(defaultUser);
                    bookRepository.save(book);
                }
            });

            // Migrar sagas antiguas que no tengan usuario
            sagaRepository.findAll().forEach(saga -> {
                if (saga.getUser() == null) {
                    saga.setUser(defaultUser);
                    sagaRepository.save(saga);
                }
            });
        };
    }
}

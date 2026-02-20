package com.biblioteca.backend.service.impl;

import com.biblioteca.backend.dto.GoogleBooksResponse;
import com.biblioteca.backend.service.ExternalBookSearchService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;

/**
 * Implementación concreta que actúa como adaptador hacia la API de Google
 * Books.
 * Aisla la lógica de red y las peticiones HTTP del resto de la aplicación.
 */
@Service
public class GoogleBooksServiceImpl implements ExternalBookSearchService {

    private final RestClient restClient;
    @Value("${google.books.api-key}")
    private String apiKey;

    /**
     * Constructor que inicializa el cliente HTTP para comunicarse con Google Books.
     * * @param restClientBuilder Constructor inyectado por la configuración de
     * Spring
     * para preconfigurar la URL base de la API externa.
     */
    public GoogleBooksServiceImpl(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder
                .baseUrl("https://www.googleapis.com/books/v1")
                .build();
    }

    /**
     * Busca coincidencias de libros en el catálogo público de Google Books.
     * Realiza una petición GET externa limitando los resultados para optimizar la
     * carga.
     * * @param title El título exacto o parcial del libro ingresado por el usuario.
     * 
     * @return Una lista con un máximo de 5 coincidencias
     *         ({@link GoogleBooksResponse.Item}).
     *         Devuelve una lista vacía si la API no devuelve resultados o si la
     *         respuesta es nula.
     */
    @Override
    public List<GoogleBooksResponse.Item> searchByTitle(String title) {
        try {
            String formattedTitle = title.replace(" ", "+");

            GoogleBooksResponse response = restClient.get()
                    .uri("/volumes?q=intitle:{title}&maxResults=5&key={key}", formattedTitle, apiKey)
                    .retrieve()
                    .body(GoogleBooksResponse.class);

            if (response != null && response.items() != null) {
                return response.items();
            }
        } catch (Exception e) {
            System.err.println("Error buscando en Google Books: " + e.getMessage());
            e.printStackTrace();
        }

        return Collections.emptyList();
    }
}
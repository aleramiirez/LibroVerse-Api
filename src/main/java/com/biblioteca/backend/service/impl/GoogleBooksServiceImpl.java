package com.biblioteca.backend.service.impl;

import com.biblioteca.backend.dto.GoogleBooksResponse;
import com.biblioteca.backend.service.ExternalBookSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;

/**
 * Implementación concreta que actúa como adaptador hacia la API de Google Books.
 * <p>
 * Esta clase aísla la lógica de red y las peticiones HTTP externas del resto de la aplicación,
 * permitiendo buscar metadatos de libros en el catálogo global de Google.
 * </p>
 */
@Service
public class GoogleBooksServiceImpl implements ExternalBookSearchService {

    /** Cliente HTTP moderno de Spring para realizar peticiones REST. */
    private final RestClient restClient;

    /** Clave de API de Google inyectada desde la configuración del sistema. */
    @Value("${google.books.api-key}")
    private String apiKey;

    /**
     * Constructor que inicializa y preconfigura el cliente HTTP.
     * <p>
     * Utiliza un {@link RestClient.Builder} para establecer la URL base
     * de la API de Google Books v1.
     * </p>
     * @param restClientBuilder Constructor inyectado por Spring para configurar el cliente.
     */
    public GoogleBooksServiceImpl(final RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder
                .baseUrl("https://www.googleapis.com/books/v1")
                .build();
    }

    /**
     * Busca coincidencias de libros en el catálogo público de Google Books mediante un título.
     * <p>
     * Realiza una petición GET externa. Los espacios en el título se reemplazan por '+' para
     * cumplir con el formato de consulta de Google. La búsqueda se limita a los 5 resultados
     * más relevantes para optimizar el rendimiento.
     * </p>
     * @param title El título exacto o parcial ingresado por el usuario en la interfaz.
     * @return Una lista con un máximo de 5 coincidencias ({@link GoogleBooksResponse.Item}).
     * Si ocurre un error o no hay resultados, devuelve una lista vacía.
     */
    @Override
    public List<GoogleBooksResponse.Item> searchByTitle(String title) {
        try {
            // Formateo del título para la URL (reemplazo de espacios por +)
            String formattedTitle = title.replace(" ", "+");

            // Ejecución de la petición GET externa a Google Books
            GoogleBooksResponse response = restClient.get()
                    .uri("/volumes?q=intitle:{title}&maxResults=5&key={key}", formattedTitle, apiKey)
                    .retrieve()
                    .body(GoogleBooksResponse.class);

            // Validación de la respuesta para evitar NullPointerException
            if (response != null && response.items() != null) {
                return response.items();
            }
        } catch (Exception e) {
            // Log de error en caso de fallo en la comunicación o serialización
            System.err.println("Error buscando en Google Books: " + e.getMessage());
            e.printStackTrace();
        }

        // Retorno de lista segura en caso de ausencia de datos
        return Collections.emptyList();
    }
}

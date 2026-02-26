package com.biblioteca.backend.service;

import com.biblioteca.backend.dto.GoogleBooksResponse;
import java.util.List;

/**
 * Interfaz para la búsqueda de libros en catálogos externos.
 * <p>
 * Abstrae la implementación de servicios de terceros (como Google Books API)
 * para permitir la búsqueda de metadatos de libros por título o autor.
 * </p>
 */
public interface ExternalBookSearchService {

    /**
     * Realiza una búsqueda en la API externa utilizando un término de consulta.
     * @param title Título o palabras clave para la búsqueda.
     * @return Lista de resultados mapeados al formato interno de la aplicación.
     */
    List<GoogleBooksResponse.Item> searchByTitle(String title);
}

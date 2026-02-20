package com.biblioteca.backend.service;

import com.biblioteca.backend.dto.GoogleBooksResponse;
import java.util.List;

/**
 * Puerto de salida para buscar metadatos de libros en catálogos externos.
 * Aisla la lógica de negocio del proveedor específico (Google Books, OpenLibrary, etc.).
 */
public interface ExternalBookSearchService {

    /**
     * Busca coincidencias de libros por su título en un servicio de terceros.
     * @param title El título introducido por el usuario.
     * @return Lista de coincidencias limitadas.
     */
    List<GoogleBooksResponse.Item> searchByTitle(String title);
}
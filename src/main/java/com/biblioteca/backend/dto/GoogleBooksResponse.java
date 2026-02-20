package com.biblioteca.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

/**
 * Data Transfer Object (DTO) para mapear la respuesta JSON de la API de Google
 * Books.
 * <p>
 * Implementado utilizando Java Records para garantizar inmutabilidad,
 * código limpio y una mínima huella de memoria en la JVM, mapeando
 * estrictamente
 * los campos necesarios y descartando el resto del payload.
 * </p>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record GoogleBooksResponse(List<Item> items) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Item(VolumeInfo volumeInfo) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record VolumeInfo(
            String title,
            List<String> authors,
            ImageLinks imageLinks) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ImageLinks(
            String thumbnail,
            String medium,
            String large,
            String extraLarge) {
    }
}
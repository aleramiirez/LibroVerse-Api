package com.biblioteca.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

/**
 * Data Transfer Object (DTO) diseñado para mapear la respuesta JSON de la API externa de Google Books.
 * <p>
 * Utiliza la anotación {@code @JsonIgnoreProperties(ignoreUnknown = true)} para descartar automáticamente
 * los cientos de campos que devuelve Google que no son relevantes para nuestra aplicación,
 * ahorrando procesamiento y memoria.
 * </p>
 * @param items Lista de resultados (volúmenes) encontrados en la búsqueda.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record GoogleBooksResponse(List<Item> items) {

    /**
     * Representa un elemento individual (libro) dentro de la lista de resultados de Google.
     * @param volumeInfo Contenedor de la información bibliográfica detallada.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Item(VolumeInfo volumeInfo) {
    }

    /**
     * Contiene los metadatos principales del libro según la estructura de la API de Google.
     * @param title Título oficial del libro.
     * @param authors Lista de nombres de los autores.
     * @param imageLinks Objeto con las distintas resoluciones de la portada del libro.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record VolumeInfo(
            String title,
            List<String> authors,
            ImageLinks imageLinks) {
    }

    /**
     * Almacena las URLs de las imágenes de portada proporcionadas por Google Books.
     * @param thumbnail URL de la portada en tamaño miniatura (usada en listas).
     * @param medium URL de la portada en tamaño medio.
     * @param large URL de la portada en tamaño grande.
     * @param extraLarge URL de la portada en máxima resolución disponible.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ImageLinks(
            String thumbnail,
            String medium,
            String large,
            String extraLarge) {
    }
}

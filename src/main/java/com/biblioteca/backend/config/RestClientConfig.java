package com.biblioteca.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * Configuración de infraestructura para los clientes HTTP de la aplicación.
 */
@Configuration
public class RestClientConfig {

    /**
     * Define el Bean de RestClient.Builder necesario para realizar peticiones externas.
     * Al definirlo aquí, Spring podrá inyectarlo automáticamente en GoogleBooksServiceImpl.
     * * @return Una instancia por defecto de RestClient.Builder.
     */
    @Bean
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }
}
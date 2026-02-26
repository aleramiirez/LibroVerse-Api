package com.biblioteca.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * Configuración para el cliente HTTP de la aplicación.
 * <p>
 * Define los Beans necesarios para realizar peticiones REST a servicios externos.
 * Spring Boot 3 introduce {@link RestClient} como una alternativa moderna, funcional
 * y síncrona al antiguo RestTemplate.
 * </p>
 */
@Configuration
public class RestClientConfig {

    /**
     * Define un Bean para el constructor de RestClient.
     * <p>
     * Se expone el {@link RestClient.Builder} en lugar de una instancia única de RestClient
     * para permitir que cada servicio (como GoogleBooksService) pueda personalizar su
     * propia instancia con URLs base, headers específicos o timeouts sin afectar
     * al resto de la aplicación.
     * </p>
     * @return Un constructor preconfigurado de RestClient listo para ser inyectado.
     */
    @Bean
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }
}
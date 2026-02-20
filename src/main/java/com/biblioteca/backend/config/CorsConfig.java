package com.biblioteca.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración de seguridad global para CORS.
 * Permite que el frontend alojado en dominios externos (como Vercel o localhost)
 * pueda consumir la API de la biblioteca sin ser bloqueado por el navegador.
 */
@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                        // Cambiamos allowedOrigins por allowedOriginPatterns("*")
                        // Esto permite que tu móvil (192.168.X.X) pueda conectarse sin problemas
                        .allowedOriginPatterns("*")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}
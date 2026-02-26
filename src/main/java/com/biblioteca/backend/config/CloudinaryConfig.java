package com.biblioteca.backend.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración para el servicio de almacenamiento en la nube Cloudinary.
 * <p>
 * Inicializa el cliente de Cloudinary utilizando las credenciales inyectadas
 * desde las variables de entorno, permitiendo que la aplicación suba
 * imágenes (portadas) y archivos raw (EPUBs) de forma remota.
 * </p>
 */
@Configuration
public class CloudinaryConfig {

    @Value("${cloudinary.cloud-name}")
    private String cloudName;

    @Value("${cloudinary.api-key}")
    private String apiKey;

    @Value("${cloudinary.api-secret}")
    private String apiSecret;

    /**
     * Define el Bean del cliente de Cloudinary para ser inyectado en los servicios.
     * @return Instancia configurada de {@link Cloudinary}.
     */
    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret,
                "secure", true // Obliga a devolver URLs con HTTPS
        ));
    }

}

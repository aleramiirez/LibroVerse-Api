package com.biblioteca.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Configuración personalizada para el manejo de recursos web en Spring MVC.
 * <p>
 * Esta clase implementa {@link WebMvcConfigurer} para extender la funcionalidad por defecto de Spring,
 * permitiendo exponer carpetas del sistema de archivos local como recursos accesibles mediante HTTP.
 * Es fundamental para servir las portadas de libros y archivos multimedia almacenados en el servidor.
 * </p>
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * Configura los manejadores de recursos estáticos.
     * <p>
     * Este método establece un mapeo entre una ruta URL virtual (ej. /uploads/**) y una
     * ubicación física en el disco duro. De esta forma, cualquier archivo guardado en la
     * carpeta "uploads" de la raíz del proyecto puede ser visualizado en el navegador
     * o descargado por el frontend.
     * </p>
     * * @param registry Registro de manejadores de recursos de Spring MVC.
     */
    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        // Obtención de la ruta absoluta de la carpeta "uploads" en el servidor
        Path uploadDir = Paths.get("uploads");
        String uploadPath = uploadDir.toFile().getAbsolutePath();

        // Mapear la ruta URL "/uploads/**" al directorio físico correspondiente
        // El prefijo "file:/" indica a Spring que busque en el sistema de archivos local
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:/" + uploadPath + "/");
    }
}

package com.biblioteca.backend.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Configuración principal de seguridad de la aplicación.
 * <p>
 * Define la cadena de filtros de seguridad, políticas de acceso a endpoints,
 * gestión de sesiones sin estado (Stateless), configuración de CORS y el
 * sistema de cifrado de contraseñas.
 * </p>
 */
@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /** Filtro personalizado para la validación de tokens JWT en cada petición. */
    private final JwtAuthenticationFilter jwtAuthFilter;

    /**
     * Configura la cadena de filtros de seguridad (Security Filter Chain).
     * <p>
     * Las reglas configuradas son:
     * 1. Deshabilitar CSRF (innecesario para APIs REST con JWT).
     * 2. Habilitar CORS según la configuración del Bean {@code corsConfigurationSource}.
     * 3. Definir rutas públicas (auth, documentación Swagger, descargas) y proteger el resto.
     * 4. Establecer política de sesión STATELESS (sin estado).
     * 5. Insertar el filtro JWT antes del filtro de autenticación estándar por nombre/password.
     * </p>
     * @param http Objeto para configurar la seguridad a nivel web.
     * @return El objeto {@link SecurityFilterChain} construido.
     * @throws Exception Si ocurre un error en la configuración.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(final HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        // Endpoints públicos de autenticación
                        .requestMatchers("/api/auth/**").permitAll()
                        // Endpoints públicos para recursos multimedia
                        .requestMatchers("/api/files/download/**").permitAll()
                        // Documentación técnica de la API (Swagger/OpenAPI)
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        // Cualquier otra ruta requiere token válido
                        .anyRequest().authenticated())
                // La API no guarda sesiones en el servidor; cada petición debe enviar su token
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Inyección del guardia de seguridad JWT
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Expone el {@link AuthenticationManager} como un Bean.
     * <p>
     * Este gestor es el encargado de orquestar el proceso de validación de credenciales
     * en el servicio de login.
     * </p>
     * @param config Configuración de autenticación de Spring.
     * @return El AuthenticationManager configurado.
     * @throws Exception Si no se puede recuperar el gestor.
     */
    @Bean
    public AuthenticationManager authenticationManager(final AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Define el algoritmo de cifrado para las contraseñas.
     * @return Una instancia de {@link BCryptPasswordEncoder}, estándar de la industria.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(8);
    }

    /**
     * Configuración de CORS (Cross-Origin Resource Sharing).
     * <p>
     * Permite que el frontend (ej. React/Vue en localhost o Vercel) pueda realizar
     * peticiones a este backend. Gestiona métodos permitidos, cabeceras y orígenes.
     * </p>
     * @return La configuración de CORS lista para ser aplicada por Spring.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Recuperación de la URL del frontend desde variables de entorno para entornos Cloud
        String allowedOrigin = System.getenv("FRONTEND_URL");
        if (allowedOrigin == null || allowedOrigin.isEmpty()) {
            allowedOrigin = "http://localhost:5173"; // Por defecto para desarrollo (Vite)
        }
        configuration.setAllowedOrigins(List.of("http://localhost:5173", allowedOrigin));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}

package com.biblioteca.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro de autenticación basado en JWT.
 * <p>
 * Este componente intercepta cada petición HTTP entrante (extiende de {@link OncePerRequestFilter})
 * para extraer el token del encabezado "Authorization", validarlo y establecer la identidad
 * del usuario en el contexto de seguridad de Spring si el token es correcto.
 * </p>
 */
@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /** Utilidad para la manipulación y validación de tokens JWT. */
    private final JwtUtil jwtUtil;

    /** Servicio para cargar los detalles del usuario desde la base de datos. */
    private final UserDetailsService userDetailsService;

    /**
     * Método interno que ejecuta la lógica de filtrado para cada solicitud.
     * <p>
     * El flujo de ejecución es:
     * 1. Extraer el encabezado "Authorization".
     * 2. Verificar si el encabezado contiene un token Bearer.
     * 3. Extraer el nombre de usuario (email) del token.
     * 4. Si el usuario no está autenticado aún, validar el token contra los datos del sistema.
     * 5. Establecer la autenticación en el {@link SecurityContextHolder}.
     * </p>
     * * @param request La solicitud HTTP entrante.
     * @param response La respuesta HTTP saliente.
     * @param filterChain La cadena de filtros de Spring Security.
     * @throws ServletException Si ocurre un error de procesamiento.
     * @throws IOException Si ocurre un error de entrada/salida.
     */
    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
                                    final FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        String username = null;
        String jwt = null;

        // 1. Extracción del token JWT del encabezado Authorization
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(jwt);
            } catch (Exception e) {
                // Si el token es inválido o ha expirado, el username permanecerá nulo
                // y la petición será denegada por los filtros posteriores.
            }
        }

        // 2. Validación del usuario y creación del contexto de autenticación
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // Verificación de integridad y expiración del token
            if (jwtUtil.validateToken(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                // Enriquecimiento del token con detalles de la solicitud (IP, sesión, etc.)
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Establecimiento de la identidad del usuario en el contexto de Spring
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 3. Continuar con el siguiente filtro en la cadena (o llegar al controlador)
        filterChain.doFilter(request, response);
    }
}

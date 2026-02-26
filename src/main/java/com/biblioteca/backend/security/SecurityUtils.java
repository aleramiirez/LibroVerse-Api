package com.biblioteca.backend.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Clase de utilidad para operaciones relacionadas con la seguridad y el contexto de usuario.
 * <p>
 * Proporciona métodos estáticos para acceder de forma sencilla a la información del
 * usuario autenticado en el hilo actual de ejecución, evitando la redundancia de código
 * en la capa de servicio y controladores.
 * </p>
 */
public class SecurityUtils {

    /**
     * Recupera el identificador único (ID) del usuario actualmente autenticado.
     * <p>
     * El método consulta el {@link SecurityContextHolder}, extrae el objeto de autenticación
     * y realiza un casting hacia {@link CustomUserDetails} para obtener el ID de la base de datos.
     * </p>
     * * @return El ID (Long) del usuario vinculado al token JWT de la petición actual.
     * @throws RuntimeException Si se intenta acceder a este método en una ruta no protegida
     * o si no existe una autenticación válida en el contexto.
     */
    public static Long getCurrentUserId() {
        // Obtenemos el objeto de autenticación del contexto de seguridad de Spring
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Verificamos que la autenticación exista y que el Principal sea de nuestro tipo personalizado
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            return ((CustomUserDetails) authentication.getPrincipal()).getUser().getId();
        }
        // Error de seguridad: se intentó realizar una operación de usuario sin estar logueado
        throw new RuntimeException("No hay usuario autenticado en el contexto de seguridad");
    }
}

package com.biblioteca.backend.security;

import com.biblioteca.backend.model.User;
import com.biblioteca.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Servicio personalizado para la carga de detalles de usuario.
 * <p>
 * Implementa la interfaz {@link UserDetailsService} de Spring Security para
 * proporcionar una estrategia de búsqueda de usuarios basada en la base de datos
 * de la aplicación, utilizando el correo electrónico como identificador único.
 * </p>
 */
@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    /** Repositorio para la consulta de usuarios en la base de datos. */
    private final UserRepository userRepository;

    /**
     * Localiza a un usuario basándose en su dirección de correo electrónico.
     * <p>
     * Este método es invocado por el gestor de autenticación de Spring Security
     * durante el proceso de login para verificar la existencia del usuario y
     * obtener sus credenciales cifradas.
     * </p>
     * @param email El correo electrónico introducido en el formulario de login.
     * @return Una instancia de {@link UserDetails} (específicamente {@link CustomUserDetails})
     * que contiene la información de seguridad del usuario.
     * @throws UsernameNotFoundException Si no existe ningún usuario registrado con el email proporcionado.
     */
    @Override
    public UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException {
        // Búsqueda del usuario en la base de datos
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + email));

        // Conversión de la entidad JPA User al formato que Spring Security entiende
        return new CustomUserDetails(user);
    }
}

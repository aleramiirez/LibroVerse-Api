package com.biblioteca.backend.security;

import com.biblioteca.backend.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * Adaptador para la entidad de usuario compatible con Spring Security.
 * <p>
 * Esta clase implementa la interfaz {@link UserDetails}, actuando como un "wrapper"
 * o envoltorio de la entidad {@link User}. Permite que Spring Security gestione
 * la autenticación utilizando los datos reales de la base de datos de la aplicación.
 * </p>
 */
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    /** Entidad de usuario original de la base de datos. */
    private final User user;

    /**
     * Recupera la entidad de usuario completa.
     * <p>
     * Útil cuando se necesita acceder a atributos adicionales del usuario (como el ID o nombre)
     * que no forman parte de la interfaz estándar de UserDetails.
     * </p>
     * @return La entidad {@link User} subyacente.
     */
    public User getUser() {
        return user;
    }

    /**
     * Devuelve las autoridades (roles/permisos) concedidas al usuario.
     * @return Una colección vacía, dado que este sistema utiliza un modelo de permisos simplificado.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    /**
     * Devuelve la contraseña utilizada para la autenticación.
     * @return La contraseña cifrada almacenada en la entidad {@link User}.
     */
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * Devuelve el nombre de usuario utilizado para la autenticación.
     * @return El correo electrónico del usuario, que actúa como identificador único en el sistema.
     */
    @Override
    public String getUsername() {
        return user.getEmail();
    }

    /**
     * Indica si la cuenta del usuario ha expirado.
     * @return {@code true} (siempre activa por defecto).
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Indica si el usuario está bloqueado o deshabilitado.
     * @return {@code true} (siempre desbloqueada por defecto).
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Indica si las credenciales (contraseña) han expirado.
     * @return {@code true} (siempre válidas por defecto).
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Indica si el usuario está habilitado para acceder al sistema.
     * @return {@code true} (siempre habilitado por defecto).
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}

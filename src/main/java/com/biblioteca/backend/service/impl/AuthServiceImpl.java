package com.biblioteca.backend.service.impl;

import com.biblioteca.backend.dto.auth.AuthResponse;
import com.biblioteca.backend.dto.auth.LoginRequest;
import com.biblioteca.backend.dto.auth.RegisterRequest;
import com.biblioteca.backend.model.User;
import com.biblioteca.backend.repository.UserRepository;
import com.biblioteca.backend.security.CustomUserDetails;
import com.biblioteca.backend.security.JwtUtil;
import com.biblioteca.backend.service.AuthServiceI;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Implementación del servicio de autenticación y seguridad de la aplicación.
 * <p>
 * Esta clase centraliza la lógica de negocio para el registro de nuevos usuarios
 * y la validación de credenciales durante el inicio de sesión, integrándose con
 * Spring Security para el manejo de contraseñas y la generación de tokens JWT.
 * </p>
 */
@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthServiceI {

    /** Repositorio para la gestión de persistencia de usuarios. */
    private final UserRepository userRepository;

    /** Componente para el cifrado seguro de contraseñas utilizando BCrypt. */
    private final PasswordEncoder passwordEncoder;

    /** Utilidad para la creación y gestión de tokens JSON Web Tokens. */
    private final JwtUtil jwtUtil;

    /** Gestor de autenticación nativo de Spring Security. */
    private final AuthenticationManager authenticationManager;

    /**
     * Procesa el registro de un nuevo usuario en el sistema.
     * <p>
     * El flujo incluye verificar la unicidad del correo electrónico, cifrar la
     * contraseña sensible y generar un token de acceso inicial para el usuario.
     * </p>
     * @param request Objeto que contiene el nombre, email y contraseña del solicitante.
     * @return {@link AuthResponse} con el token JWT generado y los datos de perfil.
     * @throws RuntimeException Si el correo electrónico ya se encuentra vinculado a otra cuenta.
     */
    @Override
    public AuthResponse register(final RegisterRequest request) {
        // Validación de existencia previa del usuario
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        // Construcción y cifrado de la nueva entidad de usuario
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        userRepository.save(user);

        // Generación del token de sesión tras el registro exitoso
        String token = jwtUtil.generateToken(new CustomUserDetails(user));
        return new AuthResponse(token, user.getName(), user.getEmail());
    }

    /**
     * Valida las credenciales de acceso de un usuario existente.
     * <p>
     * Utiliza el gestor de autenticación para comprobar el email y la contraseña.
     * Si la validación es correcta, devuelve un nuevo token JWT válido.
     * </p>
     * @param request Objeto con el email y la contraseña para el login.
     * @return {@link AuthResponse} con el token de acceso, nombre y email del usuario.
     * @throws RuntimeException Si las credenciales son incorrectas o el usuario no existe.
     */
    @Override
    public AuthResponse login(final LoginRequest request) {
        // Validación de credenciales mediante el AuthenticationManager
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        // Recuperación de la información del usuario tras autenticación exitosa
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Generación del token JWT para las peticiones subsecuentes
        String token = jwtUtil.generateToken(new CustomUserDetails(user));
        return new AuthResponse(token, user.getName(), user.getEmail());
    }
}

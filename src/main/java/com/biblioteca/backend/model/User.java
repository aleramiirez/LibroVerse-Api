package com.biblioteca.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

/**
 * Entidad que representa a un Usuario registrado en el sistema.
 * <p>
 * Esta clase es el núcleo de la seguridad y personalización de la biblioteca.
 * Almacena las credenciales de acceso y el perfil básico del usuario.
 * </p>
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class User {

    /**
     * Identificador único del usuario (Clave primaria autoincremental).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Correo electrónico del usuario.
     * Se utiliza como nombre de usuario único para el proceso de autenticación.
     */
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * Contraseña del usuario almacenada de forma segura (encriptada).
     * La anotación @JsonIgnore garantiza que nunca se envíe al frontend en las respuestas API.
     */
    @Column(nullable = false)
    @JsonIgnore
    private String password;

    /**
     * Nombre personal o apodo del usuario para mostrar en la interfaz.
     */
    @Column(nullable = false)
    private String name;

}

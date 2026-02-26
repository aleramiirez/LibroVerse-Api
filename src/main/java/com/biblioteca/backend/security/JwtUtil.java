package com.biblioteca.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Clase utilitaria (Componente de Spring) encargada de la gestión completa de los tokens JWT.
 * <p>
 * Sus responsabilidades incluyen la creación, firmado, validación y extracción de
 * información (Claims) de los JSON Web Tokens usados para la autenticación de la API.
 * </p>
 */
@Component
public class JwtUtil {

    /**
     * Clave secreta utilizada para firmar el token. Se inyecta desde application.yml o variables de entorno.
     * Se recomienda usar un secreto de al menos 256 bits (32 caracteres).
     */
    @Value("${jwt.secret}")
    private String secret;

    /**
     * Tiempo de expiración del token en milisegundos.
     * Por defecto se establece en 86400000 ms (24 horas).
     */
    @Value("${jwt.expiration:86400000}")
    private long jwtExpirationInMs;

    /**
     * Genera la clave criptográfica segura (HMAC SHA) a partir de la cadena secreta.
     * @return SecretKey objeto de clave secreta utilizado para firmar y verificar el JWT.
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Extrae el nombre de usuario (subject) contenido dentro del token JWT.
     * @param token El token JWT proporcionado por el cliente.
     * @return El nombre de usuario almacenado en el token.
     */
    public String extractUsername(final String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrae la fecha exacta de expiración del token JWT.
     * @param token El token JWT proporcionado por el cliente.
     * @return Objeto Date representando cuándo expira el token.
     */
    public Date extractExpiration(final String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Método genérico para extraer una propiedad específica (Claim) del token.
     * @param token El token JWT proporcionado.
     * @param claimsResolver Función que define qué dato específico se quiere extraer de los Claims.
     * @param <T> El tipo de dato que se va a devolver.
     * @return El valor del Claim solicitado.
     */
    public <T> T extractClaim(final String token, final Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Desencripta y extrae el cuerpo completo (Payload/Claims) del token JWT
     * utilizando la clave secreta del servidor.
     * @param token El token JWT proporcionado.
     * @return Objeto Claims que contiene toda la información (usuario, fechas, roles, etc.).
     */
    private Claims extractAllClaims(final String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Verifica si un token JWT ya ha superado su fecha y hora de expiración.
     * @param token El token JWT proporcionado.
     * @return true si el token está caducado, false si aún es válido.
     */
    private Boolean isTokenExpired(final String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Genera un nuevo token JWT para un usuario que acaba de iniciar sesión o registrarse.
     * @param userDetails Objeto que contiene la información del usuario autenticado.
     * @return Una cadena de texto (String) que representa el token JWT firmado.
     */
    public String generateToken(final UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }

    /**
     * Método privado interno que construye físicamente el token JWT, asignando los claims,
     * el sujeto, las fechas de creación/expiración y firmándolo criptográficamente.
     * @param claims Mapa con información adicional que se quiera incrustar en el token (vacío por defecto).
     * @param subject El sujeto principal del token (habitualmente el username o email del usuario).
     * @return El token JWT final en formato String.
     */
    private String createToken(final Map<String, Object> claims, final String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationInMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Valida si un token JWT es legítimo comprobando dos cosas:
     * 1. Que el usuario del token coincida con el usuario que está intentando acceder.
     * 2. Que el token no haya expirado en el tiempo.
     * @param token El token JWT proporcionado en la cabecera de la petición.
     * @param userDetails Los detalles del usuario cargados desde la base de datos.
     * @return true si el token es válido y pertenece al usuario, false en caso contrario.
     */
    public Boolean validateToken(final String token, final UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}

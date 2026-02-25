package com.biblioteca.backend.exception;

/**
 * Excepción personalizada para representar errores de recursos no encontrados.
 * <p>
 * Se utiliza principalmente en la capa de servicio cuando una búsqueda por identificador (ID)
 * no devuelve resultados en la base de datos. Al ser una {@link RuntimeException}, no obliga
 * a bloques try-catch explícitos, simplificando el código de negocio.
 * </p>
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Construye una nueva excepción con un mensaje detallado sobre el recurso faltante.
     * @param message Mensaje explicativo que será capturado por el manejador global.
     */
    public ResourceNotFoundException(final String message) {
        super(message);
    }
}

package com.biblioteca.backend.service;

import com.biblioteca.backend.model.Saga;
import java.util.List;

/**
 * Interfaz para la administración de las sagas literarias del usuario.
 * <p>
 * Define los métodos necesarios para organizar libros en colecciones o series,
 * permitiendo un seguimiento ordenado de los volúmenes y la gestión de metadatos
 * grupales como el nombre de la colección o su portada.
 * </p>
 */
public interface SagaServiceI {

    /**
     * Recupera todas las sagas pertenecientes al usuario que ha iniciado sesión.
     * @return Lista de entidades Saga asociadas al perfil del usuario actual.
     */
    List<Saga> getAllSagas();

    /**
     * Obtiene la información detallada de una saga específica mediante su identificador.
     * @param id Identificador único de la saga en la base de datos.
     * @return La entidad Saga encontrada.
     * @throws com.biblioteca.backend.exception.ResourceNotFoundException Si el ID no existe.
     */
    Saga getSagaById(Long id);

    /**
     * Crea y almacena una nueva saga en el sistema vinculada al usuario actual.
     * @param saga Objeto con la información de la nueva saga a registrar.
     * @return La saga guardada con su ID generado por la base de datos.
     */
    Saga createSaga(Saga saga);

    /**
     * Actualiza los datos de una saga existente (como el nombre o la imagen de portada).
     * @param id Identificador de la saga que se desea modificar.
     * @param sagaDetails Objeto que contiene los nuevos valores a aplicar.
     * @return La entidad Saga tras persistir los cambios en la base de datos.
     */
    Saga updateSaga(Long id, Saga sagaDetails);

    /**
     * Elimina una saga de la biblioteca del usuario de forma permanente.
     * <p>
     * Nota: Dependiendo de la lógica del negocio, esto puede desvincular los libros
     * asociados o eliminarlos en cascada según la configuración de la entidad.
     * </p>
     * @param id Identificador único de la saga a borrar.
     */
    void deleteSaga(Long id);
}

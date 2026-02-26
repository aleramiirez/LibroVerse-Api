package com.biblioteca.backend.controller;

import com.biblioteca.backend.dto.ErrorResponse;
import com.biblioteca.backend.model.Saga;
import com.biblioteca.backend.service.SagaServiceI;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de Sagas literarias.
 * <p>
 * Permite al usuario organizar sus libros en series lógicas y
 * gestionar los metadatos de dichas colecciones.
 * </p>
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/sagas")
@Tag(name = "Sagas", description = "Operaciones para gestionar colecciones y series de libros")
public class SagaController {

    private final SagaServiceI sagaService;

    /**
     * Obtiene todas las sagas creadas por el usuario.
     * @return Lista JSON de sagas disponibles.
     */
    @Operation(summary = "Listar todas las sagas",
            description = "Recupera todas las sagas registradas por el usuario actual")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de sagas recuperada con éxito"),
            @ApiResponse(responseCode = "401", description = "No autorizado - Token inválido o ausente",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<List<Saga>> getAllSagas() {
        return ResponseEntity.ok(sagaService.getAllSagas());
    }

    /**
     * Obtiene la información detallada de una saga por su ID.
     * @param id ID de la saga.
     * @return La saga solicitada.
     */
    @Operation(summary = "Obtener saga por ID",
            description = "Devuelve la información detallada de una saga específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Saga encontrada y devuelta"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "404", description = "La saga con el ID proporcionado no existe",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<Saga> getSagaById(@PathVariable final Long id) {
        return ResponseEntity.ok(sagaService.getSagaById(id));
    }

    /**
     * Crea una nueva saga en la cuenta del usuario.
     * @param saga Datos de la nueva saga.
     * @return La saga creada con su ID.
     */
    @Operation(summary = "Crear nueva saga", description = "Registra una nueva colección de libros en la base de datos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Saga creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @PostMapping
    public ResponseEntity<Saga> createSaga(@Valid @RequestBody final Saga saga) {
        return ResponseEntity.ok(sagaService.createSaga(saga));
    }

    /**
     * Actualiza una saga existente (nombre o portada).
     * @param id ID de la saga a editar.
     * @param saga Datos actualizados.
     * @return La saga modificada.
     */
    @Operation(summary = "Actualizar saga", description = "Modifica los detalles de una saga existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Saga actualizada correctamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "404", description = "Saga no encontrada para el usuario actual",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<Saga> updateSaga(@PathVariable final Long id,@Valid @RequestBody final Saga saga) {
        return ResponseEntity.ok(sagaService.updateSaga(id, saga));
    }

    /**
     * Elimina una saga de la biblioteca.
     * @param id ID de la saga a borrar.
     * @return Respuesta 204 No Content.
     */
    @Operation(summary = "Eliminar saga", description = "Borra definitivamente una saga de la cuenta del usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Saga eliminada con éxito"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "404", description = "La saga que se intenta borrar no existe",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSaga(@PathVariable final Long id) {
        sagaService.deleteSaga(id);
        return ResponseEntity.noContent().build();
    }
}

package com.biblioteca.backend.controller;

import com.biblioteca.backend.dto.DashboardResponse;
import com.biblioteca.backend.dto.ErrorResponse;
import com.biblioteca.backend.service.DashboardServiceI;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para la gestión del panel de control (Dashboard).
 * <p>
 * Proporciona una visión consolidada de las métricas de lectura y
 * el progreso actual del usuario.
 * </p>
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/dashboard")
@Tag(name = "Dashboard", description = "Estadísticas y resumen de actividad del usuario")
public class DashboardController {

    private final DashboardServiceI dashboardService;

    /**
     * Endpoint ligero para pintar la pantalla de inicio de la aplicación.
     * <p>
     * Ejemplo de uso: GET /api/dashboard
     * </p>
     * @return Un JSON con el libro actual, el siguiente de la saga y el contador anual.
     */
    @Operation(summary = "Obtener estadísticas",
            description = "Calcula libros leídos, promedio de lectura y género favorito")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Estadísticas generadas y recuperadas con éxito"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "No autorizado - El token JWT no es válido, ha expirado o no se encuentra" +
                            " en la cabecera",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno al procesar los cálculos estadísticos en el servidor",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping
    public ResponseEntity<DashboardResponse> getDashboard() {
        return ResponseEntity.ok(dashboardService.getDashboardStats());
    }
}

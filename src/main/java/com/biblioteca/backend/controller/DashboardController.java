package com.biblioteca.backend.controller;

import com.biblioteca.backend.dto.DashboardResponse;
import com.biblioteca.backend.service.DashboardServiceI;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@Tag(name = "Dashboard", description = "Métricas y estadísticas principales de la biblioteca")
public class DashboardController {

    private final DashboardServiceI dashboardService;

    public DashboardController(DashboardServiceI dashboardService) {
        this.dashboardService = dashboardService;
    }

    /**
     * Endpoint ligero para pintar la pantalla de inicio de la aplicación.
     * <p>
     * Ejemplo de uso: GET /api/dashboard
     * </p>
     * @return Un JSON con el libro actual, el siguiente de la saga y el contador anual.
     */
    @GetMapping
    public ResponseEntity<DashboardResponse> getDashboard() {
        return ResponseEntity.ok(dashboardService.getDashboardStats());
    }
}
package com.biblioteca.backend.service;

import com.biblioteca.backend.dto.DashboardResponse;

public interface DashboardServiceI {
    /**
     * Calcula y agrupa las estadísticas principales del usuario.
     * @return DTO con los datos del dashboard.
     */
    DashboardResponse getDashboardStats();
}
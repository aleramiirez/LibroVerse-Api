package com.biblioteca.backend.service;

import com.biblioteca.backend.dto.DashboardResponse;

/**
 * Interfaz para la generación de métricas y datos del Dashboard.
 * <p>
 * Define el contrato para obtener una visión general de la actividad del usuario,
 * como libros leídos, progreso actual y recomendaciones.
 * </p>
 */
public interface DashboardServiceI {

    /**
     * Compila toda la información estadística necesaria para la vista principal.
     * @return DashboardResponse con contadores, listas de lectura actual y retos.
     */
    DashboardResponse getDashboardStats();
}

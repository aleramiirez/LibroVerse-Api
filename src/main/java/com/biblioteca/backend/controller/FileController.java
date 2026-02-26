package com.biblioteca.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * Controlador REST encargado de la gestión y almacenamiento de archivos multimedia.
 * <p>
 * Proporciona los servicios necesarios para recibir archivos desde el frontend (como portadas
 * o archivos .epub) y persistirlos de forma segura en el sistema de archivos del servidor.
 * </p>
 */
@RestController
@RequestMapping("/api/files")
@Tag(name = "Archivos", description = "Gestión de subida de archivos (EPUBS y portadas)")
public class FileController {

    /** Ruta absoluta del directorio donde se almacenarán físicamente los archivos subidos. */
    private final Path fileStorageLocation;

    /**
     * Constructor del controlador.
     * <p>
     * Inicializa la ubicación de almacenamiento en la carpeta "uploads" de la raíz del proyecto.
     * Si el directorio no existe, intenta crearlo automáticamente al arrancar el servicio.
     * </p>
     * @throws RuntimeException Si ocurre un error crítico al intentar crear el directorio de almacenamiento.
     */
    public FileController() {
        this.fileStorageLocation = Paths.get("uploads").toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("No se pudo crear el directorio de uploads.", ex);
        }
    }

    /**
     * Endpoint para la subida de archivos individuales al servidor.
     * <p>
     * El proceso de subida incluye:
     * 1. Generación de un nombre único mediante UUID para evitar sobrescribir archivos existentes.
     * 2. Validación de seguridad para impedir el uso de caracteres de navegación de directorios (..).
     * 3. Copia física del flujo de datos del archivo al disco duro del servidor.
     * 4. Generación de una URL pública dinámica para acceder al archivo posteriormente.
     * </p>
     * @param file Objeto {@link MultipartFile} que contiene los datos binarios del archivo enviado por el cliente.
     * @return ResponseEntity con la URL completa de descarga del archivo (200 OK) o un mensaje de error detallado.
     */
    @Operation(summary = "Subir archivo", description = "Almacena un archivo en el servidor y devuelve su URL pública")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Archivo subido exitosamente. Devuelve la URL de acceso pública."),
            @ApiResponse(responseCode = "400",
                    description = "Petición inválida: el nombre del archivo contiene caracteres prohibidos."),
            @ApiResponse(responseCode = "500",
                    description = "Error interno del servidor al intentar escribir el archivo en disco.")
    })
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") final MultipartFile file) {
        // Generación de un nombre de archivo único para evitar colisiones
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

        try {
            // Validación de seguridad: Previene ataques de "Path Traversal"
            if (fileName.contains("..")) {
                return ResponseEntity.badRequest().body("Nombre de archivo inválido");
            }

            // Resolución de la ruta de destino y copia del archivo
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // Construcción dinámica de la URL de descarga basada en el contexto actual del servidor
            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/uploads/")
                    .path(fileName)
                    .toUriString();

            return ResponseEntity.ok(fileDownloadUri);

        } catch (IOException ex) {
            // Manejo de errores de entrada/salida durante la escritura en disco
            return ResponseEntity.internalServerError()
                    .body("No se pudo subir el archivo " + fileName + ". Intenta de nuevo.");
        }
    }
}

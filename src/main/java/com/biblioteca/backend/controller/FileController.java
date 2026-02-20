package com.biblioteca.backend.controller;

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

@RestController
@RequestMapping("/api/files")
@Tag(name = "Archivos", description = "Subida de imágenes y archivos")
public class FileController {

    private final Path fileStorageLocation;

    public FileController() {
        this.fileStorageLocation = Paths.get("uploads").toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("No se pudo crear el directorio de uploads.", ex);
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

        try {
            // Validar nombre de archivo
            if (fileName.contains("..")) {
                return ResponseEntity.badRequest().body("Nombre de archivo inválido");
            }

            // Copiar archivo al destino
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // Generar URL de descarga
            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/uploads/")
                    .path(fileName)
                    .toUriString();

            return ResponseEntity.ok(fileDownloadUri);

        } catch (IOException ex) {
            return ResponseEntity.internalServerError()
                    .body("No se pudo subir el archivo " + fileName + ". Intenta de nuevo.");
        }
    }
}

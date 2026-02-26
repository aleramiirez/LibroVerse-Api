package com.biblioteca.backend.controller;

import com.biblioteca.backend.dto.ErrorResponse;
import com.biblioteca.backend.service.FileServiceI;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
 * Expone los endpoints para recibir archivos (como portadas o .epub) y los deriva
 * al servicio correspondiente para su almacenamiento en la nube.
 * </p>
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/files")
@Tag(name = "Archivos", description = "Gestión de subida de archivos (EPUBS y portadas)")
public class FileController {

    private final FileServiceI fileService;

    /**
     * Endpoint para la subida de archivos al servidor en la nube (Cloudinary).
     * @param file Objeto MultipartFile enviado mediante FormData.
     * @return ResponseEntity con la URL pública (HTTPS) de descarga o visualización.
     */
    @Operation(summary = "Subir archivo", description = "Sube un archivo a la nube y devuelve su URL pública segura")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Archivo subido exitosamente. Devuelve la URL en texto plano."),
            @ApiResponse(responseCode = "400",
                    description = "El archivo enviado es inválido o está vacío",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500",
                    description = "Error interno al comunicarse con el proveedor de la nube",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(
            @Parameter(description = "Archivo binario a subir (.jpg, .png, .epub)")
            @RequestParam("file") final MultipartFile file) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("El archivo no puede estar vacío");
        }

        try {
            String fileUrl = fileService.uploadFile(file);
            return ResponseEntity.ok(fileUrl);
        } catch (Exception ex) {
            // Se captura cualquier error de I/O de Cloudinary
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("No se pudo subir el archivo a la nube. Detalle: " + ex.getMessage());
        }
    }
}

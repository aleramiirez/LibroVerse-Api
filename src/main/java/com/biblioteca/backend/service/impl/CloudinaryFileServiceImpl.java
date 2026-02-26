package com.biblioteca.backend.service.impl;

import com.biblioteca.backend.service.FileServiceI;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

/**
 * Implementación del servicio de archivos utilizando Cloudinary.
 * <p>
 * Esta clase se encarga de recibir los archivos (portadas o EPUBs) y enviarlos
 * a los servidores de Cloudinary, devolviendo la URL optimizada.
 * </p>
 */
@RequiredArgsConstructor
@Service
public class CloudinaryFileServiceImpl implements FileServiceI {

    private final Cloudinary cloudinary;

    /**
     * Sube el archivo a Cloudinary.
     * <p>
     * Nota técnica: Se utiliza "resource_type" en "auto" para que Cloudinary
     * sepa distinguir entre imágenes (portadas) y archivos RAW (como los .epub).
     * </p>
     * @param file El archivo a subir.
     * @return La URL segura (HTTPS) generada por Cloudinary.
     * @throws IOException Si falla la transmisión de los bytes.
     */
    @Override
    public String uploadFile(final MultipartFile file) throws IOException {
        // Generamos un identificador único para el archivo en la nube
        String uniqueFilename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

        // Parámetros de subida
        Map<String, Object> uploadParams = ObjectUtils.asMap(
                "public_id", uniqueFilename,
                "resource_type", "auto", // Crucial para que acepte tanto JPGs como EPUBs
                "folder", "libroverse_iploads" // Organiza los archivos en una carpeta dentro de Cloudinary
        );

        // Subida del archivo enviando sus bytes
        Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadParams);

        // Retorna la URL segura (HTPS) proporcionada por Cloudinary
        return uploadResult.get("secure_url").toString();
    }


    /**
     * Elimina un archivo de Cloudinary a partir de su URL pública.
     * <p>
     * Este método valida si la URL pertenece a Cloudinary antes de intentar el borrado.
     * Si la URL corresponde a un recurso externo (como Google Books), la operación se ignora.
     * </p>
     * @param fileUrl URL completa del archivo que se desea eliminar.
     */
    @Override
    public void deleteFile(final String fileUrl) {
        // Solo intentamos borrar si es una URL válida de Cloudinary
        if (fileUrl != null && fileUrl.contains("cloudinary.com")) {
            try {
                String publicId = extractPublicIdFromUrl(fileUrl);
                if (publicId != null) {
                    cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
                    System.out.println("Archivo eliminado de Cloudinary: " + publicId);
                }
            } catch (Exception e) {
                // Registro del error en consola en caso de fallo en la comunicación con la API
                System.err.println("Error al intentar eliminar archivo en Cloudinary: " + e.getMessage());
            }
        }
    }

    /**
     * Extrae el identificador único (public_id) de un recurso a partir de su URL de Cloudinary.
     * <p>
     * Realiza un parsing de la cadena para eliminar el dominio, el prefijo de subida,
     * la versión del archivo (v12345...) y la extensión del archivo, dejando únicamente
     * la ruta necesaria para las operaciones de gestión de Cloudinary.
     * </p>
     * @param url URL completa del recurso.
     * @return El public_id del recurso o {@code null} si la URL no tiene el formato esperado.
     */
    private String extractPublicIdFromUrl(final String url) {
        try {
            // Buscamos donde empieza la ruta real después de /upload/
            int uploadIndex = url.indexOf("/upload/");
            if (uploadIndex == -1) return null;

            String path = url.substring(uploadIndex + 8);

            // Eliminamos la versión si existe (ej. v1634567890/)
            if (path.matches("v\\d+/.*")) {
                path = path.replaceFirst("v\\d+/", "");
            }

            // Eliminamos la extensión (.jpg, .epub, etc.)
            int dotIndex = path.lastIndexOf('.');
            if (dotIndex != -1) {
                path = path.substring(0, dotIndex);
            }
            return path;
        } catch (Exception e) {
            // Retorno de null en caso de error durante el parsing
            return null;
        }
    }

}

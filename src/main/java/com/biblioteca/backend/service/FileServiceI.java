package com.biblioteca.backend.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Interfaz para la gestión de archivos multimedia.
 * <p>
 * Abstrae la lógica de almacenamiento permitiendo cambiar en el futuro
 * entre almacenamiento local, Cloudinary, AWS S3, etc., sin afectar al controlador.
 * </p>
 */
public interface FileServiceI {

    /**
     * Sube un archivo al proveedor de almacenamiento configurado.
     * @param file El archivo binario recibido desde el cliente.
     * @return La URL pública y segura (HTTPS) para acceder al archivo subido.
     * @throws IOException Si ocurre un error durante la lectura o subida del archivo.
     */
    String uploadFile(MultipartFile file) throws IOException;

    /**
     * Elimina un archivo del almacenamiento remoto utilizando su URL pública.
     * <p>
     * Este método es fundamental para evitar la acumulación de archivos huérfanos en la nube
     * cuando se actualizan metadatos de un libro o se elimina un registro de la biblioteca.
     * </p>
     * @param fileUrl URL completa del recurso que se desea eliminar.
     */
    void deleteFile(String fileUrl);

}

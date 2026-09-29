package com.clubdeportivo.service;

import org.springframework.web.multipart.MultipartFile;

/** Contrato de almacenamiento de archivos (fotos de rostro de socios y familiares). */
public interface FileStorageService {

    /**
     * Valida y guarda una foto. Devuelve el nombre generado del archivo (para persistir en
     * {@code Persona.fotoRostroUrl}), o {@code null} si {@code archivo} viene vacío/ausente.
     *
     * @throws com.clubdeportivo.exception.ArchivoInvalidoException si el archivo no es un JPEG/PNG
     *                                                               válido o supera el tamaño permitido
     */
    String guardar(MultipartFile archivo);

    /** Elimina un archivo guardado (usado al reemplazar una foto). No falla si ya no existe. */
    void eliminar(String nombreArchivo);

    /** Ruta absoluta en disco de un archivo, para servirlo desde el controlador. */
    java.nio.file.Path resolver(String nombreArchivo);
}

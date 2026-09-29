package com.clubdeportivo.service.impl;

import com.clubdeportivo.exception.ArchivoInvalidoException;
import com.clubdeportivo.service.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

/**
 * Guarda las fotos de rostro en el SISTEMA DE ARCHIVOS LOCAL, en la carpeta configurada por
 * {@code club.storage.ruta-fotos}.
 *
 * <p>Para migrar a un almacenamiento en la nube (S3, Azure Blob, etc.) basta con escribir OTRA
 * implementación de {@link FileStorageService} y anotarla con {@code @Service}: el resto del
 * sistema (SocioService, controladores) depende solo de la interfaz.
 */
@Slf4j
@Service
public class FileStorageServiceImpl implements FileStorageService {

    /** Tipos MIME aceptados. Se valida tanto el content-type declarado como el contenido real. */
    private static final Set<String> TIPOS_PERMITIDOS = Set.of("image/jpeg", "image/png");

    private final Path directorioBase;
    private final long maxBytes;

    public FileStorageServiceImpl(@Value("${club.storage.ruta-fotos}") String rutaFotos,
                                  @Value("${club.storage.max-bytes}") long maxBytes) {
        this.directorioBase = Path.of(rutaFotos).toAbsolutePath().normalize();
        this.maxBytes = maxBytes;
        try {
            Files.createDirectories(this.directorioBase);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo crear el directorio de fotos: " + this.directorioBase, e);
        }
    }

    @Override
    public String guardar(MultipartFile archivo) {
        if (archivo == null || archivo.isEmpty()) {
            return null; // La foto es opcional en algunos flujos (p. ej. editar sin cambiarla)
        }
        if (archivo.getSize() > maxBytes) {
            throw new ArchivoInvalidoException("La foto supera el tamaño máximo permitido (2 MB)");
        }
        String contentType = archivo.getContentType();
        if (contentType == null || !TIPOS_PERMITIDOS.contains(contentType)) {
            throw new ArchivoInvalidoException("Formato no permitido. Solo se aceptan imágenes JPEG o PNG");
        }

        // Validacion de CONTENIDO real: un archivo .exe renombrado a .jpg no supera esta lectura,
        // porque ImageIO intenta decodificar los bytes como una imagen de verdad.
        String extension = contentType.equals("image/png") ? ".png" : ".jpg";
        try (InputStream in = archivo.getInputStream()) {
            BufferedImage imagen = ImageIO.read(in);
            if (imagen == null) {
                throw new ArchivoInvalidoException("El archivo no es una imagen válida");
            }
        } catch (IOException e) {
            throw new ArchivoInvalidoException("No se pudo leer el archivo de imagen");
        }

        // Nombre aleatorio: evita colisiones y que el usuario controle el nombre en disco (path traversal).
        String nombreArchivo = UUID.randomUUID() + extension;
        Path destino = directorioBase.resolve(nombreArchivo);
        try (InputStream in = archivo.getInputStream()) {
            Files.copy(in, destino, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.error("No se pudo guardar la foto {}: {}", nombreArchivo, e.getMessage());
            throw new ArchivoInvalidoException("No se pudo guardar la foto. Intenta nuevamente");
        }
        return nombreArchivo;
    }

    @Override
    public void eliminar(String nombreArchivo) {
        if (!StringUtils.hasText(nombreArchivo)) {
            return;
        }
        try {
            Files.deleteIfExists(resolver(nombreArchivo));
        } catch (IOException e) {
            log.warn("No se pudo eliminar la foto {}: {}", nombreArchivo, e.getMessage());
        }
    }

    @Override
    public Path resolver(String nombreArchivo) {
        // normalize() + startsWith evita path traversal (p. ej. "../../etc/passwd") aunque el nombre
        // ya se genera de forma controlada en guardar().
        Path ruta = directorioBase.resolve(nombreArchivo).normalize();
        if (!ruta.startsWith(directorioBase)) {
            throw new ArchivoInvalidoException("Nombre de archivo inválido");
        }
        return ruta;
    }
}

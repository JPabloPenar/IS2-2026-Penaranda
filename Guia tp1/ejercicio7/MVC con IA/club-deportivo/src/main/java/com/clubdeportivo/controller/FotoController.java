package com.clubdeportivo.controller;

import com.clubdeportivo.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Files;

/**
 * Sirve las fotos de rostro guardadas en disco. Es una ruta protegida (exige sesión, ver
 * SecurityConfig): las fotos de los socios no son públicas.
 */
@RestController
@RequiredArgsConstructor
public class FotoController {

    private final FileStorageService fileStorageService;

    @GetMapping("/fotos/{nombreArchivo}")
    public ResponseEntity<Resource> servirFoto(@PathVariable String nombreArchivo) {
        var ruta = fileStorageService.resolver(nombreArchivo);
        if (!Files.exists(ruta)) {
            return ResponseEntity.notFound().build();
        }
        MediaType tipo = nombreArchivo.toLowerCase().endsWith(".png") ? MediaType.IMAGE_PNG : MediaType.IMAGE_JPEG;
        return ResponseEntity.ok()
                .contentType(tipo)
                .header(HttpHeaders.CACHE_CONTROL, "private, max-age=3600")
                .body(new FileSystemResource(ruta));
    }
}

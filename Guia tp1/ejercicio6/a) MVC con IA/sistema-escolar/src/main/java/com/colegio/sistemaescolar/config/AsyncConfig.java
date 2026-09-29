package com.colegio.sistemaescolar.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Habilita la ejecución asíncrona ({@code @Async}).
 *
 * <p>Sin {@code @EnableAsync}, Spring ignora {@code @Async} y el método corre en el hilo
 * de la petición. Con él, el envío del correo de bienvenida se ejecuta en un hilo aparte
 * y el usuario no espera a que responda el servidor SMTP.
 */
@Configuration
@EnableAsync
public class AsyncConfig {
}

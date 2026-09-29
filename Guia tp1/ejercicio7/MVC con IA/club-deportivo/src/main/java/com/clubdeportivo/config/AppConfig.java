package com.clubdeportivo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.Clock;

/**
 * Configuracion general de la aplicacion.
 *
 * <p>{@code @Configuration}: clase que declara beans. {@code @EnableScheduling}: activa las tareas
 * {@code @Scheduled} (marcado diario de cuotas vencidas).
 */
@Configuration
@EnableScheduling
public class AppConfig {

    /**
     * Reloj inyectable. Los servicios usan {@code LocalDateTime.now(clock)} en lugar de
     * {@code LocalDateTime.now()}: asi las pruebas unitarias pueden fijar la fecha con
     * {@code Clock.fixed(...)} y los resultados son deterministas.
     */
    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}

package com.colegio.sistemaescolar.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Activa la auditoría de Spring Data JPA.
 *
 * <p>{@code @EnableJpaAuditing} habilita el procesamiento de {@code @CreatedDate},
 * {@code @LastModifiedDate}, {@code @CreatedBy} y {@code @LastModifiedBy}.
 * El atributo {@code auditorAwareRef} indica qué bean entrega el nombre del usuario
 * responsable de cada cambio.
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class JpaAuditingConfig {

    /**
     * Bean que identifica al usuario actual. El nombre del método ("auditorAware")
     * debe coincidir con {@code auditorAwareRef}.
     */
    @Bean
    public AuditorAware<String> auditorAware() {
        return new AuditorAwareImpl();
    }
}

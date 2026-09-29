package com.clubdeportivo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Activa la auditoria JPA. {@code @EnableJpaAuditing} habilita {@code @CreatedDate},
 * {@code @LastModifiedDate}, {@code @CreatedBy} y {@code @LastModifiedBy};
 * {@code auditorAwareRef} indica el bean que entrega el usuario responsable.
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class JpaAuditingConfig {

    /** El nombre del metodo debe coincidir con {@code auditorAwareRef}. */
    @Bean
    public AuditorAware<String> auditorAware() {
        return new AuditorAwareImpl();
    }
}

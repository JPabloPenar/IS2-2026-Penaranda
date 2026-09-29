package com.colegio.sistemaescolar.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * Proveedor del "auditor" para Spring Data JPA Auditing.
 *
 * <p>Cada vez que Hibernate inserta o actualiza una entidad auditable, Spring Data
 * llama a {@link #getCurrentAuditor()} para rellenar automáticamente los campos
 * {@code @CreatedBy} y {@code @LastModifiedBy}.
 *
 * <p>Se toma el usuario autenticado del {@link SecurityContextHolder}. Si no hay
 * sesión (por ejemplo, durante el registro público o al sembrar datos iniciales),
 * se usa el valor "sistema".
 */
public class AuditorAwareImpl implements AuditorAware<String> {

    /** Nombre usado cuando la operación no la realiza un usuario autenticado. */
    private static final String AUDITOR_POR_DEFECTO = "sistema";

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication autenticacion = SecurityContextHolder.getContext().getAuthentication();

        // Sin autenticación, no autenticado o usuario anónimo -> auditor "sistema".
        if (autenticacion == null
                || !autenticacion.isAuthenticated()
                || autenticacion instanceof AnonymousAuthenticationToken) {
            return Optional.of(AUDITOR_POR_DEFECTO);
        }

        // getName() devuelve el username, que en este sistema es el email del docente.
        return Optional.of(autenticacion.getName());
    }
}

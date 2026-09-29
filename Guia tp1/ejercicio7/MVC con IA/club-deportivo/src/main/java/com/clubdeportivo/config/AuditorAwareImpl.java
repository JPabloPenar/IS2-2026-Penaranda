package com.clubdeportivo.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * Proveedor del "auditor" para Spring Data JPA Auditing.
 *
 * <p>Al insertar o actualizar una entidad, Spring Data invoca {@link #getCurrentAuditor()} para
 * completar {@code @CreatedBy} y {@code @LastModifiedBy}. Se toma el usuario autenticado del
 * {@link SecurityContextHolder}; si no hay sesion (tareas programadas, carga inicial) se usa "sistema".
 */
public class AuditorAwareImpl implements AuditorAware<String> {

    private static final String AUDITOR_POR_DEFECTO = "sistema";

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication autenticacion = SecurityContextHolder.getContext().getAuthentication();
        if (autenticacion == null
                || !autenticacion.isAuthenticated()
                || autenticacion instanceof AnonymousAuthenticationToken) {
            return Optional.of(AUDITOR_POR_DEFECTO);
        }
        return Optional.of(autenticacion.getName()); // username del empleado
    }
}

package com.clubdeportivo.model;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Clase base abstracta de todas las entidades principales.
 *
 * <ul>
 *   <li>{@code @MappedSuperclass}: no genera tabla propia; sus columnas se heredan en cada tabla hija.</li>
 *   <li>{@code @EntityListeners(AuditingEntityListener.class)}: Spring Data completa los campos de
 *       auditoria automaticamente al insertar/actualizar.</li>
 * </ul>
 */
@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseAuditableEntity {

    /** Clave primaria autoincremental (IDENTITY es lo natural en MySQL). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedDate
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @LastModifiedDate
    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;

    /** Username del empleado que creo el registro (lo entrega AuditorAwareImpl). */
    @CreatedBy
    @Column(name = "creado_por", updatable = false, length = 100)
    private String creadoPor;

    @LastModifiedBy
    @Column(name = "modificado_por", length = 100)
    private String modificadoPor;
}

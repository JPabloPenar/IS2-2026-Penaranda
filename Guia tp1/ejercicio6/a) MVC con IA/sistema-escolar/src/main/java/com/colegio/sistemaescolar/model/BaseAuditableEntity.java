package com.colegio.sistemaescolar.model;

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
 * <p>{@code @MappedSuperclass}: no es una tabla por sí misma; sus campos se "heredan" como
 * columnas en la tabla de cada entidad hija (docentes, alumnos, notas...).
 *
 * <p>{@code @EntityListeners(AuditingEntityListener.class)}: registra el listener de Spring Data
 * que rellena automáticamente los campos de auditoría al insertar o actualizar.
 *
 * <p>{@code @Getter/@Setter} (Lombok) generan los métodos de acceso en tiempo de compilación.
 */
@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseAuditableEntity {

    /** Clave primaria autoincremental (IDENTITY es el modo natural de MySQL). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Fecha y hora de creación; la fija Spring Data una sola vez (updatable = false). */
    @CreatedDate
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    /** Fecha y hora de la última modificación; se actualiza en cada cambio. */
    @LastModifiedDate
    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;

    /** Usuario (email) que creó el registro, obtenido de {@code AuditorAwareImpl}. */
    @CreatedBy
    @Column(name = "creado_por", updatable = false, length = 150)
    private String creadoPor;

    /** Usuario (email) que realizó la última modificación. */
    @LastModifiedBy
    @Column(name = "modificado_por", length = 150)
    private String modificadoPor;
}

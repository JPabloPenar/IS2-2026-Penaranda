package com.clubdeportivo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Empleado del club que opera el sistema (administrador o recepcion).
 * No tiene relacion con {@link Socio}: son conceptos distintos (quien gestiona vs. quien es gestionado).
 *
 * <p>{@code @Entity}: Hibernate la mapea a una tabla. {@code @Table}: nombre de tabla y unicidad del username.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "usuarios",
        uniqueConstraints = @UniqueConstraint(name = "uk_usuario_username", columnNames = "username"))
public class Usuario extends BaseAuditableEntity {

    @Column(nullable = false, length = 50)
    private String username;

    @Column(nullable = false, length = 80)
    private String nombre;

    @Column(nullable = false, length = 80)
    private String apellido;

    @Column(nullable = false, length = 150)
    private String email;

    /** Hash BCrypt (60 caracteres). NUNCA texto plano. */
    @Column(nullable = false, length = 100)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Rol rol = Rol.ROLE_RECEPCION;

    @Column(nullable = false)
    private boolean activo = true;
}

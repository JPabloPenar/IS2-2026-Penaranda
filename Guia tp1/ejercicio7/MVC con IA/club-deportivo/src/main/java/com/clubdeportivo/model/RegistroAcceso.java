package com.clubdeportivo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Registro de un intento de entrada o salida en la porteria. Se guardan TAMBIEN los intentos
 * rechazados (permitido = false), lo que permite auditar quien intento ingresar sin cuota al dia.
 *
 * <p>Es la tabla que mas crece: por eso tiene indices por (persona, fecha) para hallar el ultimo
 * acceso y por fecha para los listados recientes.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "registros_acceso",
        indexes = {
                @Index(name = "idx_acceso_persona_fecha", columnList = "persona_id, fecha_hora"),
                @Index(name = "idx_acceso_fecha", columnList = "fecha_hora")
        })
public class RegistroAcceso extends BaseAuditableEntity {

    /** Persona (Socio o Familiar) que paso por el molinete. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "persona_id", nullable = false)
    private Persona persona;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_acceso", nullable = false, length = 10)
    private TipoAcceso tipoAcceso;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    /** true si se dejo pasar; false si se rechazo. */
    @Column(nullable = false)
    private boolean permitido;

    /** Motivo, p. ej. "Acceso permitido - Cuota al dia" o "Rechazado - Cuota adeudada". */
    @Column(length = 200)
    private String observaciones;
}

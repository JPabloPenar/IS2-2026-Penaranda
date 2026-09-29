package com.colegio.sistemaescolar.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Calificación de un {@link Alumno} en una {@link Materia} durante un {@link Periodo}.
 *
 * <p>Restricción única: solo puede existir UNA nota por combinación alumno + materia + período.
 * Para tener varias evaluaciones por período habría que agregar un campo (p. ej. "evaluación").
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "notas",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_nota_alumno_materia_periodo",
                columnNames = {"alumno_id", "materia_id", "periodo"}))
public class Nota extends BaseAuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "alumno_id", nullable = false)
    private Alumno alumno;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "materia_id", nullable = false)
    private Materia materia;

    /** Docente que registró la nota (opcional para datos históricos). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "docente_id")
    private Docente docente;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Periodo periodo;

    /**
     * Calificación numérica. BigDecimal evita errores de redondeo de double/float.
     * precision = 4, scale = 2 -> hasta 99,99 (la escala 0-10 se valida en el DTO).
     */
    @Column(name = "valor_numerico", nullable = false, precision = 4, scale = 2)
    private BigDecimal valorNumerico;

    @Column(length = 500)
    private String comentarios;
}

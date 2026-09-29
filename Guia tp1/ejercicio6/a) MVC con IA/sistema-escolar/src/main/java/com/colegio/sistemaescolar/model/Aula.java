package com.colegio.sistemaescolar.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * Aula física (por ejemplo "Aula 101" o "Pabellón A") con una capacidad máxima de alumnos.
 * La regla de capacidad se valida en {@code AlumnoServiceImpl}.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "aulas",
        uniqueConstraints = @UniqueConstraint(name = "uk_aula_codigo", columnNames = "codigo"))
public class Aula extends BaseAuditableEntity {

    /** Código o número visible del aula. Único. */
    @Column(nullable = false, length = 40)
    private String codigo;

    /** Cantidad máxima de alumnos que caben en el aula. */
    @Column(nullable = false)
    private int capacidad;

    /** Alumnos asignados al aula (lado inverso de Alumno.aula). */
    @OneToMany(mappedBy = "aula", fetch = FetchType.LAZY)
    private Set<Alumno> alumnos = new HashSet<>();

    /** Docentes que enseñan en esta aula (lado inverso de Docente.aulas). */
    @ManyToMany(mappedBy = "aulas")
    private Set<Docente> docentes = new HashSet<>();
}

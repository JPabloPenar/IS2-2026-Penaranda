package com.colegio.sistemaescolar.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * Grado escolar (por ejemplo "1° de Primaria" o "5° de Secundaria").
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "grados")
public class Grado extends BaseAuditableEntity {

    @Column(nullable = false, length = 60)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private Nivel nivel;

    /**
     * Alumnos del grado (lado inverso de la relación 1:N).
     * {@code mappedBy = "grado"}: la clave foránea vive en la tabla alumnos, en el campo
     * {@code Alumno.grado}; este atributo solo permite navegar en sentido contrario.
     */
    @OneToMany(mappedBy = "grado", fetch = FetchType.LAZY)
    private Set<Alumno> alumnos = new HashSet<>();

    /** Docentes que enseñan en este grado (lado inverso de Docente.grados). */
    @ManyToMany(mappedBy = "grados")
    private Set<Docente> docentes = new HashSet<>();
}

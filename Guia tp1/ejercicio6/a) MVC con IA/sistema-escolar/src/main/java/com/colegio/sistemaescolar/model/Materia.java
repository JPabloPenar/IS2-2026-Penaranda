package com.colegio.sistemaescolar.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * Materia o asignatura (Matemática, Inglés, etc.).
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "materias",
        uniqueConstraints = @UniqueConstraint(name = "uk_materia_nombre", columnNames = "nombre"))
public class Materia extends BaseAuditableEntity {

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 300)
    private String descripcion;

    /** Docentes que imparten la materia (lado inverso de Docente.materias). */
    @ManyToMany(mappedBy = "materias")
    private Set<Docente> docentes = new HashSet<>();
}

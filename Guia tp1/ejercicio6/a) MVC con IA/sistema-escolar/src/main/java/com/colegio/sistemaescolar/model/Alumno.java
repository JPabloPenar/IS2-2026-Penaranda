package com.colegio.sistemaescolar.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Alumno del colegio. Pertenece a un {@link Grado} y tiene asignada un {@link Aula}.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "alumnos",
        uniqueConstraints = @UniqueConstraint(name = "uk_alumno_dni", columnNames = "dni"))
public class Alumno extends BaseAuditableEntity {

    @Column(nullable = false, length = 80)
    private String nombre;

    @Column(nullable = false, length = 80)
    private String apellido;

    /** DNI o matrícula. Único: dos alumnos no pueden compartirlo. */
    @Column(nullable = false, length = 20)
    private String dni;

    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private Genero genero;

    /**
     * Grado del alumno (N:1). {@code @JoinColumn} crea la clave foránea "grado_id".
     * LAZY: el grado no se consulta hasta que se accede a él, lo que evita consultas innecesarias.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "grado_id", nullable = false)
    private Grado grado;

    /** Aula asignada (N:1), clave foránea "aula_id". */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aula_id")
    private Aula aula;

    /**
     * Notas del alumno (1:N). {@code CascadeType.REMOVE}: al eliminar un alumno también se
     * eliminan sus notas, evitando violaciones de clave foránea.
     */
    @OneToMany(mappedBy = "alumno", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<Nota> notas = new ArrayList<>();
}

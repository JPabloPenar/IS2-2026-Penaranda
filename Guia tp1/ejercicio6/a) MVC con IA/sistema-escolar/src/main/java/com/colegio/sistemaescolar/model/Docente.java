package com.colegio.sistemaescolar.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Docente del colegio. Además de ser una persona, es el USUARIO del sistema:
 * su {@code email} funciona como nombre de usuario (username) para el login.
 *
 * <p>{@code @Entity}: Hibernate la mapea a una tabla. {@code @Table}: define el nombre de la
 * tabla y una restricción de unicidad sobre el email (no pueden existir dos docentes con el mismo).
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "docentes",
        uniqueConstraints = @UniqueConstraint(name = "uk_docente_email", columnNames = "email"))
public class Docente extends BaseAuditableEntity {

    @Column(nullable = false, length = 80)
    private String nombre;

    @Column(nullable = false, length = 80)
    private String apellido;

    /** Se persiste el nombre del enum (MASCULINO, FEMENINO, OTRO). */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private Genero sexo;

    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    /** Email = username. Siempre se guarda en minúsculas. */
    @Column(nullable = false, length = 150)
    private String email;

    /** Hash BCrypt (60 caracteres). NUNCA la contraseña en texto plano. */
    @Column(nullable = false, length = 100)
    private String password;

    /** Rol de Spring Security. Debe empezar con "ROLE_". */
    @Column(nullable = false, length = 30)
    private String rol = "ROLE_DOCENTE";

    /** Permite deshabilitar a un docente sin borrarlo. */
    @Column(nullable = false)
    private boolean activo = true;

    /**
     * Materias que imparte el docente (relación N:M, tabla intermedia "docente_materia").
     * {@code @ManyToMany} + {@code @JoinTable} definen el lado propietario de la relación.
     * LAZY (por defecto en N:M): las materias solo se cargan cuando se usan.
     */
    @ManyToMany
    @JoinTable(name = "docente_materia",
            joinColumns = @JoinColumn(name = "docente_id"),
            inverseJoinColumns = @JoinColumn(name = "materia_id"))
    private Set<Materia> materias = new HashSet<>();

    /** Grados en los que enseña (tabla intermedia "docente_grado"). */
    @ManyToMany
    @JoinTable(name = "docente_grado",
            joinColumns = @JoinColumn(name = "docente_id"),
            inverseJoinColumns = @JoinColumn(name = "grado_id"))
    private Set<Grado> grados = new HashSet<>();

    /** Aulas en las que enseña (tabla intermedia "docente_aula"). */
    @ManyToMany
    @JoinTable(name = "docente_aula",
            joinColumns = @JoinColumn(name = "docente_id"),
            inverseJoinColumns = @JoinColumn(name = "aula_id"))
    private Set<Aula> aulas = new HashSet<>();
}

package com.clubdeportivo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Persona que puede pasar por el molinete: un {@link Socio} (titular) o un {@link Familiar}.
 *
 * <p><b>Herencia JPA JOINED:</b> los datos comunes viven en la tabla "personas" y los especificos de
 * cada subclase en "socios" y "familiares" (unidas por el id). Se eligio esta estrategia porque:
 * <ul>
 *   <li>{@code RegistroAcceso} necesita apuntar a "cualquier persona" con UNA sola clave foranea;</li>
 *   <li>el DNI es unico entre todas las personas del club (nadie puede ser socio y familiar a la vez);</li>
 *   <li>la busqueda del molinete por DNI consulta un unico indice.</li>
 * </ul>
 *
 * <p>Todas las personas pertenecen a un {@link GrupoFamiliar}: el titular es el que lo encabeza
 * y los familiares se suman a el. Asi la cuota al dia se resuelve igual para cualquier persona.
 */
@Getter
@Setter
@Entity
@Table(name = "personas",
        uniqueConstraints = @UniqueConstraint(name = "uk_persona_dni", columnNames = "dni"),
        indexes = @Index(name = "idx_persona_apellido_nombre", columnList = "apellido, nombre"))
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Persona extends BaseAuditableEntity {

    @Column(nullable = false, length = 80)
    private String nombre;

    @Column(nullable = false, length = 80)
    private String apellido;

    /** DNI sin puntos. Unico en todo el club. */
    @Column(nullable = false, length = 20)
    private String dni;

    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    @Column(length = 150)
    private String email;

    @Column(length = 20)
    private String telefono;

    /**
     * Nombre del archivo de la foto del rostro (p. ej. "3f2a....jpg"). Solo se guarda la referencia;
     * el binario esta en el sistema de archivos (ver FileStorageService). Es nullable para poder
     * cargar datos historicos, pero el alta desde la web la exige.
     */
    @Column(name = "foto_rostro_url", length = 120)
    private String fotoRostroUrl;

    /** Baja logica: una persona inactiva no puede ingresar, pero se conserva su historial. */
    @Column(nullable = false)
    private boolean activo = true;

    /**
     * Grupo familiar al que pertenece (N:1). LAZY: no se consulta hasta que se necesita.
     * Es nullable solo durante el alta del socio (primero se crea el socio y luego su grupo).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grupo_familiar_id")
    private GrupoFamiliar grupoFamiliar;

    /** "Socio" o "Familiar". Polimorfismo: cada subclase responde lo suyo (funciona tambien con proxies LAZY). */
    public abstract String getTipoPersona();

    /** Descripcion de la relacion con el grupo: "Titular" o el parentesco. */
    public abstract String getRelacion();
}

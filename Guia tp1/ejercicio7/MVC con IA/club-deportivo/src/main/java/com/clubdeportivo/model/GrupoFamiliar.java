package com.clubdeportivo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Grupo familiar: el socio titular y sus familiares. Es la unidad a la que se le factura
 * una cuota por periodo.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "grupos_familiares")
public class GrupoFamiliar extends BaseAuditableEntity {

    /**
     * Titular (1:1). Este lado es el propietario de la relacion: la clave foranea "titular_id" esta aqui,
     * con restriccion UNIQUE para que un socio encabece un unico grupo.
     */
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "titular_id", nullable = false, unique = true)
    private Socio titular;

    /** Todas las personas del grupo, incluido el titular (lado inverso de Persona.grupoFamiliar). */
    @OneToMany(mappedBy = "grupoFamiliar", fetch = FetchType.LAZY)
    private List<Persona> integrantes = new ArrayList<>();

    /** Cuotas emitidas al grupo (lado inverso de Cuota.grupoFamiliar). */
    @OneToMany(mappedBy = "grupoFamiliar", fetch = FetchType.LAZY)
    private List<Cuota> cuotas = new ArrayList<>();
}

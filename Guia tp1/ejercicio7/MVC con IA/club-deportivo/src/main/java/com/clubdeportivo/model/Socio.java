package com.clubdeportivo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/** Socio titular: encabeza un {@link GrupoFamiliar} y es el responsable de las cuotas. */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "socios")
public class Socio extends Persona {

    @Column(name = "fecha_alta", nullable = false)
    private LocalDate fechaAlta;

    @Override
    public String getTipoPersona() { return "Socio"; }

    @Override
    public String getRelacion() { return "Titular"; }
}

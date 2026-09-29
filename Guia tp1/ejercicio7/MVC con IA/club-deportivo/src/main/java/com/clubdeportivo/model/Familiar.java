package com.clubdeportivo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Miembro de la familia de un socio. Comparte el grupo familiar (y por lo tanto la cuota) del titular. */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "familiares")
public class Familiar extends Persona {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Parentesco parentesco;

    @Override
    public String getTipoPersona() { return "Familiar"; }

    @Override
    public String getRelacion() { return parentesco != null ? parentesco.getEtiqueta() : "Familiar"; }
}

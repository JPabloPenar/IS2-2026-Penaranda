package com.clubdeportivo.model;

/** Vinculo de un familiar con el socio titular. */
public enum Parentesco {
    CONYUGE("Cónyuge"),
    HIJO_A("Hijo/a"),
    PADRE_MADRE("Padre/Madre"),
    HERMANO_A("Hermano/a"),
    OTRO("Otro");

    private final String etiqueta;

    Parentesco(String etiqueta) { this.etiqueta = etiqueta; }

    public String getEtiqueta() { return etiqueta; }
}

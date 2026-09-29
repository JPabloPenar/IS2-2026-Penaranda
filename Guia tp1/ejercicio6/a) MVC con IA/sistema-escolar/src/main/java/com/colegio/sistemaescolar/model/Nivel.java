package com.colegio.sistemaescolar.model;

/** Nivel educativo al que pertenece un {@link Grado}. */
public enum Nivel {
    INICIAL("Inicial"),
    PRIMARIA("Primaria"),
    SECUNDARIA("Secundaria");

    private final String etiqueta;

    Nivel(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}

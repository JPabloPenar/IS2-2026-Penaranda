package com.clubdeportivo.model;

/** Sentido de un pasaje por el molinete de la porteria. */
public enum TipoAcceso {
    ENTRADA("Entrada"),
    SALIDA("Salida");

    private final String etiqueta;

    TipoAcceso(String etiqueta) { this.etiqueta = etiqueta; }

    public String getEtiqueta() { return etiqueta; }
}

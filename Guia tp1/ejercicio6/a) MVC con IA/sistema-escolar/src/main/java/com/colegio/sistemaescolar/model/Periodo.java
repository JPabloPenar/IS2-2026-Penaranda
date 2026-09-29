package com.colegio.sistemaescolar.model;

/**
 * Período o etapa del año escolar en el que se registra una {@link Nota}.
 * Para usar bimestres o semestres basta con cambiar los valores de este enum.
 */
public enum Periodo {
    PRIMER_TRIMESTRE("1.er trimestre"),
    SEGUNDO_TRIMESTRE("2.º trimestre"),
    TERCER_TRIMESTRE("3.er trimestre");

    private final String etiqueta;

    Periodo(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}

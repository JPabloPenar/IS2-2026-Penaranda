package com.colegio.sistemaescolar.model;

/**
 * Género de una persona (sirve tanto para el "sexo" del docente como para el "género" del alumno).
 * Se guarda en la base de datos como texto (@Enumerated STRING), no como número,
 * para que reordenar o agregar valores no corrompa datos existentes.
 */
public enum Genero {
    MASCULINO("Masculino"),
    FEMENINO("Femenino"),
    OTRO("Otro");

    private final String etiqueta;

    Genero(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    /** Texto legible que se muestra en las vistas. */
    public String getEtiqueta() {
        return etiqueta;
    }
}

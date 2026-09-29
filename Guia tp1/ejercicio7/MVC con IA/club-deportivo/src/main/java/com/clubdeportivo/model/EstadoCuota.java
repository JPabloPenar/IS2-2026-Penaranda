package com.clubdeportivo.model;

/**
 * Estado de una cuota. {@code claseCss} es la clase de la vista que pinta la etiqueta de estado,
 * asi las plantillas no necesitan condicionales.
 */
public enum EstadoCuota {
    PENDIENTE("Pendiente", "estado-pendiente"),
    PAGADA("Pagada", "estado-pagada"),
    VENCIDA("Vencida", "estado-vencida");

    private final String etiqueta;
    private final String claseCss;

    EstadoCuota(String etiqueta, String claseCss) {
        this.etiqueta = etiqueta;
        this.claseCss = claseCss;
    }

    public String getEtiqueta() { return etiqueta; }

    public String getClaseCss() { return claseCss; }
}

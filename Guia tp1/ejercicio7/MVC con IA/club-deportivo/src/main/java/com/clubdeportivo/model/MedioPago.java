package com.clubdeportivo.model;

/** Medios de pago soportados. Cada uno tiene su propio {@code ProcesadorPago} (patron Strategy). */
public enum MedioPago {
    EFECTIVO("Efectivo"),
    TRANSFERENCIA("Transferencia"),
    MERCADO_PAGO("Mercado Pago");

    private final String etiqueta;

    MedioPago(String etiqueta) { this.etiqueta = etiqueta; }

    public String getEtiqueta() { return etiqueta; }
}

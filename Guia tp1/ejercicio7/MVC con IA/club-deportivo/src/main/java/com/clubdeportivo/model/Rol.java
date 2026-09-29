package com.clubdeportivo.model;

/** Roles de los empleados. El prefijo ROLE_ es el que Spring Security espera para hasRole(...). */
public enum Rol {
    ROLE_ADMIN("Administrador"),
    ROLE_RECEPCION("Recepción");

    private final String etiqueta;

    Rol(String etiqueta) { this.etiqueta = etiqueta; }

    public String getEtiqueta() { return etiqueta; }
}

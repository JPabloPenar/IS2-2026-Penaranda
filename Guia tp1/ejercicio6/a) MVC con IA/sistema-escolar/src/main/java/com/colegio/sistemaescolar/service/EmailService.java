package com.colegio.sistemaescolar.service;

/** Contrato del envío de correos electrónicos. */
public interface EmailService {

    /**
     * Envía de forma ASÍNCRONA un correo HTML de bienvenida a un docente recién registrado.
     *
     * @param destinatario email del docente
     * @param nombre       nombre para personalizar el saludo
     */
    void enviarBienvenida(String destinatario, String nombre);
}

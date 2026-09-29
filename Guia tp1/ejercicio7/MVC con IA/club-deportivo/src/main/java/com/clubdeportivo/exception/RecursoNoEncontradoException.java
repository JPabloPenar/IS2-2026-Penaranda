package com.clubdeportivo.exception;

/** Se lanza cuando un recurso buscado (socio, cuota, foto...) no existe. Se traduce en un 404. */
public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String recurso, Long id) {
        super(recurso + " con id " + id + " no fue encontrado.");
    }

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}

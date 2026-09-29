package com.clubdeportivo.exception;

/** Archivo de foto rechazado (vacio, muy grande, formato no permitido o contenido falso). */
public class ArchivoInvalidoException extends ReglaNegocioException {

    /** El error se asocia al campo "foto" de los formularios. */
    public ArchivoInvalidoException(String mensaje) {
        super("foto", mensaje);
    }
}

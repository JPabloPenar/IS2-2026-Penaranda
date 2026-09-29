package com.colegio.sistemaescolar.exception;

/**
 * Se lanza cuando se busca una entidad por id y no existe (alumno, nota, grado...).
 * {@link GlobalExceptionHandler} la convierte en una página de error 404.
 */
public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String recurso, Long id) {
        super(recurso + " con id " + id + " no fue encontrado.");
    }
}

package com.colegio.sistemaescolar.exception;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Manejador global de excepciones para todos los controladores MVC.
 *
 * <p>{@code @ControllerAdvice} aplica este código a todos los {@code @Controller}, de modo
 * que no hay que repetir bloques try/catch para los errores comunes.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /** Muestra una página de error amigable con estado HTTP 404. */
    @ExceptionHandler(RecursoNoEncontradoException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String manejarNoEncontrado(RecursoNoEncontradoException ex, Model model) {
        model.addAttribute("titulo", "No encontramos lo que buscabas");
        model.addAttribute("mensaje", ex.getMessage());
        return "error/error";
    }
}

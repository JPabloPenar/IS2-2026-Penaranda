package com.clubdeportivo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

/**
 * Manejador global de excepciones de los controladores MVC. {@code @ControllerAdvice} aplica estos
 * metodos a TODOS los controladores, evitando repetir bloques try/catch.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNoEncontradoException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String manejarNoEncontrado(RecursoNoEncontradoException ex, Model model) {
        model.addAttribute("titulo", "No encontramos lo que buscabas");
        model.addAttribute("mensaje", ex.getMessage());
        return "error/error";
    }

    /**
     * Spring lanza esta excepcion ANTES de llegar al controlador cuando el archivo supera
     * {@code spring.servlet.multipart.max-file-size}; por eso no puede tratarse con BindingResult.
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.PAYLOAD_TOO_LARGE)
    public String manejarArchivoGrande(MaxUploadSizeExceededException ex, Model model) {
        model.addAttribute("titulo", "La foto es demasiado pesada");
        model.addAttribute("mensaje", "El tamaño máximo permitido es de 2 MB. Reduce la imagen y vuelve a intentarlo.");
        return "error/error";
    }
}

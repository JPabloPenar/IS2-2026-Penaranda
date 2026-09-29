package com.colegio.sistemaescolar.exception;

/**
 * Se lanza cuando se viola una regla de negocio (email duplicado, DNI repetido,
 * aula llena, contraseña actual incorrecta...).
 *
 * <p>Lleva el nombre del campo del formulario afectado para que el controlador pueda
 * asociar el mensaje a ese campo con {@code BindingResult.rejectValue(campo, ...)} y
 * Thymeleaf lo muestre junto al input correspondiente.
 */
public class ReglaNegocioException extends RuntimeException {

    private final String campo;

    public ReglaNegocioException(String campo, String mensaje) {
        super(mensaje);
        this.campo = campo;
    }

    /** Nombre del campo del DTO al que se asocia el error. */
    public String getCampo() {
        return campo;
    }
}

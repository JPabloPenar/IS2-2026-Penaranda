package com.clubdeportivo.exception;

/**
 * Violacion de una regla de negocio (DNI repetido, monto mayor al saldo, comprobante ya usado...).
 *
 * <p>Incluye el nombre del campo del formulario afectado: el controlador lo usa en
 * {@code BindingResult.rejectValue(campo, ...)} para que Thymeleaf muestre el mensaje junto al input.
 */
public class ReglaNegocioException extends RuntimeException {

    private final String campo;

    public ReglaNegocioException(String campo, String mensaje) {
        super(mensaje);
        this.campo = campo;
    }

    public String getCampo() {
        return campo;
    }
}

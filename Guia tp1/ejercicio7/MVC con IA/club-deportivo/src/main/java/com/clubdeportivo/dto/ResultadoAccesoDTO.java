package com.clubdeportivo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * DTO de SALIDA con el veredicto de un intento de acceso. Se envia a la vista como "flash attribute",
 * que Spring guarda en la sesion, por eso implementa {@link Serializable}.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResultadoAccesoDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private boolean permitido;
    private String mensaje;
    private String nombreCompleto;
    private String dni;
    private String foto;
    private String tipoAcceso;
    private String fechaHora;
}

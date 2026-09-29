package com.clubdeportivo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** DTO de SALIDA para el listado de ultimos accesos. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccesoResponseDTO {
    private Long id;
    private String nombreCompleto;
    private String dni;
    private String foto;
    private String tipoAcceso;
    private String fechaHora;
    private boolean permitido;
    private String observaciones;
}

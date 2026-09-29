package com.clubdeportivo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/** DTO de SALIDA para la ficha completa de un socio: sus datos, su grupo familiar y sus cuotas. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SocioDetalleDTO {
    private PersonaResponseDTO socio;
    private Long grupoFamiliarId;
    private List<PersonaResponseDTO> familiares;
    private List<CuotaResponseDTO> cuotas;
    private boolean alDia;
}

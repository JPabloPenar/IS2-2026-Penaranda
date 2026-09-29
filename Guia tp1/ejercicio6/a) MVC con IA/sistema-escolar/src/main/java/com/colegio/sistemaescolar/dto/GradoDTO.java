package com.colegio.sistemaescolar.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** DTO de solo lectura para poblar listas desplegables de grados. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GradoDTO {
    private Long id;
    private String nombre;
    private String nivel;
}

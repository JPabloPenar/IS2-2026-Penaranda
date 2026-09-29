package com.colegio.sistemaescolar.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** DTO de solo lectura para poblar listas desplegables de aulas. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AulaDTO {
    private Long id;
    private String codigo;
    private int capacidad;
}
